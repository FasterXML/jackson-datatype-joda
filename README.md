Project to build [Jackson](http://jackson.codehaus.org) module (jar)
to support JSON serialization and deserialization of
[Joda](http://joda-time.sourceforge.net/) data types.

## Status

[![Build Status](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-joda-master/badge/icon)](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-joda-master/)

As of version 2.0 module is usable and relatively extensive.
Contributions are always welcome -- not all types are yet supported; and we may want to support even wider alternative
formats on input side.

## Usage

Since this module extends basic [Jackson databind](../jackson-databind) functionality, you may want to check out
documentation at [Jackson-docs](../jackson-docs) first.

### Maven dependency

To use module on Maven-based projects, use following dependency:

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-joda</artifactId>
  <version>2.3.1</version>
</dependency>    
```

(or whatever version is most up-to-date at the moment)

### Registering module

To use Joda datatypes with Jackson, you will first need to register the module first (same as
with all Jackson datatype modules):

```java
ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(new JodaModule());
```

### Reading and Writing Joda types

After registering Joda module, [Jackson Databind](../jackson-databind) will be able to write values
of supported Joda types as JSON (and other formats Jackson supports), and read Joda values
from same formats.

With JSON, for example, following would work

```java
public class Bean {
  public DateTime start;
}

final String INPUT_JSON = "{\"start\" : \"1972-12-28T12:00:01.000Z\"}";
Bean bean = mapper.readValue(INPUT_JSON, Bean.class);
```

and property `start` of Bean would have expected `DateTime` value.

Conversely, you can produce JSON (and other supported formats) simply with:

```java
String json = mapper.writeValueAsString(bean);
Assert.assertEquals(INPUT_JSON, json);
```

## More

See [Wiki](../../wiki) for more information (javadocs, downloads).
