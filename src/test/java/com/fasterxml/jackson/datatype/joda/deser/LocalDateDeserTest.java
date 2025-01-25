package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

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

    private final ObjectMapper MAPPER = jodaMapper();

    /*
    /**********************************************************
    /* Tests for LocalDate type
    /**********************************************************
     */

    @Test
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

    @Test
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

        assertNull(MAPPER.readValue(quote(""), LocalDate.class));

        MAPPER.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));

        LocalDate date4 = MAPPER.readValue(trickyInstant, LocalDate.class);
        assertEquals(2009, date4.getYear());
        assertEquals(3, date4.getMonthOfYear());
        assertEquals(31, date4.getDayOfMonth());
        assertEquals(ISOChronology.getInstanceUTC(), date4.getChronology());
    }

    @Test
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

    @Test
    public void testLocalDateDeserWithPartsAsString() throws IOException
    {
        // couple of acceptable formats, so:
        LocalDate date = MAPPER.readValue("[\"2001\",\"5\",\"25\"]", LocalDate.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());
    }
}
