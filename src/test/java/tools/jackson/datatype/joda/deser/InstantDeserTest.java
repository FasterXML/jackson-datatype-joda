package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class InstantDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testDeserReadableInstant() throws IOException {
        ReadableInstant date = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"), ReadableInstant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        assertNull(MAPPER.readValue(quote(""), ReadableInstant.class));
    }

    @Test
    public void testDeserInstantWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(Instant.class, MixinForPolymorphism.class)
                .build();
        Instant date = mapper.readValue("[\"org.joda.time.Instant\",\"1972-12-28T12:00:01.000+0000\"]",
                Instant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());
    }

    @Test
    public void testDeserInstantFromNumber() throws IOException
    {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 1972);
        long timepoint = cal.getTime().getTime();

        // Ok, first: using JSON number (milliseconds since epoch)
        Instant instant = MAPPER.readValue(String.valueOf(timepoint), Instant.class);
        assertEquals(timepoint, instant.getMillis());
    }

    @Test
    public void testDeserInstant() throws IOException
    {
        Instant date = MAPPER.readValue(quote("1972-12-28T12:00:01.000Z"), Instant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        assertNull(MAPPER.readValue(quote(""), Instant.class));
    }

    @Test
    public void testDeserInstantCustomFormat() throws IOException
    {
        FormattedInstant input = MAPPER.readValue(aposToQuotes(
                "{'value':'28/12/1972 12_34_56_789'}"),
                FormattedInstant.class);
        DateTime date = input.value.toDateTime();
        assertEquals(1972, date.getYear());
        assertEquals(789, date.getMillisOfSecond());
    }

    // "Timestamp as String" with leading sign must be accepted for all values
    // within 64-bit range, including 19-digit ones
    @Test
    public void testDeserInstantFromSignedTimestampString() throws IOException
    {
        for (long ts : new long[] { Long.MIN_VALUE, -1000000000000000000L,
                1000000000000000000L, Long.MAX_VALUE }) {
            _verifyTimestampString(String.valueOf(ts), ts);
        }
        _verifyTimestampString("+1000000000000000000", 1000000000000000000L);
        _verifyTimestampString("+"+Long.MAX_VALUE, Long.MAX_VALUE);
    }

    private void _verifyTimestampString(String text, long expected) throws IOException
    {
        try (JsonParser p = new UntypedScalarsParser(MAPPER.createParser(quote(text)))) {
            assertEquals(expected, MAPPER.readValue(p, Instant.class).getMillis());
        }
    }

    // Value without offset must be resolved using context time zone (and not
    // JVM default one) even if not adjusting values that include offset
    @Test
    public void testDeserInstantWithoutOffsetWithoutAdjust() throws IOException
    {
        for (String zoneId : new String[] { "Asia/Kolkata", "America/Los_Angeles" }) {
            final DateTimeZone tz = DateTimeZone.forID(zoneId);
            ObjectMapper mapper = jodaMapperBuilder(tz.toTimeZone())
                    .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                    .build();
            assertEquals(new DateTime(2020, 1, 1, 0, 0, 0, tz).toInstant(),
                    mapper.readValue(quote("2020-01-01T00:00:00"), Instant.class));
        }
    }

    static class ExplicitZoneBean {
        @JsonFormat(timezone = "Asia/Tokyo")
        public Instant value;
    }

    // Explicit zone of format wins over context time zone
    @Test
    public void testDeserInstantWithoutOffsetExplicitZoneWithoutAdjust() throws IOException
    {
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("America/Los_Angeles"))
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        ExplicitZoneBean bean = mapper.readValue("{\"value\":\"2020-01-01T00:00:00\"}",
                ExplicitZoneBean.class);
        assertEquals(new DateTime(2020, 1, 1, 0, 0, 0, DateTimeZone.forID("Asia/Tokyo")).toInstant(),
                bean.value);
    }

    // Context time zone same as JVM default one: no override needed, same result
    @Test
    public void testDeserInstantWithoutOffsetDefaultZoneWithoutAdjust() throws IOException
    {
        final DateTimeZone tz = DateTimeZone.getDefault();
        ObjectMapper mapper = jodaMapperBuilder(tz.toTimeZone())
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        assertEquals(new DateTime(2020, 1, 1, 0, 0, 0, tz).toInstant(),
                mapper.readValue(quote("2020-01-01T00:00:00"), Instant.class));
    }

    // Joda accepts years one past supported range, but instant calculation
    // silently wraps around to the other end of the timeline on overflow
    @Test
    public void testDeserFailsForOverflowingYear() throws IOException
    {
        final ObjectMapper mapper = mapperWithModule();
        for (String value : new String[] {
                "292278994-12-31T00:00:00Z",
                "-292275055-01-01T00:00:00Z",
        }) {
            try {
                Instant result = mapper.readValue(quote(value), Instant.class);
                fail("Should not pass, got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "instant out of supported range (overflow)");
                assertEquals(value, e.getValue());
            }
        }
        assertEquals(new Instant(Long.MAX_VALUE),
                mapper.readValue(quote("292278994-08-17T07:12:55.807Z"), Instant.class));
    }
}
