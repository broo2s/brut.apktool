/**
 *  Copyright 2014 Ryszard Wiśniewski <brut.alll@gmail.com>
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package brut.androlib.res.decoder;

import brut.androlib.AndrolibException;
import brut.androlib.err.CantFind9PatchChunk;
import brut.util.ExtDataInput;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;

import org.apache.commons.io.IOUtils;

/**
 * @author Ryszard Wiśniewski <brut.alll@gmail.com>
 */
public class Res9patchStreamDecoder implements ResStreamDecoder {
    @Override
    public void decode(InputStream in, OutputStream out)
            throws AndrolibException {
        try {
            byte[] data = IOUtils.toByteArray(in);

            BufferedImage im = ImageIO.read(new ByteArrayInputStream(data));
            int w = im.getWidth(), h = im.getHeight();

            ImageTypeSpecifier its = ImageTypeSpecifier.createFromRenderedImage( im );
            BufferedImage im2 = its.createBufferedImage( w+2, h+2 );

            im2.getRaster().setRect(1, 1, im.getRaster());
            NinePatch np = getNinePatch(data);
            drawHLine(im2, h + 1, np.padLeft + 1, w - np.padRight);
            drawVLine(im2, w + 1, np.padTop + 1, h - np.padBottom);

            int[] xDivs = np.xDivs;
            for (int i = 0; i < xDivs.length; i += 2) {
                drawHLine(im2, 0, xDivs[i] + 1, xDivs[i + 1]);
            }

            int[] yDivs = np.yDivs;
            for (int i = 0; i < yDivs.length; i += 2) {
                drawVLine(im2, 0, yDivs[i] + 1, yDivs[i + 1]);
            }

            ImageIO.write(im2, "png", out);
        } catch (IOException ex) {
            throw new AndrolibException(ex);
        } catch (NullPointerException ex) {
            // In my case this was triggered because a .png file was
            // containing a html document instead of an image.
            // This could be more verbose and try to MIME ?
            throw new AndrolibException(ex);
        }
    }

    private NinePatch getNinePatch(byte[] data) throws AndrolibException,
            IOException {
        ExtDataInput di = new ExtDataInput(new ByteArrayInputStream(data));
        find9patchChunk(di);
        return NinePatch.decode(di);
    }

    private void find9patchChunk(DataInput di) throws AndrolibException,
            IOException {
        di.skipBytes(8);
        while (true) {
            int size;
            try {
                size = di.readInt();
            } catch (IOException ex) {
                throw new CantFind9PatchChunk("Cant find nine patch chunk", ex);
            }
            if (di.readInt() == NP_CHUNK_TYPE) {
                return;
            }
            di.skipBytes(size + 4);
        }
    }

    private void drawHLine(BufferedImage im, int y, int x1, int x2) {
        for (int x = x1; x <= x2; x++) {
            im.setRGB(x, y, NP_COLOR);
        }
    }

    private void drawVLine(BufferedImage im, int x, int y1, int y2) {
        for (int y = y1; y <= y2; y++) {
            im.setRGB(x, y, NP_COLOR);
        }
    }

    private static final int NP_CHUNK_TYPE = 0x6e705463; // npTc
    private static final int NP_COLOR = 0xff000000;

    private static class NinePatch {
        public final int padLeft, padRight, padTop, padBottom;
        public final int[] xDivs, yDivs;

        public NinePatch(int padLeft, int padRight, int padTop, int padBottom,
                         int[] xDivs, int[] yDivs) {
            this.padLeft = padLeft;
            this.padRight = padRight;
            this.padTop = padTop;
            this.padBottom = padBottom;
            this.xDivs = xDivs;
            this.yDivs = yDivs;
        }

        public static NinePatch decode(ExtDataInput di) throws IOException {
            di.skipBytes(1);
            byte numXDivs = di.readByte();
            byte numYDivs = di.readByte();
            di.skipBytes(1);
            di.skipBytes(8);
            int padLeft = di.readInt();
            int padRight = di.readInt();
            int padTop = di.readInt();
            int padBottom = di.readInt();
            di.skipBytes(4);
            int[] xDivs = di.readIntArray(numXDivs);
            int[] yDivs = di.readIntArray(numYDivs);

            return new NinePatch(padLeft, padRight, padTop, padBottom, xDivs,
                    yDivs);
        }
    }
}