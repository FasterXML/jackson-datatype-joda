package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.cfg.DateTimeFeature;
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
