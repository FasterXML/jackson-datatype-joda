package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Map;
import java.util.TimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.joda.time.*;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

/**
 * Unit tests for verifying limited interoperability for Joda time.
 * Basic support is added for handling {@link DateTime}; more can be
 * added over time if and when requested.
 */
public class MiscDeserializationTest extends JodaTestBase
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

    /*
    /**********************************************************
    /* Tests for DateTime (and closely related)
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();

    /**
     * Ok, then: should be able to convert from JSON String or Number,
     * with standard deserializer we provide.
     */
    public void testDeserFromNumber() throws IOException
    {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        // use some arbitrary but non-default time point (after 1.1.1970)
        cal.set(Calendar.YEAR, 1972);
        long timepoint = cal.getTime().getTime();

        // Ok, first: using JSON number (milliseconds since epoch)
        DateTime dt = MAPPER.readValue(String.valueOf(timepoint), DateTime.class);
        assertEquals(timepoint, dt.getMillis());

        // And then ISO-8601 String
        dt = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"), DateTime.class);
        assertEquals("1972-12-28T12:00:01.000Z", dt.toString());
    }

    public void testDeserReadableDateTime() throws IOException
    {
        ReadableDateTime date = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"), ReadableDateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        assertNull(MAPPER.readValue(quote(""), ReadableDateTime.class));
    }

    // [datatype-joda#8]
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

    public void testDeserReadableDateTimeWithTimeZoneFromData() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("GMT-6"));
        ReadableDateTime date = mapper.readValue(quote("2014-01-20T08:59:01.000-0500"),
                ReadableDateTime.class);
        assertEquals(DateTimeZone.forOffsetHours(-5), date.getZone());
    }

    static class ReadableDateTimeWithContextTZOverride {
        @JsonFormat(with = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ReadableDateTime time;
    }

    public void testDeserReadableDateTimeWithContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        ReadableDateTimeWithContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 5, 59, DateTimeZone.forID("UTC"));
        assertEquals(expected, date.time);
    }

    static class ReadableDateTimeWithoutContextTZOverride {

        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public ReadableDateTime time;
    }

    public void testDeserReadableDateTimeWithoutContextTZOverride() throws IOException {
        ObjectMapper mapper = jodaMapper();
        mapper.enable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);
        mapper.setTimeZone(TimeZone.getTimeZone("UTC"));
        ReadableDateTimeWithoutContextTZOverride date = mapper.readValue("{ \"time\" : \"2016-06-20T08:59:00.000+0300\"}",
                ReadableDateTimeWithoutContextTZOverride.class);
        DateTime expected = new DateTime(2016, 6, 20, 8, 59, DateTimeZone.forOffsetHours(3));
        assertEquals(expected, date.time);
    }

    public void testDeserReadableInstant() throws IOException {
        ReadableInstant date = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"), ReadableInstant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        assertNull(MAPPER.readValue(quote(""), ReadableInstant.class));
    }

    public void testDeserDateTimeWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(DateTime.class, ObjectConfiguration.class);
        DateTime date = mapper.readValue("[\"org.joda.time.DateTime\",\"1972-12-28T12:00:01.000+0000\"]", DateTime.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());
    }

    /*
    /**********************************************************
    /* Tests for LocalDate type
    /**********************************************************
     */

    public void testLocalDateDeser() throws IOException
    {
        // couple of acceptable formats, so:
        LocalDate date = MAPPER.readValue("[2001,5,25]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        LocalDate date2 = MAPPER.readValue(quote("2005-07-13"), LocalDate.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalDate.class));
    }

	public void testLocalDateDeserWithTimeZone() throws IOException
	{
    final String trickyInstant = "1238558582001";
    // MAPPER is using default TimeZone (GMT)
    LocalDate date3 = MAPPER.readValue(trickyInstant, LocalDate.class);
    assertEquals(2009, date3.getYear());
    assertEquals(4, date3.getMonthOfYear());
    assertEquals(1, date3.getDayOfMonth());
    assertEquals(ISOChronology.getInstanceUTC(), date3.getChronology());

		MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

		// couple of acceptable formats, so:
		LocalDate date = MAPPER.readValue("[2001,5,25]", LocalDate.class);
		assertEquals(2001, date.getYear());
		assertEquals(5, date.getMonthOfYear());
		assertEquals(25, date.getDayOfMonth());
		assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

		MAPPER.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
		LocalDate date2 = MAPPER
				.readValue(quote("2005-07-13"), LocalDate.class);
		assertEquals(2005, date2.getYear());
		assertEquals(7, date2.getMonthOfYear());
		assertEquals(13, date2.getDayOfMonth());
		assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

		// since 1.6.1, for [JACKSON-360]
		assertNull(MAPPER.readValue(quote(""), LocalDate.class));

    MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

    LocalDate date4 = MAPPER.readValue(trickyInstant, LocalDate.class);
    assertEquals(2009, date4.getYear());
    assertEquals(3, date4.getMonthOfYear());
    assertEquals(31, date4.getDayOfMonth());
    assertEquals(ISOChronology.getInstanceUTC(), date4.getChronology());
	}
    
    public void testLocalDateDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalDate.class, ObjectConfiguration.class);

        // couple of acceptable formats, so:
        LocalDate date = mapper.readValue("[\"org.joda.time.LocalDate\",[2001,5,25]]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        LocalDate date2 = mapper.readValue("[\"org.joda.time.LocalDate\",\"2005-07-13\"]", LocalDate.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());
    }

    public void testLocalDateDeserWithPartsAsString() throws IOException
    {
        // couple of acceptable formats, so:
        LocalDate date = MAPPER.readValue("[\"2001\",\"5\",\"25\"]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());
    }
    
    /*
    /**********************************************************
    /* Tests for LocalTime type
    /**********************************************************
     */

    public void testLocalTimeDeser() throws IOException
    {
        // couple of acceptable formats, so:
        LocalTime time = MAPPER.readValue("[23,59,1,222]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(222, time.getMillisOfSecond());

        LocalTime time2 = MAPPER.readValue(quote("13:45:22"), LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalTime.class));
    }

    public void testLocalTimeDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalTime.class, ObjectConfiguration.class);

        // couple of acceptable formats, so:
        LocalTime time = mapper.readValue("[\"org.joda.time.LocalTime\",[23,59,1,10]]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(10, time.getMillisOfSecond());

        LocalTime time2 = mapper.readValue("[\"org.joda.time.LocalTime\",\"13:45:22\"]", LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());
    }

    /*
    /**********************************************************
    /* Tests for Interval type
    /**********************************************************
     */
    public void testIntervalDeser() throws IOException
    {
        Interval interval = MAPPER.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());

        interval = MAPPER.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        
        assertEquals(ISOChronology.getInstance(DateTimeZone.UTC), interval.getChronology());
    }

    public void testIntervalDeserWithTimeZone() throws IOException
    {
    	MAPPER.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));

        Interval interval = MAPPER.readValue(quote("1396439982-1396440001"), Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("Europe/Paris")), interval.getChronology());

    	MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        interval = MAPPER.readValue(quote("-100-1396440001"), Interval.class);
        assertEquals(-100, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
        assertEquals(ISOChronology.getInstance(DateTimeZone.forID("America/Los_Angeles")), interval.getChronology());

    }
    
    public void testIntervalDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Interval.class, ObjectConfiguration.class);

        Interval interval= mapper.readValue("[\"org.joda.time.Interval\",\"1396439982-1396440001\"]", Interval.class);
        assertEquals(1396439982, interval.getStartMillis());
        assertEquals(1396440001, interval.getEndMillis());
    }

    /*
    /**********************************************************
    /* Tests for LocalDateTime type
    /**********************************************************
     */

    public void testLocalDateTimeDeser() throws IOException
    {
        // couple of acceptable formats again:
        LocalDateTime date = MAPPER.readValue("[2001,5,25,10,15,30,37]", LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());

        LocalDateTime date2 = MAPPER.readValue(quote("2007-06-30T08:34:09.001"), LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalDateTime.class));

        // MAPPER is using default TimeZone (GMT)
        LocalDateTime date3 = MAPPER.readValue("1238558582001", LocalDateTime.class);
        assertEquals(2009, date3.getYear());
        assertEquals(4, date3.getMonthOfYear());
        assertEquals(1, date3.getDayOfMonth());
        assertEquals(4, date3.getHourOfDay());
        assertEquals(3, date3.getMinuteOfHour());
        assertEquals(2, date3.getSecondOfMinute());
        assertEquals(1, date3.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date3.getChronology());
    }

    public void testLocalDateTimeDeserWithTimeZone() throws IOException
    {
        MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        
        // couple of acceptable formats again:
        LocalDateTime date = MAPPER.readValue("[2001,5,25,10,15,30,37]", LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

        MAPPER.setTimeZone(TimeZone.getTimeZone("Asia/Taipei"));
        LocalDateTime date2 = MAPPER.readValue(quote("2007-06-30T08:34:09.001"), LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());
        
        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalDateTime.class));
    }
    
    public void testLocalDateTimeDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalDateTime.class, ObjectConfiguration.class);

        // couple of acceptable formats again:
        LocalDateTime date = mapper.readValue("[\"org.joda.time.LocalDateTime\",[2001,5,25,10,15,30,37]]", LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());

        LocalDateTime date2 = mapper.readValue("[\"org.joda.time.LocalDateTime\",\"2007-06-30T08:34:09.001\"]", LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());
    }

    /*
    /**********************************************************
    /* Tests for Period type
    /**********************************************************
     */

    public void testPeriodDeser() throws IOException
    {
        Period out = MAPPER.readValue(quote("PT1H2M3.004S"), Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        assertEquals(4, out.getMillis());

        // also, should work as number:
        String json = String.valueOf(1000 * out.toStandardSeconds().getSeconds());
        out = MAPPER.readValue(json, Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        // but millis are actually truncated...
        assertEquals(0, out.getMillis());
    }

    public void testPeriodDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Period.class, ObjectConfiguration.class);

        Period out = mapper.readValue("[\"org.joda.time.Period\",\"PT1H2M3.004S\"]", Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        assertEquals(4, out.getMillis());

        // also, should work as number:
        String json = "[\"org.joda.time.Period\"," + String.valueOf(1000 * out.toStandardSeconds().getSeconds()) + "]";
        out = mapper.readValue(json, Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        // but millis are actually truncated...
        assertEquals(0, out.getMillis());
    }

    /*
    /**********************************************************
    /* Tests for Duration type
    /**********************************************************
     */

    public void testDurationDeserFromInt() throws IOException
    {
        Duration d = MAPPER.readValue("1234", Duration.class);
        assertEquals(1234, d.getMillis());
    }

    public void testDurationDeserFromString() throws IOException
    {
        Duration d = MAPPER.readValue(quote("PT1.234S"), Duration.class);
        assertEquals(1234, d.getMillis());
    }

    public void testDurationRoundtrip() throws IOException
    {
        Duration d = new Duration(5513);
        assertEquals(d, MAPPER.readValue(MAPPER.writeValueAsString(d), Duration.class));
    }

    public void testDurationFailsDeserializingUnexpectedType() throws IOException
    {
        try {
            MAPPER.readValue("{\"foo\":1234}", Duration.class);
            fail();
        } catch (JsonMappingException e) {
            // there's location info involving a string object id on the second line, so just use the first line
            assertEquals("expected JSON Number or String", e.getMessage().split("\n")[0]);
        }
    }

    public void testDurationDeserFromIntWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Duration.class, ObjectConfiguration.class);

        Duration d = mapper.readValue("[\"org.joda.time.Duration\",1234]", Duration.class);
        assertEquals(1234, d.getMillis());
    }

    public void testDeserInstantFromNumber() throws IOException
    {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(Calendar.YEAR, 1972);
        long timepoint = cal.getTime().getTime();

        // Ok, first: using JSON number (milliseconds since epoch)
        Instant instant = MAPPER.readValue(String.valueOf(timepoint), Instant.class);
        assertEquals(timepoint, instant.getMillis());
    }

    public void testDeserInstant() throws IOException
    {
        Instant date = MAPPER.readValue(quote("1972-12-28T12:00:01.000Z"), Instant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), Instant.class));
    }

    /*
    /**********************************************************
    /* Tests for DateTimeZone
    /**********************************************************
     */
 
    // [datatype-joda#82]
    public void testSimpleDateTimeZone() throws Exception
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        DateTimeZone input = DateTimeZone.forTimeZone(timeZone);
        String json = MAPPER.writeValueAsString(input);
        assertEquals(quote("-06:00"), json);

        DateTimeZone result = MAPPER.readValue(json, DateTimeZone.class);
        assertEquals(result, input);

        // But then verify polymorphic handling
        DateTimeZoneWrapper input2 = new DateTimeZoneWrapper(input);
        json = MAPPER.writeValueAsString(input2);
        if (json.indexOf("DateTimeZone") < 0) {
            fail("Should contain type information, json = "+json);
        }

        DateTimeZoneWrapper result2 = MAPPER.readValue(json, DateTimeZoneWrapper.class);    
        assertNotNull(result2.tz);
        assertEquals(input, result2.tz);
    }

    /*
    /**********************************************************
    /* Tests for key deserializers
    /**********************************************************
     */
    
    public void testDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("1970-01-01T00:00:00.000Z") + ":0}";
        final Map<DateTime, Long> map = MAPPER.readValue(json, new TypeReference<Map<DateTime, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(DateTime.parse("1970-01-01T00:00:00.000Z")));
    }

    public void testLocalDateKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23") + ":0}";
        final Map<LocalDate, Long> map = MAPPER.readValue(json, new TypeReference<Map<LocalDate, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(LocalDate.parse("2014-05-23")));
    }

    public void testLocalTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("00:00:00.000") + ":0}";
        final Map<LocalTime, Long> map = MAPPER.readValue(json, new TypeReference<Map<LocalTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalTime.parse("00:00:00.000")));
    }
    public void testLocalDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23T00:00:00.000") + ":0}";
        final Map<LocalDateTime, Long> map = MAPPER.readValue(json, new TypeReference<Map<LocalDateTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalDateTime.parse("2014-05-23T00:00:00.000")));
    }
    public void testDurationKeyDeserialize() throws IOException {

        final String json = "{" + quote("PT60s") + ":0}";
        final Map<Duration, Long> map = MAPPER.readValue(json, new TypeReference<Map<Duration, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(Duration.standardMinutes(1L)));
    }
    public void testPeriodKeyDeserialize() throws IOException {

        final String json = "{" + quote("PT1H2M3.004S") + ":0}";
        final Map<Period, Long> map = MAPPER.readValue(json, new TypeReference<Map<Period, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(new Period(1, 2, 3, 4)));
    }

    public void testDeserMonthDay() throws Exception
    {
        String monthDayString = new MonthDay(7, 23).toString();
        MonthDay monthDay = MAPPER.readValue(quote(monthDayString), MonthDay.class);
        assertEquals(new MonthDay(7, 23), monthDay);
    }

    public void testDeserMonthDayWithTimeZone() throws Exception
    {
        MAPPER.setTimeZone(TimeZone.getTimeZone("Europe/Paris"));
        
        String monthDayString = new MonthDay(7, 23).toString();
        MonthDay monthDay = MAPPER.readValue(quote(monthDayString), MonthDay.class);
        assertEquals(new MonthDay(7, 23), monthDay);
        
        assertEquals(ISOChronology.getInstanceUTC(), monthDay.getChronology());
    }
    
    public void testDeserMonthDayFromEmptyString() throws Exception
    {
        MonthDay monthDay = MAPPER.readValue(quote(""), MonthDay.class);
        assertNull(monthDay);
    }

    public void testDeserMonthDayFailsForUnexpectedType() throws IOException
    {
        try
        {
            MAPPER.readValue("{\"month\":8}", MonthDay.class);
            fail();
        } catch (JsonMappingException e)
        {
            assertTrue(e.getMessage().contains("expected JSON String"));
        }
    }

    public void testDeserYearMonth() throws Exception
    {
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = MAPPER.readValue(quote(yearMonthString), YearMonth.class);
        assertEquals(new YearMonth(2013, 8), yearMonth);
    }

    public void testDeserYearMonthWithTimeZone() throws Exception
    {
        MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = MAPPER.readValue(quote(yearMonthString), YearMonth.class);
        assertEquals(new YearMonth(2013, 8), yearMonth);
        assertEquals(ISOChronology.getInstanceUTC(), yearMonth.getChronology());
    }

    /*
    /**********************************************************
    /* Misc other tests
    /**********************************************************
     */
    
    public void testDeserYearMonthFromEmptyString() throws Exception
    {
        YearMonth yearMonth = MAPPER.readValue(quote(""), YearMonth.class);
        assertNull(yearMonth);
    }

    public void testDeserYearMonthFailsForUnexpectedType() throws IOException
    {
        try
        {
            MAPPER.readValue("{\"year\":2013}", YearMonth.class);
            fail();
        } catch (JsonMappingException e)
        {
            assertTrue(e.getMessage().contains("expected JSON String"));
        }
    }

}
