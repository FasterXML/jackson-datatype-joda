package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class LocalTimeDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests for LocalTime type
    /**********************************************************
     */

    public void testLocalTimeDeser() throws IOException
    {
        // couple of acceptable formats, so:
        LocalTime time = MAPPER.readValue("[23,59,1,222]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(222, time.getMillisOfSecond());

        LocalTime time2 = MAPPER.readValue(quote("13:45:22"), LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalTime.class));
    }

    public void testLocalTimeDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalTime.class, ObjectConfiguration.class)
                .build();

        // couple of acceptable formats, so:
        LocalTime time = mapper.readValue("[\"org.joda.time.LocalTime\",[23,59,1,10]]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(10, time.getMillisOfSecond());

        LocalTime time2 = mapper.readValue("[\"org.joda.time.LocalTime\",\"13:45:22\"]", LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());
    }
}
