package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class LocalDateDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testLocalDateDeser() throws IOException
    {
        ObjectMapper mapper = jodaMapper();

        // couple of acceptable formats, so:
        LocalDate date = mapper.readValue("[2001,5,25]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        LocalDate date2 = mapper.readValue(quote("2005-07-13"), LocalDate.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        assertNull(mapper.readValue(quote(""), LocalDate.class));
    }

    public void testLocalDateDeserWithTimeZone() throws IOException
    {
        final String trickyInstant = "1238558582001";

        // MAPPER is using default TimeZone (GMT)
        ObjectMapper mapper = jodaMapper();
        LocalDate date3 = mapper.readValue(trickyInstant, LocalDate.class);
        assertEquals(2009, date3.getYear());
        assertEquals(4, date3.getMonthOfYear());
        assertEquals(1, date3.getDayOfMonth());
        assertEquals(ISOChronology.getInstanceUTC(), date3.getChronology());

        mapper = jodaMapper(TimeZone.getTimeZone("America/Los_Angeles"));

        // couple of acceptable formats, so:
        LocalDate date = mapper.readValue("[2001,5,25]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

        mapper = jodaMapper(TimeZone.getTimeZone("Asia/Taipei"));
        LocalDate date2 = mapper
                .readValue(quote("2005-07-13"), LocalDate.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

        assertNull(mapper.readValue(quote(""), LocalDate.class));

        mapper = jodaMapper(TimeZone.getTimeZone("America/Los_Angeles"));

        LocalDate date4 = mapper.readValue(trickyInstant, LocalDate.class);
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
        ObjectMapper mapper = jodaMapper();
        LocalDate date = mapper.readValue("[\"2001\",\"5\",\"25\"]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());
    }
}
