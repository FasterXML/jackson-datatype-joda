package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class IntervalDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testIntervalDeser() throws IOException
    {
        final ObjectMapper mapper = jodaMapper();

        Interval interval = mapper.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());

        interval = mapper.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        
        assertEquals(ISOChronology.getInstance(DateTimeZone.UTC), interval.getChronology());
    }

    public void testIntervalDeserWithTimeZone() throws IOException
    {
        ObjectMapper mapper = jodaMapper(TimeZone.getTimeZone("Europe/Paris"));

        Interval interval = mapper.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("Europe/Paris")), interval.getChronology());

        mapper = jodaMapper(TimeZone.getTimeZone("America/Los_Angeles"));

        interval = mapper.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("America/Los_Angeles")), interval.getChronology());

    }
    
    public void testIntervalDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapperBuilder()
                .addMixIn(Interval.class, ObjectConfiguration.class)
                .build();
        Interval interval= mapper.readValue("[\"org.joda.time.Interval\",\"1396439982-1396440001\"]", Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
    }
}
