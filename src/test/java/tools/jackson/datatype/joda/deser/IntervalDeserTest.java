package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import tools.jackson.databind.DeserializationFeature;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.chrono.ISOChronology;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class IntervalDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Tests for Interval type
    /**********************************************************
     */

    @Test
    public void testIntervalDeser() throws IOException
    {
        final ObjectMapper mapper = mapperWithModule();

        Interval interval = mapper.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());

        interval = mapper.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        
        assertEquals(ISOChronology.getInstance(DateTimeZone.UTC), interval.getChronology());
    }

    @Test
    public void testIntervalDeserWithTimeZone() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Europe/Paris"));

        Interval interval = mapper.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("Europe/Paris")), interval.getChronology());

        mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));

        interval = mapper.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("America/Los_Angeles")), interval.getChronology());
    }
    
    @Test
    public void testIntervalDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(Interval.class, ObjectConfiguration.class)
                .build();
        Interval interval= mapper.readValue("[\"org.joda.time.Interval\",\"1396439982-1396440001\"]", Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
    }

    @Test
    public void testIntervalDeserFromIso8601WithTimezoneWithContextTimeZone() throws IOException
    {
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .defaultTimeZone(TimeZone.getTimeZone("GMT-2:00"))
                .enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        final Interval expectedInterval = new Interval(1000, 2000, DateTimeZone.forOffsetHours(-2));
        final Interval actualInterval =
                mapper.readValue(quote("1970-01-01T06:00:01.000+06:00/1970-01-01T06:00:02.000+06:00"), Interval.class);

        assertEquals(expectedInterval, actualInterval);
    }

    @Test
    public void testIntervalDeserFromIso8601WithTimezoneWithoutContextTimeZone() throws IOException
    {
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        final Interval expectedInterval = new Interval(1000, 2000, DateTimeZone.forOffsetHours(6));
        final Interval actualInterval =
                mapper.readValue(quote("1970-01-01T06:00:01.000+06:00/1970-01-01T06:00:02.000+06:00"), Interval.class);

        assertEquals(expectedInterval, actualInterval);
    }

    @Test
    public void testIntervalDeserFromIso8601WithTimezoneWithDefaultTimeZone() throws IOException
    {
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        final Interval expectedInterval = new Interval(1000, 2000, DateTimeZone.UTC);
        final Interval actualInterval =
                mapper.readValue(quote("1970-01-01T06:00:01.000+06:00/1970-01-01T06:00:02.000+06:00"), Interval.class);

        assertEquals(expectedInterval, actualInterval);
    }
}
