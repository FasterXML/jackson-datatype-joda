package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for verifying limited interoperability for Joda time.
 * Basic support is added for handling {@link DateTime}; more can be
 * added over time if and when requested.
 */
public class DateTimeDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    static class DateTimeZoneWrapper {
        @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
        public DateTimeZone tz;

        public DateTimeZoneWrapper() { }
        public DateTimeZoneWrapper(DateTimeZone tz0) { tz = tz0; }
    }

    static class ReadableDateTimeWithoutContextTZOverride {
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ReadableDateTime time;
    }

    static class ReadableDateTimeWithContextTZOverride {
        @JsonFormat(with = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ReadableDateTime time;
    }

    static class BracketPatternBean {
        @JsonFormat(pattern = "yyyy-MM-dd'['HH']'", timezone = "UTC")
        public DateTime value;
    }

    static class BracketInMiddlePatternBean {
        @JsonFormat(pattern = "yyyy'['MM']'dd", timezone = "UTC")
        public DateTime value;
    }

    static class Issue93Bean {

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        public DateTime jodaDateTime;

        public DateTime getJodaDateTime() {
            return jodaDateTime;
        }
    }

    /*
    /**********************************************************
    /* Tests for DateTime (and closely related)
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();
    private final ObjectReader READER = MAPPER.readerFor(DateTime.class);

    /**
     * Ok, then: should be able to convert from JSON String or Number,
     * with standard deserializer we provide.
     */
    @Test
    public void testDeserFromNumber() throws IOException
    {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // use some arbitrary but non-default time point (after 1.1.1970)
        cal.set(Calendar.YEAR, 1972);
        long timepoint = cal.getTime().getTime();

        // Ok, first: using JSON number (milliseconds since epoch)
        DateTime dt = READER.readValue(String.valueOf(timepoint));
        assertEquals(timepoint, dt.getMillis());

        // And then ISO-8601 String
        dt = READER.readValue(quote("1972-12-28T12:00:01.000+0000"));
        assertEquals("1972-12-28T12:00:01.000Z", dt.toString());
    }

    @Test
    public void testDeserReadableDateTime() throws IOException
    {
        ReadableDateTime date = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"),
                ReadableDateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());
    }

    // [datatype-joda#8]
    @Test
    public void testDeserReadableDateTimeWithTimeZoneInfo() throws IOException
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        MAPPER.setTimeZone(timeZone);
        ReadableDateTime date = MAPPER.readValue(quote("1972-12-28T12:00:01.000-0600"),
                ReadableDateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000-06:00", date.toString());
        assertEquals(dateTimeZone, date.getZone());

        // default behavior is to ignore the timezone in serialized data
        ReadableDateTime otherTzDate = MAPPER.readValue(quote("1972-12-28T12:00:01.000-0700"), ReadableDateTime.class);
        assertEquals(dateTimeZone, otherTzDate.getZone());

        assertNull(MAPPER.readValue(quote(""), ReadableDateTime.class));
    }

    @Test
    public void testDeserReadableDateTimeWithTimeZoneFromData() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT-6"));
        ReadableDateTime date = mapper.readValue(quote("2014-01-20T08:59:01.000-0500"),
                ReadableDateTime.class);
        assertEquals(DateTimeZone.forOffsetHours(-5), date.getZone());
    }

    @Test
    public void testDeserReadableDateTimeWithContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        ReadableDateTimeWithContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 5, 59, DateTimeZone.forID("UTC"));
        assertEquals(expected, date.time);
    }

    @Test
    public void testDeserReadableDateTimeWithoutContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        ReadableDateTimeWithoutContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithoutContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 8, 59, DateTimeZone.forOffsetHours(3));
        assertEquals(expected, date.time);
    }

    @Test
    public void test_enable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = jodaMapper();
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        DateTime result = mapper.readValue("{\"jodaDateTime\":\"2017-01-01 01:01:01[Asia/Shanghai]\"}",
                Issue93Bean.class).getJodaDateTime();
        assertEquals(new DateTime(2016, 12, 31, 17, 1, 1, DateTimeZone.UTC), result);
    }

    // [datatype-joda#194]: zone id suffix value must be adjusted to context
    // time zone (not default zone of format), same as offset value
    @Test
    public void test_enable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE_nonUTC() throws Exception
    {
        final DateTimeZone taipei = DateTimeZone.forID("Asia/Taipei");
        ObjectMapper mapper = jodaMapper();
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(taipei.toTimeZone());
        final DateTime exp = new DateTime(2020, 1, 1, 7, 0, 0, taipei);
        assertEquals(exp, mapper.readValue(quote("2020-01-01T00:00:00.000+01:00"),
                DateTime.class));
        assertEquals(exp, mapper.readValue(quote("2020-01-01T00:00:00.000+01:00[Europe/Paris]"),
                DateTime.class));
    }

    @Test
    public void test_disable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        DateTime result = mapper.readValue("{\"jodaDateTime\":\"2017-01-01 01:01:01[Asia/Shanghai]\"}",
                Issue93Bean.class).getJodaDateTime();

        DateTimeZone expTZ = DateTimeZone.forID("Asia/Shanghai");
        assertEquals(new DateTime(2017, 1, 1, 1, 1, 1, expTZ), result);
    }

    @Test
    public void testDeserFailsForMalformedZoneIdSuffix() throws Exception
    {
        // well-formed suffix is what we write, and still reads back:
        assertEquals(new DateTime(2017, 1, 1, 1, 1, 1, DateTimeZone.UTC),
                READER.readValue(quote("2017-01-01T01:01:01.000Z[UTC]")));

        _verifyMalformedSuffix("2017-01-01T01:01:01.000Z[UTC]x",
                "unexpected content after closing ']'");
        _verifyMalformedSuffix("2017-01-01T01:01:01.000Z[UTC",
                "missing closing ']'");
    }

    // [datatype-joda#191]: edge cases of suffix detection
    @Test
    public void testZoneIdSuffixEdgeCases() throws Exception
    {
        final DateTime exp = new DateTime(2017, 1, 1, 1, 1, 1, DateTimeZone.UTC);
        // surrounding whitespace is trimmed, so still fine
        assertEquals(exp, READER.readValue(quote("  2017-01-01T01:01:01.000Z[UTC]  ")));
        // '[' as the very last character
        _verifyMalformedSuffix("2017-01-01T01:01:01.000Z[", "missing closing ']'");
        // multiple bracketed sections, last one is not a valid zone id
        try {
            READER.readValue(quote("2017-01-01T01:01:01.000Z[UTC][x]"));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Unknown DateTimeZone id 'x'");
            assertEquals("2017-01-01T01:01:01.000Z[UTC][x]", e.getValue());
        }
    }

    // [datatype-joda#191]: invalid date/time with valid suffix must not leak
    //   raw IllegalArgumentException
    @Test
    public void testInvalidDateTimeBeforeZoneIdSuffix() throws Exception
    {
        for (String doc : new String[] {
                "x[UTC]",
                "2017-01-01T01:01:01.000Zjunk[UTC]"
        }) {
            try {
                READER.readValue(quote(doc));
                fail("Should not pass for '"+doc+"'");
            } catch (InvalidFormatException e) {
                verifyException(e, "Invalid date/time before DateTimeZone id suffix");
                assertEquals(doc, e.getValue());
            }
        }
    }

    // [datatype-joda#191]: same handling for all registered target types
    @Test
    public void testZoneIdSuffixForReadableTypes() throws Exception
    {
        final ObjectMapper mapper = jodaMapper();
        final DateTime exp = new DateTime(2017, 1, 1, 1, 1, 1, DateTimeZone.UTC);
        final String valid = quote("2017-01-01T01:01:01.000Z[UTC]");
        final String malformed = quote("2017-01-01T01:01:01.000Z[UTC]x");

        assertEquals(exp, mapper.readValue(valid, ReadableInstant.class));
        assertEquals(exp, mapper.readValue(valid, ReadableDateTime.class));
        for (Class<?> type : new Class<?>[] { ReadableInstant.class, ReadableDateTime.class }) {
            try {
                mapper.readValue(malformed, type);
                fail("Should not pass for "+type.getName());
            } catch (InvalidFormatException e) {
                verifyException(e, "Malformed DateTimeZone id suffix");
            }
        }
    }

    // [datatype-joda#191]: also as a bean property, not just root value
    @Test
    public void testMalformedZoneIdSuffixInBean() throws Exception
    {
        try {
            jodaMapper().readValue(
                    a2q("{'time':'2017-01-01T01:01:01.000Z[UTC'}"),
                    ReadableDateTimeWithoutContextTZOverride.class);
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Malformed DateTimeZone id suffix");
            verifyException(e, "missing closing ']'");
            // [datatype-joda#200]: Joda's own message included as well
            verifyException(e, "is malformed at");
        }
    }

    private void _verifyMalformedSuffix(String doc, String expMsg) throws Exception
    {
        try {
            READER.readValue(quote(doc));
            fail("Should not pass for '"+doc+"'");
        } catch (InvalidFormatException e) {
            verifyException(e, "Malformed DateTimeZone id suffix");
            verifyException(e, expMsg);
            assertEquals(doc, e.getValue());
        }
    }

    // [datatype-joda#191]: problem handler gets to deal with malformed suffix
    @Test
    public void testMalformedZoneIdSuffixWithProblemHandler() throws Exception
    {
        final DateTime fallback = new DateTime(0L, DateTimeZone.UTC);
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt,
                            Class<?> targetType, String valueToConvert, String failureMsg) {
                        return fallback;
                    }
                })
                .build();
        assertEquals(fallback, mapper.readValue(quote("2017-01-01T01:01:01.000Z[UTC"),
                DateTime.class));
        // [datatype-joda#200]: as well as unknown zone id
        assertEquals(fallback, mapper.readValue(quote("2017-01-01T01:01:01.000Z[Foo/Bar]"),
                DateTime.class));
    }

    // [datatype-joda#191]: custom patterns with literal brackets must still work
    @Test
    public void testZoneIdSuffixWithBracketPattern() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        BracketPatternBean input = new BracketPatternBean();
        input.value = new DateTime(2017, 1, 1, 10, 0, DateTimeZone.UTC);
        String json = mapper.writeValueAsString(input);
        assertEquals(a2q("{'value':'2017-01-01[10][UTC]'}"), json);
        BracketPatternBean result = mapper.readValue(json, BracketPatternBean.class);
        assertEquals(input.value, result.value);
    }

    @Test
    public void testBracketPatternWithoutZoneIdSuffix() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        BracketInMiddlePatternBean input = new BracketInMiddlePatternBean();
        input.value = new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC);
        String json = mapper.writeValueAsString(input);
        assertEquals(a2q("{'value':'2017[01]01'}"), json);
        BracketInMiddlePatternBean result = mapper.readValue(json, BracketInMiddlePatternBean.class);
        assertEquals(input.value, result.value);
    }

    // Value ending with literal bracket of custom pattern is not a zone id suffix
    @Test
    public void testBracketPatternValueWithoutZoneIdSuffix() throws Exception
    {
        BracketPatternBean result = mapperWithModule().readValue(
                a2q("{'value':'2017-01-01[10]'}"), BracketPatternBean.class);
        assertEquals(new DateTime(2017, 1, 1, 10, 0, DateTimeZone.UTC), result.value);
        // but still fails for invalid value
        try {
            mapperWithModule().readValue(a2q("{'value':'2017-01-01[xx]'}"), BracketPatternBean.class);
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Unknown DateTimeZone id 'xx'");
        }
        // including actual cause, if date/time itself is invalid
        try {
            mapperWithModule().readValue(a2q("{'value':'2017-13-01[10]'}"), BracketPatternBean.class);
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Unknown DateTimeZone id '10'");
            verifyException(e, "Value 13 for monthOfYear must be in the range [1,12]");
        }
    }

    // [datatype-joda#199]: edge-of-long timestamps
    @Test
    public void testDeserFromEdgeOfLongTimestamp() throws Exception
    {
        // valid in UTC (commonly used as "max value" marker)
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("UTC"));
        DateTime dt = mapper.readValue(String.valueOf(Long.MAX_VALUE), DateTime.class);
        assertEquals(Long.MAX_VALUE, dt.getMillis());
        assertEquals(DateTimeZone.UTC, dt.getZone());
        dt = mapper.readValue(String.valueOf(Long.MIN_VALUE), DateTime.class);
        assertEquals(Long.MIN_VALUE, dt.getMillis());

        // but zone offset addition may overflow...
        _verifyEdgeOfLongTimestampFails(mapperWithModule(TimeZone.getTimeZone("Asia/Taipei")),
                Long.MAX_VALUE, "Adding time zone offset caused overflow");
        // ... or Joda would silently drop zone
        _verifyEdgeOfLongTimestampFails(mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles")),
                Long.MAX_VALUE, "Timestamp out of supported range for time zone 'America/Los_Angeles'");
    }

    private void _verifyEdgeOfLongTimestampFails(ObjectMapper mapper, long ts, String expMsg)
        throws Exception
    {
        try {
            DateTime result = mapper.readValue(String.valueOf(ts), DateTime.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, expMsg);
            assertEquals(Long.valueOf(ts), e.getValue());
        }
    }

    /*
    /**********************************************************
    /* Coercion tests
    /**********************************************************
     */

    // @since 2.12
    @Test
    public void testReadFromEmptyString() throws Exception
    {
        // By default, fine to deser from empty or blank
        assertNull(READER.readValue(quote("")));
        assertNull(READER.readValue(quote("    ")));

        final ObjectMapper m = mapperWithFailFromEmptyString();
        try {
            m.readerFor(DateTime.class)
                .readValue(quote(""));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot coerce empty String");
            verifyException(e, DateTime.class.getName());
        }
    }

    @Test
    public void testDateTimeDeserFromTimestampWithoutAdjust() throws IOException
    {
        final TimeZone la = TimeZone.getTimeZone("America/Los_Angeles");
        // with adjustment (default), context zone is used
        DateTime dt = mapperWithModule(la).readValue("1238558582001", DateTime.class);
        assertEquals(1238558582001L, dt.getMillis());
        assertEquals(DateTimeZone.forID("America/Los_Angeles"), dt.getZone());
        // without, timestamp resolved as UTC
        ObjectMapper mapper = jodaMapperBuilder(la)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        dt = mapper.readValue("1238558582001", DateTime.class);
        assertEquals(1238558582001L, dt.getMillis());
        assertEquals(DateTimeZone.UTC, dt.getZone());
    }

    @Test
    public void testDateTimeDeserFromTimestampWithoutAdjustAnnotation() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        ReadableDateTimeWithoutContextTZOverride w = mapper.readValue(
                "{\"time\":1238558582001}", ReadableDateTimeWithoutContextTZOverride.class);
        assertEquals(1238558582001L, w.time.getMillis());
        assertEquals(DateTimeZone.UTC, w.time.getZone());
    }
}
