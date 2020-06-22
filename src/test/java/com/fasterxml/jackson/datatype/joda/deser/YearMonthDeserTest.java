package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.Instant;
import org.joda.time.YearMonth;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class YearMonthDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    final ObjectMapper MAPPER = mapperWithModule();
    
    public void testDeserYearMonth() throws Exception
    {
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = MAPPER.readValue(quote(yearMonthString), YearMonth.class);
        assertEquals(new YearMonth(2013, 8), yearMonth);
    }

    public void testDeserYearMonthWithTimeZone() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = mapper.readValue(quote(yearMonthString), YearMonth.class);
        assertEquals(new YearMonth(2013, 8), yearMonth);
        assertEquals(ISOChronology.getInstanceUTC(), yearMonth.getChronology());
    }

    public void testDeserYearMonthFromEmptyString() throws Exception
    {
        YearMonth yearMonth = MAPPER.readValue(quote(""), YearMonth.class);
        assertNull(yearMonth);
    }

    public void testDeserYearMonthFailsForUnexpectedType() throws IOException
    {
        try {
            MAPPER.readValue("{\"year\":2013}", YearMonth.class);
            fail();
        } catch (MismatchedInputException e) {
            verifyException(e, "expected JSON String");
        }
    }

    public void testDeserYearMonthCustomFormat() throws IOException
    {
        FormattedYearMonth input = MAPPER.readValue(aposToQuotes(
                "{'value':'2013/8'}"),
                FormattedYearMonth.class);
        YearMonth yearMonth = input.value;
        assertEquals(2013, yearMonth.getYear());
        assertEquals(8, yearMonth.getMonthOfYear());
    }

    public void testDeserYearMonthWhichWasSerializedWithoutJodaModule() throws IOException {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(2020, Calendar.JUNE, 20, 11, 31, 44);
        cal.set(Calendar.MILLISECOND, 276);
        long timepoint = cal.getTime().getTime();
        Instant instant = new Instant(timepoint);
        YearMonth expectedYearMonth = new YearMonth(instant);
        String json = mapper().writeValueAsString(expectedYearMonth);
        YearMonth yearMonth = mapperWithModule().readValue(json, YearMonth.class);
        assertEquals(expectedYearMonth, yearMonth);
    }
}
