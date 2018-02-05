# apktool-lib [![CircleCI branch](https://img.shields.io/circleci/project/tony19/apktool-lib/master.svg)](https://circleci.com/gh/tony19/logback-android)
<sup>v1.4.4-4</sup>

Overview
--------
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
-----
Parse XML events from `AXmlResourceParser` in a loop, as in the following code example:

```java
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
```


Download
--------
_Gradle_

```groovy
dependencies {
  compile 'com.github.tony19:apktool-lib:1.4.4-4'
}
```

Build
-----
Use these commands to create the AAR:

    git clone git://github.com/tony19/apktool-lib.git
    cd apktool-lib
    scripts/makejar.sh

The file is output to: `./build/apktool-lib-1.4.4-4-debug.aar`

License
=======
[Apache License 2.0](http://www.apache.org/licenses/LICENSE-2.0)

 [1]: https://github.com/brutall/brut.apktool
 [2]: http://developer.android.com/reference/android/content/res/AssetManager.html#openXmlResourceParser(java.lang.String)