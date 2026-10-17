package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
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
                .enable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
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
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
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
                .enable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        final Interval expectedInterval = new Interval(1000, 2000, DateTimeZone.UTC);
        final Interval actualInterval =
                mapper.readValue(quote("1970-01-01T06:00:01.000+06:00/1970-01-01T06:00:02.000+06:00"), Interval.class);

        assertEquals(expectedInterval, actualInterval);
    }

    @Test
    public void testIntervalDeserFailsForOverflowingPeriod() throws IOException
    {
        final ObjectMapper mapper = mapperWithModule();
        for (String value : new String[] {
                "2020-01-01T00:00:00Z/P2147483647Y",
                "-292275054-01-01T00:00:00Z/P-2147483647Y",
                "292278993-12-31T23:59:59.999Z/PT2147483647H",
                "P-2147483647Y/2020-01-01T00:00:00Z",
                "PT2147483647H/-292275054-01-01T00:00:00Z",
        }) {
            try {
                Interval result = mapper.readValue(quote(value), Interval.class);
                fail("Should not pass, got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "Cannot deserialize value of type `org.joda.time.Interval` from String");
                verifyException(e, "period or duration out of range");
                assertEquals(value, e.getValue());
            }
        }
    }

    @Test
    public void testIntervalDeserFailsForOutOfRangeEpochMillis() throws IOException
    {
        final ObjectMapper mapper = mapperWithModule();
        // start, end outside of `long` range for "start-end" (epoch millis) notation
        for (String[] input : new String[][] {
                { "9223372036854775808-9223372036854775809", "9223372036854775808" },
                { "0-9223372036854775808", "9223372036854775808" },
        }) {
            final String value = input[0];
            try {
                Interval result = mapper.readValue(quote(value), Interval.class);
                fail("Should not pass, got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "Failed to parse number from '"+input[1]+"'");
                assertEquals(input[1], e.getValue());
            }
        }
    }
}
