// Initially generated using Moditect maven plugin
// Last changes:
//
// * 2020-11-25 change joda requires from "joda.time" to "org.joda.time"
//     (Joda 2.10 now has automatic module name)
//    
module com.fasterxml.jackson.datatype.joda {
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires org.joda.time;

    exports com.fasterxml.jackson.datatype.joda;
    exports com.fasterxml.jackson.datatype.joda.cfg;
    exports com.fasterxml.jackson.datatype.joda.deser;
    exports com.fasterxml.jackson.datatype.joda.deser.key;
    exports com.fasterxml.jackson.datatype.joda.ser;

    provides com.fasterxml.jackson.databind.Module with
        com.fasterxml.jackson.datatype.joda.JodaModule;
}
