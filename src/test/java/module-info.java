// Joda datatype module (unit) Test Module descriptor
module tools.jackson.datatype.joda
{
    // Since we are not split from Main artifact, will not
    // need to depend on Main artifact -- but need its dependencies

    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.core;
    requires tools.jackson.databind;
    requires org.joda.time;

    // Additional test lib/framework dependencies
    requires org.junit.jupiter.api; // JUnit 5

    // Further, need to open up test packages for JUnit et al
    opens tools.jackson.datatype.joda;
    opens tools.jackson.datatype.joda.deser;
    opens tools.jackson.datatype.joda.failing;
    opens tools.jackson.datatype.joda.ser;
}
