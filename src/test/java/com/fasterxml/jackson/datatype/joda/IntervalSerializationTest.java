package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.Interval;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

public class IntervalSerializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = jodaMapper();
    {
        MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }
    private final ObjectWriter WRITER = MAPPER.writer();

    /*
    /**********************************************************
    /* Tests for Interval type
    /**********************************************************
     */

    public void testIntervalSerBasic() throws IOException
    {
        Interval interval = new Interval(1396439982, 1396440001);
        assertEquals(quote("1396439982-1396440001"), MAPPER.writeValueAsString(interval));

        // related to #48
        assertEquals(quote("1970-01-17T03:53:59.982Z/1970-01-17T03:54:00.001Z"),
                WRITER.without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                        .writeValueAsString(interval));
    }

    public void testIntervalSerWithTypeInfo() throws IOException
    {
        Interval interval = new Interval(1396439982, 1396440001);

        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Interval.class, ObjectConfiguration.class);
        assertEquals("[\"org.joda.time.Interval\"," + quote("1396439982-1396440001") + "]",
                mapper.writeValueAsString(interval));
    }
}
