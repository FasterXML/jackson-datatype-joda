package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
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

    @Test
    public void testLocalDateDeserFailsForPartialArray() throws IOException
    {
        ObjectMapper mapper = mapperWithModule();
        for (String doc : new String[] { "[]", "[2001]", "[2001,5]" }) {
            try {
                LocalDate result = mapper.readValue(doc, LocalDate.class);
                fail("Should not pass for '"+doc+"', got: "+result);
            } catch (MismatchedInputException e) {
                verifyException(e, "expected 3 int values for LocalDate");
            }
        }
    }

    @Test
    public void testLocalDateDeserFailsForInvalidArrayValues() throws IOException
    {
        ObjectMapper mapper = mapperWithModule();
        try {
            LocalDate result = mapper.readValue("[2001,2,30]", LocalDate.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Value 30 for dayOfMonth must be in the range [1,28]");
        }
        // in 2.x `null` is coerced to 0 by default (so fails as out-of-range value)
        try {
            LocalDate result = mapper.readValue("[2001,null,30]", LocalDate.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Value 0 for monthOfYear must be in the range [1,12]");
        }
        // ... unless `null`s are not allowed for primitives
        try {
            LocalDate result = mapper.readerFor(LocalDate.class)
                    .with(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                    .readValue("[2001,null,30]");
            fail("Should not pass, got: "+result);
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot coerce `null` to `int` for element #2 of `org.joda.time.LocalDate` Array value");
        }
    }

    @Test
    public void testLocalDateDeserFromTimestampWithoutAdjust() throws IOException
    {
        final TimeZone la = TimeZone.getTimeZone("America/Los_Angeles");
        // with adjustment (default), context zone is used
        assertEquals(new LocalDate(2009, 3, 31),
                mapperWithModule(la).readValue("1238558582001", LocalDate.class));
        // without, timestamp resolved as UTC
        ObjectMapper mapper = jodaMapperBuilder(la)
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        assertEquals(new LocalDate(2009, 4, 1),
                mapper.readValue("1238558582001", LocalDate.class));
    }
}
