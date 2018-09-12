package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class IntervalSerializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    static class FormattedInterval
    {
        @JsonFormat(timezone="EST")
        public Interval interval;
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                    SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .build();

    private final ObjectWriter WRITER = MAPPER.writer();

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

        ObjectMapper mapper = mapperWithModuleBuilder()
            .addMixIn(Interval.class, ObjectConfiguration.class)
            .build();
        assertEquals("[\"org.joda.time.Interval\"," + quote("1396439982-1396440001") + "]",
                mapper.writeValueAsString(interval));
    }

    public void testWithTimeZoneOverride() throws Exception
    {
        Interval int1 = new Interval(1396439982, 1396440001);
        FormattedInterval input = new FormattedInterval();
        input.interval = int1;
        String json = MAPPER.writeValueAsString(input);

        FormattedInterval result = MAPPER.readValue(json, FormattedInterval.class);
        assertNotNull(result);

        // Ensure timezone sticks:
        Interval resultInt = result.interval;
        assertEquals(1396439982, resultInt.getStartMillis());
        assertEquals(1396440001, resultInt.getEndMillis());

        DateTimeZone resultTz = resultInt.getStart().getZone();
        // Is this stable enough for testing?
        assertEquals("America/New_York", resultTz.getID());
    }
}
