apktool-lib 1.4.4-1
===================
This library decodes the Android manifest (`AndroidManifest.xml`) from an APK. This is useful if you need to parse the manifest outside of an Android environment.

This is based on the [APK Tool][1] project.


Usage
=====

_Maven dependency_

<pre>
&lt;dependency>
  &lt;groupId>com.github.tony19&lt;/groupId>
  &lt;artifactId>apktool-lib&lt;/artifactId>
  &lt;version>1.4.4-1&lt;/version>
&lt;/dependency>
</pre>

_Steps_

 1. Add the `apktool-lib` dependency to your `pom.xml`.
 2. Parse XML events from `AXmlResourceParser` in a loop, as in the following code example:

<pre>
import brut.androlib.res.decoder.AXmlResourceParser;

//...

public void parseStream(InputStream stream) {
	AXmlResourceParser xpp = new AXmlResourceParser(stream);
	
	int eventType = -1;
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
}
</pre>


Download
========
[apktool-lib-1.4.4-1.jar](https://oss.sonatype.org/content/repositories/releases/com/github/tony19/apktool-lib/1.4.4-1/apktool-lib-1.4.4-1.jar)


Build
=====

To build, use Maven 2+:

    $ mvn package


License
=======
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


Changelog
=========

__1.4.4-1__ (25 July 2012)
 * Initial commit

[1]: https://github.com/brutall/brut.apktool
