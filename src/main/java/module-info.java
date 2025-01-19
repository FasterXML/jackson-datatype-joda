// Joda datatype module Main artifact Module descriptor
module tools.jackson.datatype.joda
{
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires transitive tools.jackson.databind;
    requires org.joda.time;

    exports tools.jackson.datatype.joda;
    exports tools.jackson.datatype.joda.cfg;
    exports tools.jackson.datatype.joda.deser;
    exports tools.jackson.datatype.joda.deser.key;
    exports tools.jackson.datatype.joda.ser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.joda.JodaModule;
}
