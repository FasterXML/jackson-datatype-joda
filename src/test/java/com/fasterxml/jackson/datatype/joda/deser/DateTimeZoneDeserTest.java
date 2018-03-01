package com.fasterxml.jackson.datatype.joda.deser;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserTest.DateTimeZoneWrapper;

public class DateTimeZoneDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [datatype-joda#82]
    public void testSimpleDateTimeZone() throws Exception
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        DateTimeZone input = DateTimeZone.forTimeZone(timeZone);
        String json = MAPPER.writeValueAsString(input);
        assertEquals(quote("-06:00"), json);

        DateTimeZone result = MAPPER.readValue(json, DateTimeZone.class);
        assertEquals(result, input);

        // But then verify polymorphic handling
        DateTimeZoneWrapper input2 = new DateTimeZoneWrapper(input);
        json = MAPPER.writeValueAsString(input2);
        if (json.indexOf("DateTimeZone") < 0) {
            fail("Should contain type information, json = "+json);
        }

        DateTimeZoneWrapper result2 = MAPPER.readValue(json, DateTimeZoneWrapper.class);    
        assertNotNull(result2.tz);
        assertEquals(input, result2.tz);
    }
}
