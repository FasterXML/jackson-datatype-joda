Project to build Jackson (http://jackson.codehaus.org) module (jar)
to support JSON serialization and deserialization of
Joda (http://joda-time.sourceforge.net/) data types.

[![Build Status](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-joda-master/badge/icon)](https://fasterxml.ci.cloudbees.com/job/jackson-datatype-joda-master/)

# Usage

Modules are registered through ObjectMapper, like so:

    ObjectMapper mapper = new ObjectMapper();
    mapper.registerModule(new JodaModule());

after which you can read JSON as Joda types, as well as write Joda values as JSON.
It's really that simple; convenient and efficient.

Note that core Jackson does offer some level of support for Joda types; but this module is planned to take over these responsibilities; and possibly so that Jackson 2.0 might not have any Joda dependencies.

# Current status

Just getting started...
