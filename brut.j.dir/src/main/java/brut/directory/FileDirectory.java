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

package brut.directory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;

public class FileDirectory extends AbstractDirectory {
    private File mDir;

    public FileDirectory(String dir) throws DirectoryException {
        this(new File(dir));
    }

    public FileDirectory(File dir) throws DirectoryException {
        super();
        if (! dir.isDirectory()) {
            throw new DirectoryException("file must be a directory: " + dir);
        }
        mDir = dir;
    }

    @Override
    protected AbstractDirectory createDirLocal(String name) throws DirectoryException {
        File dir = new File(generatePath(name));
        dir.mkdir();
        return new FileDirectory(dir);
    }

    @Override
    protected InputStream getFileInputLocal(String name) throws DirectoryException {
        try {
            return new FileInputStream(generatePath(name));
        } catch (FileNotFoundException e) {
            throw new DirectoryException(e);
        }
    }

    @Override
    protected OutputStream getFileOutputLocal(String name) throws DirectoryException {
        try {
            return new FileOutputStream(generatePath(name));
        } catch (FileNotFoundException e) {
            throw new DirectoryException(e);
        }
    }

    @Override
    protected void loadDirs() {
        loadAll();
    }

    @Override
    protected void loadFiles() {
        loadAll();
    }

    @Override
    protected void removeFileLocal(String name) {
        new File(generatePath(name)).delete();
    }
    
    private String generatePath(String name) {
        return getDir().getPath() + separator + name;
    }

    private void loadAll() {
        mFiles = new LinkedHashSet<String>();
        mDirs = new LinkedHashMap<String, AbstractDirectory>();
        
        File[] files = getDir().listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isFile()) {
                mFiles.add(file.getName());
            } else {
                // IMPOSSIBLE_EXCEPTION
                try {
                    mDirs.put(file.getName(), new FileDirectory(file));
                } catch (DirectoryException e) {}
            }
        }
    }

    private File getDir() {
        return mDir;
    }
}
