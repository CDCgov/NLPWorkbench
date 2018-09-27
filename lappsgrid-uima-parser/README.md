# UIMA Parser

An XML parser that can parse UIMA XML (XCAS or XMI) and generate a LIF Container object.

## Maven Coordinates

```xml
<dependency>
    <groupId>gov.cdc.lappsgrid.uima</groupId>
    <artifactId>parser</artifactId>
    <version>...</version>
</dependency>
```

**NOTE** Always check the `pom.xml` file for the latest version of the `parser` package.

## Usage

The `UimaParser` class has a single, no args, constructor and a number of `parse` methods that accept a `File`, `URL`, `InputStream`, or a `String` with or without a character set encoding.  If no character set encoding is specified it is assumed the input uses `UTF-8`.

```java

UimaParser parser = new UimaParser();
Container container = parser.parse(new File("/path/to/someCasObject.xmi"));
String json = Serializer.toPrettyJson(container);
System.out.println(json);
```

**Notes**

The `UimaParser` does not attempt to translate UIMA types into the LAPPS Grid vocabulary.  The UIMA type names are used *as-is* for the LIF `@type` features.  Similarly the XML attributes are specified as the `features` of the LIF `Annotation` object.  This allows for a relatively straight-forward conversion between XCAS/XMI and LIF, at least at the syntactic level.


