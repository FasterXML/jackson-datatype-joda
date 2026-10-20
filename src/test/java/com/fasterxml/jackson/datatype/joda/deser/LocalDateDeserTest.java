package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalDate;
import org.joda.time.chrono.ISOChronology;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
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

    // Edge-of-long timestamp with non-UTC time zone makes Joda's zone offset
    // addition overflow: must be reported, not escape as raw `ArithmeticException`
    @Test
    public void testLocalDateDeserFailsForOverflowingTimestamp() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        try {
            LocalDate result = mapper.readValue(String.valueOf(Long.MAX_VALUE), LocalDate.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Adding time zone offset caused overflow");
            assertEquals(Long.valueOf(Long.MAX_VALUE), e.getValue());
        }
        // and from the other end, with a negative-offset zone
        mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        try {
            LocalDate result = mapper.readValue(String.valueOf(Long.MIN_VALUE), LocalDate.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Adding time zone offset caused overflow");
            assertEquals(Long.valueOf(Long.MIN_VALUE), e.getValue());
        }
    }

    // Edge-of-long timestamp for which offset addition does not overflow, but
    // local instant falls outside of supported year range (Joda would silently
    // accept, possibly wrapping around): must be reported
    @Test
    public void testLocalDateDeserFailsForOutOfRangeTimestamp() throws IOException
    {
        _verifyOutOfRangeTimestamp(mapperWithModule(), Long.MAX_VALUE);
        _verifyOutOfRangeTimestamp(mapperWithModule(), Long.MIN_VALUE);
        _verifyOutOfRangeTimestamp(mapperWithModule(TimeZone.getTimeZone("Asia/Taipei")),
                Long.MIN_VALUE);
        _verifyOutOfRangeTimestamp(mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles")),
                Long.MAX_VALUE);
    }

    private void _verifyOutOfRangeTimestamp(ObjectMapper mapper, long ts) throws IOException
    {
        try {
            LocalDate result = mapper.readValue(String.valueOf(ts), LocalDate.class);
            fail("Should not pass, got: "+result.getClass().getName());
        } catch (InvalidFormatException e) {
            verifyException(e, "for year must be in the range");
            assertEquals(Long.valueOf(ts), e.getValue());
        }
        // and same for "timestamp as String" from textual formats (like XML, CSV):
        // reported with original String value
        final String text = String.valueOf(ts);
        try (JsonParser p = new UntypedScalarsParser(mapper.createParser(quote(text)))) {
            LocalDate result = mapper.readValue(p, LocalDate.class);
            fail("Should not pass, got: "+result.getClass().getName());
        } catch (InvalidFormatException e) {
            verifyException(e, "for year must be in the range");
            assertEquals(text, e.getValue());
        }
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

    // Should allow `DeserializationProblemHandler` to provide replacement value
    @Test
    public void testLocalDateDeserInvalidArrayValueWithProblemHandler() throws IOException
    {
        final LocalDate fallback = new LocalDate(1970, 1, 1);
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdNumberValue(DeserializationContext ctxt,
                            Class<?> targetType, Number valueToConvert, String failureMsg) {
                        return fallback;
                    }
                })
                .build();
        assertEquals(fallback, mapper.readValue("[2001,13,1]", LocalDate.class));
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
