Project to build Jackson (http://jackson.codehaus.org) module (jar)
to support JSON serialization and deserialization of
Joda (http://joda-time.sourceforge.net/) data types.

## Status

As of version 2.0 module is usable and relatively extensive (more so than in-built support 1.9 had).

## Usage

### Maven dependency

To use module on Maven-based projects, use following dependency:

    <dependency>
      <groupId>com.fasterxml.jackson.datatype</groupId>
      <artifactId>jackson-datatype-joda</artifactId>
      <version>2.1.0</version>
    </dependency>    

(or whatever version is most up-to-date at the moment)

### Registering module


Like all standard Jackson modules (libraries that implement Module interface), registration is done as follows:

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());

after which functionality is available for all normal Jackson operations:
you can read JSON as Joda types, as well as write Joda values as JSON.

## More

See [Wiki](jackson-datatype-joda/wiki) for more information (javadocs, downloads).
