# UIMA Utils

Utility classes for loading and saving CAS objects, combining UIMA TypeSytems, as well as internationalization (i18n) support.

### Contents

1. [Maven Coordinates](#maven-coordinates)
1. [Utility Methods](#utility-methods)
1. [TypeSystem Aggregator](#typesystem-aggregator)
1. [Internationalization](#internationalization)

## Maven Coordinates

```xml
<dependency>
    <groupId>gov.cdc.lappsgrid.uima</groupId>
    <artifactId>uima-utils</artifactId>
    <version>...</version>
</dependency>
```

**NOTE** Always check the `pom.xml` file for the lastest version of the `uima-utils`
package.

## Utility Methods

The `gov.cdc.lappsgrid.utils.Utils` class contains a number of handy static methods:

* `readString(URL url)` <br/>
 `readString(URL url, String charset)` <br/>
 `readString(File file)` <br/>
 `readString(File file, String charset)` <br/>
 `readString(String path)` <br/>
 `readString(String path, String charset)` <br/>
 `readString(InputStream stream)` <br/>
 `readString(InputStream stream, String charset)` <br/> Reads the input source into a String object.  If a character encoding is not specified then *UTF-8* is assumed.
* `loadCas(URL)` <br/>
`loadCas(File)` <br/>
`loadCas(String)` <br/> Loads a CAS from the given location.  The `String` variant assumes the String is the full path to the CAS file.  The input can be in either XCAS or XMI format.
* `createCas()` <br/> Creates a new CAS object using the default aggregate type system.
* `save(TypeSystemDescription tsd, File file)` <br/>
 `save(TypeSystemDescription tsd, String path)` <br/> Saves the TypeSystemDescription to a file.  The `String` variant assumes the `path` is the full path, including file name, of the file to be written. All directories in the path must already exists or a `UtilsException` will be thrown.
* `toXcas(CAS)` <br/>
 `toXmi(CAS)` <br />
 Returns a String containing the XML representation of the CAS object
 
The `loadCas()` and `createCas()` create CAS objects using the default aggregate type system provided by `TypeSystemAggregator.defaultTypeSystem()`.

## TypeSystem Aggregator

### Default TypeSystem

The UIMA framework provides the API `CasCreationUtils.mergeTypeSystems(List)` to merge a list of TypeSystemDescription objects into a single TypeSystemDescription.  The TypeSystemAggregator class encapsulates the `CasCreationUtils` method to merge the known UIMA type systems.  Currently the *known* type systems are:

1. cTAKES
1. OpenNLP
1. VAERS

```java
TypeSystemDescription tsd = TypeSystemAggregator.defaultTypeSystem();
```

To add new TypeSystemDescriptions copy the XML file to `src/main/resources/typesystems/`. Alternatively the type system description files can be loaded from an external directory and then specified with the environment variable TYPESYSTEM_DIRECTORY.

### Custom TypeSystems

Instances of the TypeSystemAggregator can be used to combine any number of type system descriptions into a single type system.

``` 
TypeSystemAggregator aggregator = new TypeSystemAggregator();
aggregator.add(new File("typesystem1.xml");
aggregator.add(new File("typesystem2.xml");
...
aggregator.add(new File("typesystemN.xml");
TypeSystemDescription tsd = aggregator.get();
``` 

## Internationalization

Use the `gov.cdc.lappsgrid.utils.i18n.BaseTranslation` class to create classes that hold `static final String` messages that will be displayed by an application and that may need to be translated into other languages.

The typical approach to internationalization (i18n) in Java applications is to use a `ResourceBundle` that loads the messages from a `.properties` file.  The problem with this approach is that we replace one hard coded string (the message) with another hard coded string (the key).

```
ResourceBundle messages = ResourceBundle.getBundle("MyMessages");
System.out.println(messages.getString("helo_world"));
``` 

In the above example there is a typo in the hard coded key (*helo_world* instead of *hello_world*). The other problem is that our IDE can not help us with auto-completion of the key values as they are just String values and could be anything. 

The `BaseTranslation` class addresses these problems and allows the best of both worlds.

1. The Strings to be displayed are defined as `final String` values that an IDE will know about so we can use auto-completion.  The IDE will also alert us to any typos we may make.
1. The String values are still backed by a ResourceBundle and will be loaded from a `.properties` file if it exists.

### Using BaseTranslation

Due to the way Java loads and initialized `static final String` objects we need to first create a class that contains the `final String` fields.  The fields are initialized to `null` and then default values are provided with the `@Default` annotation.

```java
public class Labels extends BaseTranslation {
    @Default("Hello")
    public final String HELLO = null;
    @Default("Goodbye")
    public final String GOODBYE = null;
    
    public Labels() {
        init();  // <-- very important!
    }
}

public class ErrorMessages extends BaseTranslation {
    @Default("Something bad happened.")
    public final String OPPS = null;
}
```

Then create a *wrapper* class that contains `static final` instances of all the message classes.

```java
public class Messages {
    public static final Labels Labels = new Labels();
    public static final ErrorMessages Error = new ErrorMessags();
}
```

Profit:

```java
System.out.println(Messages.Error.OPPS);
```

### NOTES

It is important to call the `init()` method in the default constructor of any class that extends `BaseTranslation` as this is when the fields will be initialized with values from the `.properties` file.

To generate the `.properties` file to create other translations call the `BaseTranslation.save()` method.  This is typically done in the `main` method of the wrapper class.

```java
public class Messages {
    public static final ErrorMessages Error = new ErrorMessages();
    
    public static void main(String[] args) 
    {
        try
        {
            new ErrorMessages().save(true);
        }
        catch (InstantiationException | IllegalAccessException | IOException e)
        {
            e.printStackTrace();
        }
    }
}
```



