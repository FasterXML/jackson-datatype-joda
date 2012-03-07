package com.fasterxml.jackson.datatype.joda;

import java.io.*;
import java.util.*;


import org.joda.time.*;

import com.fasterxml.jackson.databind.*;

/**
 * Unit tests for verifying limited interoperability for Joda time.
 * Basic support is added for handling {@link DateTime}; more can be
 * added over time if and when requested.
 */
public class JodaDeserializationTest extends JodaTestBase
{
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

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), ReadableDateTime.class));
    }

    public void testDeserReadableInstant() throws IOException
    {
        ReadableInstant date = MAPPER.readValue(quote("1972-12-28T12:00:01.000+0000"), ReadableInstant.class);
        assertNotNull(date);
        assertEquals("1972-12-28T12:00:01.000Z", date.toString());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), ReadableInstant.class));
    }
    
    /*
    /**********************************************************
    /* Tests for DateMidnight type
    /**********************************************************
     */

    public void testDateMidnightDeser() throws IOException
    {
        // couple of acceptable formats, so:
        DateMidnight date = MAPPER.readValue("[2001,5,25]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        DateMidnight date2 = MAPPER.readValue(quote("2005-07-13"), DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), DateMidnight.class));
    }
    
    /*
    /**********************************************************
    /* Tests for LocalDate type
    /**********************************************************
     */

    public void testLocalDateDeser() throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        // couple of acceptable formats, so:
        LocalDate date = mapper.readValue("[2001,5,25]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        LocalDate date2 = mapper.readValue(quote("2005-07-13"), LocalDate.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), LocalDate.class));
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
}
