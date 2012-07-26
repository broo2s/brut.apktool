apktool-lib 1.4.4-3
===================
This library decodes a given precompiled XML resource (such as `AndroidManifest.xml`)
from an APK without requiring an Android application context.
 
Normally, precompiled (as opposed to raw) XML can be accessed from an Android application 
using [`AssetManager.openXmlResourceParser()`][2]. In order to get an instance of 
`AssetManager`, the caller must have access to the Android application context, which
is normally unavailable for libraries. A possible workaround is to pass the context (e.g.,
via the consumer's constructor) to libraries that need context-specific classes, but
that might be infeasible or undesirable in some applications, including ones that need
access to the precompiled XML before the context is initialized. `apktool-lib` enables 
libraries to read precompiled XML without an application context or the Android `AssetManager`.

This is based on the [APK Tool][1] project.


Usage
=====

_Maven dependency_

<pre>
&lt;dependency>
  &lt;groupId>com.github.tony19&lt;/groupId>
  &lt;artifactId>apktool-lib&lt;/artifactId>
  &lt;version>1.4.4-3&lt;/version>
&lt;/dependency>
</pre>

_Steps_

 1. Add the `apktool-lib` dependency to your `pom.xml`.
 2. Parse XML events from `AXmlResourceParser` in a loop, as in the following code example:

<pre>
import brut.androlib.res.decoder.AXmlResourceParser;

//...

// called from a library, used by an Android application
public void readManifest() {
	InputStream manifestStream = getClassLoader().getResourceAsStream("AndroidManifest.xml");
	parseXmlStream(manifestStream);
}

public void parseXmlStream(InputStream stream) {
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
[apktool-lib-1.4.4-3.jar](https://oss.sonatype.org/content/repositories/releases/com/github/tony19/apktool-lib/1.4.4-3/apktool-lib-1.4.4-3.jar)


Build
=====

To build, use Maven 2+:

    $ mvn package


License
=======
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)


Changelog
=========

__1.4.4-3__ (26 July 2012)
 * Fix attribute decoding

__1.4.4-2__ (26 July 2012)
 * Remove debug info and unused resource to reduce JAR size

__1.4.4-1__ (25 July 2012)
 * Initial commit (based on [apktool][1] v1.4.4)

[1]: https://github.com/brutall/brut.apktool
[2]: http://developer.android.com/reference/android/content/res/AssetManager.html#openXmlResourceParser(java.lang.String)
