package tools.jackson.datatype.joda.failing;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import tools.jackson.datatype.joda.JodaTestBase;

import org.joda.time.*;

// [datatype-joda#146]: disable overwriting of timezone
public class DateTimeSerializationWithOffsets146Test extends JodaTestBase
{
    // [datatype-joda#146]
    public void testDateTimeSerializationWithOffsets146() throws Exception
    {
        final ObjectMapper MAPPER = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .build();
        final String inputStr = "2024-12-01T00:00:00+02:00";

        DateTime dateTime = DateTime.parse(inputStr);
        assertEquals(q(inputStr), MAPPER.writeValueAsString(dateTime));
    }
}
