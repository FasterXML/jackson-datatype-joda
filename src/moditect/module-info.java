// Last changes:
//
// * 2020-11-25 change joda requires from "joda.time" to "org.joda.time"
//     (Joda 2.10 now has automatic module name)
//    
module tools.jackson.datatype.joda {
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires org.joda.time;

    exports tools.jackson.datatype.joda;
    exports tools.jackson.datatype.joda.cfg;
    exports tools.jackson.datatype.joda.deser;
    exports tools.jackson.datatype.joda.deser.key;
    exports tools.jackson.datatype.joda.ser;

    provides tools.jackson.databind.JacksonModule with
        tools.jackson.datatype.joda.JodaModule;
}
