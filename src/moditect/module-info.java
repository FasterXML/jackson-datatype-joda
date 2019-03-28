
// Generated 27-Mar-2019 using Moditect maven plugin
module com.fasterxml.jackson.datatype.joda {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires joda.time;

    exports com.fasterxml.jackson.datatype.joda;
    exports com.fasterxml.jackson.datatype.joda.cfg;
    exports com.fasterxml.jackson.datatype.joda.deser;
    exports com.fasterxml.jackson.datatype.joda.deser.key;
    exports com.fasterxml.jackson.datatype.joda.ser;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.joda.JodaModule;
}
