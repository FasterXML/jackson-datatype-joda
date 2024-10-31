package com.fasterxml.jackson.datatype.joda.failing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import org.joda.time.*;

// [datatype-joda#146]: disable overwriting of timezone
public class DateTimeSerializationWithOffsets146Test extends JodaTestBase
{
    private final ObjectMapper MAPPER = jodaMapper();
    {
        MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    // [datatype-joda#146]
    public void testLocalDateSer() throws Exception
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
