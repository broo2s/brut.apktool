/**
 *  Copyright 2011 Ryszard Wiśniewski <brut.alll@gmail.com>
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Stack;

import org.junit.*;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/**
 * Tests the {@link AXmlResourceParser} class
 * 
 * @author Anthony Trinh
 */
public class AXmlResourceParserTest {
	private int holderForStartAndLength[] = new int[2];
	private Document document;
    private Element root;
    private Stack<Element> stack = new Stack<Element>();
    private static final String GOLD = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<manifest versionCode=\"\" versionName=\"1.0\" package=\"com.example.helloandroid\"><uses-sdk minSdkVersion=\"\" targetSdkVersion=\"\"/><uses-permission name=\"android.permission.INTERNET\"/><application theme=\"\" label=\"\" icon=\"\" debuggable=\"\"><activity label=\"\" name=\".ItemListActivity\"><intent-filter><action name=\"android.intent.action.MAIN\"/><category name=\"android.intent.category.LAUNCHER\"/></intent-filter></activity><activity label=\"\" name=\".ItemDetailActivity\"><meta-data name=\"android.support.PARENT_ACTIVITY\" value=\".ItemListActivity\"/></activity></application><logback><configuration debug=\"true\"><appender name=\"LOGCAT\" class=\"ch.qos.logback.classic.android.LogcatAppender\"><tagEncoder><pattern>%logger{0}</pattern></tagEncoder><encoder><pattern>[%method] &gt; %msg%n</pattern></encoder></appender><root level=\"TRACE\"><appender-ref ref=\"LOGCAT\"/></root></configuration></logback></manifest>";
    
	@Before
	public void beforeClass() {
		InputStream stream = getClass().getResourceAsStream("/AndroidManifest.xml.res");
		AXmlResourceParser xpp = new AXmlResourceParser(stream);
		
		int eventType = -1;
		try {
			while ((eventType = xpp.next()) > -1) {
				if (XmlPullParser.START_DOCUMENT == eventType) {
					startDocument(xpp);
				} else if (XmlPullParser.END_DOCUMENT == eventType) {
					endDocument();
					break;
				} else if (XmlPullParser.START_TAG == eventType) {
					startElement(xpp);
				} else if (XmlPullParser.END_TAG == eventType) {
					endElement(xpp);
				} else if (XmlPullParser.TEXT == eventType) {
					characters(xpp);
				}
			}
		} catch (XmlPullParserException e) {
			Assert.fail("failed to parse XML resource: " + e.getLocalizedMessage());
		} catch (IOException e) {
			Assert.fail("failed to parse XML resource: " + e.getLocalizedMessage());
		}
	}

	@Test
	public void testDocWasParsed() {
		assertNotNull(document);
	}
	
	@Test
	public void testDocContains() {
//		System.out.println("asXml=" + document.asXML());
		assertEquals(GOLD, document.asXML());
	}
	
	private void startDocument(XmlPullParser xpp) {
//		System.out.println("start doc");
		document = DocumentHelper.createDocument();
	}

	private void endDocument() {
//		System.out.println("end doc");
	}

	private void startElement(XmlPullParser xpp) {
		String name = xpp.getName();
		
//		System.out.printf("start element: ns=%s name=%s\n", xpp.getNamespace(), name);
		
		if (root == null) {
			root = document.addElement(name);
		} else {
			stack.push(root);
			root = root.addElement(name);
		}
		
		for (int i = 0; i < xpp.getAttributeCount(); i++) {
			String qname = xpp.getAttributeName(i);
			String value = xpp.getAttributeValue(i);
			
//			System.out.println(
//					i 
//					+ " ns=" + xpp.getAttributeNamespace(i) 
//					+ " name=" + qname 
//					+ " val=" + value
//					);
			
			root.addAttribute(qname, value);
		}
	}

	private void endElement(XmlPullParser xpp) {
//		System.out.println("end element: " + xpp.getName());
		root = stack.isEmpty() ? null : stack.pop();
	}

	private void characters(XmlPullParser xpp) {

		char ch[] = xpp.getTextCharacters(holderForStartAndLength);
		int start = holderForStartAndLength[0];
		int length = holderForStartAndLength[1];
		
		String text = new String(ch, start, length);
//		System.out.println("characters: " + text);
		
		root.setText(text);
	}
}