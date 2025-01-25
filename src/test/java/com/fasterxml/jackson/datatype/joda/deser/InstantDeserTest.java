package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.Instant;
import org.joda.time.ReadableInstant;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class InstantDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();

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
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Instant.class, MixinForPolymorphism.class);
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
}
