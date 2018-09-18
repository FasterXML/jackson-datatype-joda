package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.MonthDay;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class MonthDayDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();

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

    public void testDeserMonthDayCustomFormat() throws IOException
    {
        FormattedMonthDay input = MAPPER.readValue(aposToQuotes(
                "{'value':'12:20'}"),
                FormattedMonthDay.class);
        MonthDay monthDay = input.value;
        assertEquals(12, monthDay.getMonthOfYear());
        assertEquals(20, monthDay.getDayOfMonth());
    }
}
