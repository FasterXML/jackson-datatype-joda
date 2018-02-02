package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.joda.JodaTestBase;

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

    /**
     * Ok, then: should be able to convert from JSON String or Number,
     * with standard deserializer we provide.
     */
    public void testDeserFromNumber() throws IOException
    {
        final ObjectMapper mapper = jodaMapper();
        
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // use some arbitrary but non-default time point (after 1.1.1970)
        cal.set(Calendar.YEAR, 1972);
        long timepoint = cal.getTime().getTime();

        // Ok, first: using JSON number (milliseconds since epoch)
        DateTime dt = mapper.readValue(String.valueOf(timepoint), DateTime.class);
        assertEquals(timepoint, dt.getMillis());

        // And then ISO-8601 String
        dt = mapper.readValue(quote("1972-12-28T12:00:01.000+0000"), DateTime.class);
        assertEquals("1972-12-28T12:00:01.000Z", dt.toString());
    }

    public void testDeserReadableDateTime() throws IOException
    {
        final ObjectMapper mapper = jodaMapper();

        ReadableDateTime date = mapper.readValue(quote("1972-12-28T12:00:01.000+0000"), ReadableDateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        assertNull(mapper.readValue(quote(""), ReadableDateTime.class));
    }

    // [datatype-joda#8]
    public void testDeserReadableDateTimeWithTimeZoneInfo() throws IOException
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        final ObjectMapper mapper = jodaMapper(timeZone);
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

    public void testDeserReadableDateTimeWithTimeZoneFromData() throws IOException {
        ObjectMapper mapper = jodaMapper(TimeZone.getTimeZone("GMT-6"));
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        ReadableDateTime date = mapper.readValue(quote("2014-01-20T08:59:01.000-0500"),
                ReadableDateTime.class);
        assertEquals(DateTimeZone.forOffsetHours(-5), date.getZone());
    }

    public void testDeserReadableDateTimeWithContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper(TimeZone.getTimeZone("UTC"));
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        ReadableDateTimeWithContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 5, 59, DateTimeZone.forID("UTC"));
        assertEquals(expected, date.time);
    }

    public void testDeserReadableDateTimeWithoutContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper(TimeZone.getTimeZone("UTC"));
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        ReadableDateTimeWithoutContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithoutContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 8, 59, DateTimeZone.forOffsetHours(3));
        assertEquals(expected, date.time);
    }

    public void test_enable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = jodaMapper();
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        DateTime result = mapper.readValue("{\"jodaDateTime\":\"2017-01-01 01:01:01[Asia/Shanghai]\"}",
                Issue93Bean.class).getJodaDateTime();
        assertEquals(new DateTime(2016, 12, 31, 17, 1, 1, DateTimeZone.UTC), result);
    }

    public void test_disable_ADJUST_DATES_TO_CONTEXT_TIME_ZONE() throws Exception
    {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        DateTime result = mapper.readValue("{\"jodaDateTime\":\"2017-01-01 01:01:01[Asia/Shanghai]\"}",
                Issue93Bean.class).getJodaDateTime();

        DateTimeZone expTZ = DateTimeZone.forID("Asia/Shanghai");
        assertEquals(new DateTime(2017, 1, 1, 1, 1, 1, expTZ), result);
    }
}
