package com.fasterxml.jackson.datatype.joda.ser;

import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFormatTimeZoneWithPattern98Test extends JodaTestBase
{
    static class Wrapper {
        @JsonFormat(
                shape   = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS ZZZ",
                timezone = "Europe/Budapest"   // +01:00 in winter
        )
        public DateTime value;

        Wrapper(DateTime v) { value = v; }
    }

    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void patternShouldNotEraseTimeZone()
            throws Exception
    {
        // Explicity set with Timezone Europe/Budapest
        _testSerializationOutput(
            new DateTime(2018, 1, 1, 12, 1, 2, 3,
                    DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Budapest")))
        );
        // Using JsonFormat
        _testSerializationOutput(
            new DateTime(2018, 1, 1, 12, 1, 2, 3)
        );
    }

    private void _testSerializationOutput(
            DateTime dateTime
    ) throws Exception
    {
        String actual = MAPPER.writeValueAsString(new Wrapper(dateTime));
        String exp = "{\"value\":\"2018-01-01T12:01:02.003 Europe/Budapest\"}";
        assertEquals(exp, actual);
    }

}