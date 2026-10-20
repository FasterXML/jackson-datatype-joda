package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class IntervalDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    static class FormattedInterval {
        @JsonFormat(timezone = "Asia/Tokyo")
        public Interval interval;
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

    // Values without offset must be resolved using context time zone (and
    // not JVM default one), same as for `DateTime`
    @Test
    public void testIntervalDeserWithoutOffsetUsesContextTimeZone() throws IOException
    {
        for (String zoneId : new String[] { "Asia/Kolkata", "America/Los_Angeles" }) {
            final DateTimeZone tz = DateTimeZone.forID(zoneId);
            final Interval exp = new Interval(new DateTime(2020, 1, 1, 0, 0, 0, tz),
                    new DateTime(2020, 1, 2, 0, 0, 0, tz));
            for (boolean adjust : new boolean[] { true, false }) {
                final ObjectMapper mapper = jodaMapperBuilder(tz.toTimeZone())
                        .configure(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, adjust)
                        .build();
                for (String value : new String[] {
                        "2020-01-01T00:00:00/2020-01-02T00:00:00",
                        "2020-01-01/2020-01-02",
                        "2020-01-01T00:00:00/P1D",
                        "P1D/2020-01-02T00:00:00",
                }) {
                    assertEquals(exp, mapper.readValue(quote(value), Interval.class),
                            "value '"+value+"', adjust: "+adjust);
                }
            }
        }
    }

    @Test
    public void testIntervalDeserWithoutOffsetUsesFormatTimeZone() throws IOException
    {
        final DateTimeZone tokyo = DateTimeZone.forID("Asia/Tokyo");
        final Interval exp = new Interval(new DateTime(2020, 1, 1, 0, 0, 0, tokyo),
                new DateTime(2020, 1, 2, 0, 0, 0, tokyo));
        final ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        FormattedInterval result = mapper.readValue(
                "{\"interval\":\"2020-01-01T00:00:00/2020-01-02T00:00:00\"}",
                FormattedInterval.class);
        assertEquals(exp, result.interval);
    }

    // "start-end" (epoch millis) form has no zone information: without
    // adjustment, resolved as UTC (same as timestamps)
    @Test
    public void testIntervalDeserFromMillisWithoutAdjust() throws IOException
    {
        final ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("America/Los_Angeles"))
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        Interval interval = mapper.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstanceUTC(), interval.getChronology());
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
                // overflow from applying offset, not period
                "292278994-08-17T07:12:55.807-14:00/PT1S",
        }) {
            try {
                Interval result = mapper.readValue(quote(value), Interval.class);
                fail("Should not pass, got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "Cannot deserialize value of type `org.joda.time.Interval` from String");
                verifyException(e, "value out of range (overflow)");
                assertEquals(value, e.getValue());
            }
        }
    }

    // Smaller overflows are reported by Joda as out-of-range field values
    // (and not as `ArithmeticException`), but must fail the same way
    @Test
    public void testIntervalDeserFailsForOutOfRangeYear() throws IOException
    {
        final ObjectMapper mapper = mapperWithModule();
        final String value = "2020-01-01T00:00:00Z/P300000000Y";
        try {
            Interval result = mapper.readValue(quote(value), Interval.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `org.joda.time.Interval` from String");
            verifyException(e, "for year must be in the range");
            assertEquals(value, e.getValue());
        }
    }

    // Problem handler gets to deal with all String value failures
    @Test
    public void testIntervalDeserFailuresWithProblemHandler() throws IOException
    {
        final Interval fallback = new Interval(0L, 1L, DateTimeZone.UTC);
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt,
                            Class<?> targetType, String valueToConvert, String failureMsg) {
                        return fallback;
                    }
                })
                .build();
        for (String value : new String[] {
                "20200101", // no separator
                "2020-01-01T00:00:00Z/P2147483647Y", // overflow
                "200-100", // end before start
                "0-9223372036854775808", // out of `long` range
        }) {
            assertEquals(fallback, mapper.readValue(quote(value), Interval.class), value);
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
                assertEquals(value, e.getValue());
            }
        }
    }
}
