package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.deser.DeserializationProblemHandler;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.datatype.joda.JodaTestBase;

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

    private final ObjectMapper MAPPER = mapperWithModule();
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
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());
    }

    // [datatype-joda#8]
    @Test
    public void testDeserReadableDateTimeWithTimeZoneInfo() throws IOException
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        final ObjectMapper mapper = mapperWithModule(timeZone);
        DateTimeZone dateTimeZone = DateTimeZone.forTimeZone(timeZone);
        ReadableDateTime date = mapper.readValue(quote("1972-12-28T12:00:01.000-0600"),
                ReadableDateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000-06:00", date.toString());
        assertEquals(dateTimeZone, date.getZone());

        // default behavior is to ignore the timezone in serialized data
        ReadableDateTime otherTzDate = mapper.readValue(quote("1972-12-28T12:00:01.000-0700"), ReadableDateTime.class);
        assertEquals(dateTimeZone, otherTzDate.getZone());

        assertNull(mapper.readValue(quote(""), ReadableDateTime.class));
    }

    @Test
    public void testDeserReadableDateTimeWithTimeZoneFromData() throws IOException {
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("GMT-6"))
            .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
        ReadableDateTime date = mapper.readValue(quote("2014-01-20T08:59:01.000-0500"),
                ReadableDateTime.class);
        assertEquals(DateTimeZone.forOffsetHours(-5), date.getZone());
    }

    @Test
    public void testDeserReadableDateTimeWithContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("UTC"))
            .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
        ReadableDateTimeWithContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 5, 59, DateTimeZone.forID("UTC"));
        assertEquals(expected, date.time);
    }

    @Test
    public void testDeserReadableDateTimeWithoutContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("UTC"))
            .enable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
        ReadableDateTimeWithoutContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithoutContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 8, 59, DateTimeZone.forOffsetHours(3));
        assertEquals(expected, date.time);
    }

    @Test
    public void test_enable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
            .enable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
            .build();
        DateTime result = mapper.readValue("{\"jodaDateTime\":\"2017-01-01 01:01:01[Asia/Shanghai]\"}",
                Issue93Bean.class).getJodaDateTime();
        assertEquals(new DateTime(2016, 12, 31, 17, 1, 1, DateTimeZone.UTC), result);
    }

    @Test
    public void test_disable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
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
    }

    // [datatype-joda#191]: custom patterns with literal brackets must still work
    @Test
    public void testZoneIdSuffixWithBracketPattern() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .enable(DateTimeFeature.WRITE_DATES_WITH_ZONE_ID)
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
        ObjectMapper mapper = mapperWithModuleBuilder().build();
        BracketInMiddlePatternBean input = new BracketInMiddlePatternBean();
        input.value = new DateTime(2017, 1, 1, 0, 0, DateTimeZone.UTC);
        String json = mapper.writeValueAsString(input);
        assertEquals(a2q("{'value':'2017[01]01'}"), json);
        BracketInMiddlePatternBean result = mapper.readValue(json, BracketInMiddlePatternBean.class);
        assertEquals(input.value, result.value);
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
}
