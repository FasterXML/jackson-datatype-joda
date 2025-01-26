package tools.jackson.datatype.joda.failing;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.SerializationFeature;

import tools.jackson.datatype.joda.JodaTestBase;

import org.joda.time.*;

// [datatype-joda#146]: disable overwriting of timezone
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeSerializationWithOffsets146Test extends JodaTestBase
{
    // [datatype-joda#146]
    @Test
    public void testDateTimeSerializationWithOffsets146() throws Exception
    {
        final String inputStr = "2024-12-01T12:00:00+02:00";
        final DateTime inputValue = DateTime.parse(inputStr);
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();

        // By default, do adjust timezone to UTC
        final String defaultOutput = mapper.writeValueAsString(inputValue);
        assertEquals(q("2024-12-01T10:00:00.000Z"), defaultOutput);

        // But if we disable it, no adjustment
        final String alternateOutput = mapper.writer()
                .without(SerializationFeature.WRITE_DATES_WITH_CONTEXT_TIME_ZONE)
                .writeValueAsString(inputValue);

        assertEquals(q(inputStr), alternateOutput);
    }
}
