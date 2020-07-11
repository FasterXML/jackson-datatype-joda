package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.joda.time.Instant;
import org.joda.time.MonthDay;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class MonthDayDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();
    
    public void testDeserMonthDay() throws Exception
    {
        String monthDayString = new MonthDay(7, 23).toString();
        MonthDay monthDay = MAPPER.readValue(quote(monthDayString), MonthDay.class);
        assertEquals(new MonthDay(7, 23), monthDay);
    }

    public void testDeserMonthDayWithTimeZone() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Europe/Paris"));
        
        String monthDayString = new MonthDay(7, 23).toString();
        MonthDay monthDay = mapper.readValue(quote(monthDayString), MonthDay.class);
        assertEquals(new MonthDay(7, 23), monthDay);
        
        assertEquals(ISOChronology.getInstanceUTC(), monthDay.getChronology());
    }
    
    public void testDeserMonthDayFromEmptyString() throws Exception
    {
        MonthDay monthDay = MAPPER.readValue(quote(""), MonthDay.class);
        assertNull(monthDay);
    }

    public void testDeserMonthDayFailsForUnexpectedType() throws Exception
    {
        try {
            MAPPER.readValue("{\"month\":8}", MonthDay.class);
            fail();
        } catch (MismatchedInputException e) {
            verifyException(e, "expected JSON String");
        }
    }

    public void testDeserMonthDayCustomFormat() throws Exception
    {
        FormattedMonthDay input = MAPPER.readValue(aposToQuotes(
                "{'value':'12:20'}"),
                FormattedMonthDay.class);
        MonthDay monthDay = input.value;
        assertEquals(12, monthDay.getMonthOfYear());
        assertEquals(20, monthDay.getDayOfMonth());
    }

    public void testDeserMonthDayConfigOverride() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .withConfigOverride(MonthDay.class,
                        co -> co.setFormat(JsonFormat.Value.forPattern("MM|dd")))
                .build();
        final MonthDay input = new MonthDay(12, 20);
        final String exp = quote("12|20");
        assertEquals(exp, mapper.writeValueAsString(input));
        final MonthDay result = mapper.readValue(exp, MonthDay.class);
        assertEquals(input, result);
    }

    public void testDeserMonthDayWhichWasSerializedWithoutJodaModule() throws IOException {
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        cal.set(2020, Calendar.JUNE, 20, 11, 31, 44);
        cal.set(Calendar.MILLISECOND, 276);
        long timepoint = cal.getTime().getTime();
        Instant instant = new Instant(timepoint);
        MonthDay expectedMonthDay = new MonthDay(instant);
        String json = mapper().writeValueAsString(expectedMonthDay);
        MonthDay monthDay = mapperWithModule().readValue(json, MonthDay.class);
        assertEquals(expectedMonthDay, monthDay);
    }
}
