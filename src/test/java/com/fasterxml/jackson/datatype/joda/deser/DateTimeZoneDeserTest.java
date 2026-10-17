package com.fasterxml.jackson.datatype.joda.deser;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.util.TokenBuffer;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import com.fasterxml.jackson.datatype.joda.deser.DateTimeDeserTest.DateTimeZoneWrapper;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeZoneDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = jodaMapper();
    private final ObjectReader READER = MAPPER.readerFor(DateTimeZone.class);

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [datatype-joda#82]
    @Test
    public void testSimpleDateTimeZone() throws Exception
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        DateTimeZone input = DateTimeZone.forTimeZone(timeZone);
        String json = MAPPER.writeValueAsString(input);
        assertEquals(quote("-06:00"), json);

        DateTimeZone result = READER.readValue(json);
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
    /* Coercion tests
    /**********************************************************
     */

    // @since 2.12
    @Test
    public void testReadFromEmptyString() throws Exception
    {
        // By default, fine to deser from empty or blank
        assertNull(READER.readValue(quote("")));
        assertNull(READER.readValue(quote("    ")));

        final ObjectMapper m = mapperWithFailFromEmptyString();
        try {
            m.readerFor(DateTimeZone.class)
                .readValue(quote(""));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot coerce empty String");
            verifyException(e, DateTimeZone.class.getName());
        }
    }

    // [datatype-joda#197]: valid offset as timestamp String still works
    @Test
    public void testDeserFromOffsetTimestampString() throws Exception
    {
        assertEquals(DateTimeZone.forOffsetHours(5), _readUntyped("5"));
        assertEquals(DateTimeZone.forOffsetHours(-3), _readUntyped("-3"));
        assertEquals(DateTimeZone.forOffsetHours(23), _readUntyped("23"));
        assertEquals(DateTimeZone.forOffsetHours(-23), _readUntyped("-23"));
    }

    // [datatype-joda#197]: invalid offset as timestamp String must be reported,
    //   not escape as raw exception
    @Test
    public void testDeserFailsForInvalidOffsetTimestampString() throws Exception
    {
        // in `int` range: interpreted as hours, reported as number
        for (String value : new String[] { "24", "-24", "2147483647", "+9999" }) {
            try {
                DateTimeZone result = _readUntyped(value);
                fail("Should not pass for '"+value+"', got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "Hours out of range");
                assertEquals(Integer.valueOf(value), e.getValue());
            }
        }
        // out of `int` range: reported as String
        for (String value : new String[] { "2147483648", "-2147483649", "9999999999",
                "99999999999999999999" }) {
            try {
                DateTimeZone result = _readUntyped(value);
                fail("Should not pass for '"+value+"', got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "from String \""+value+"\"");
                verifyException(e, "Offset value out of range of `int`");
                assertEquals(value, e.getValue());
            }
        }
    }

    // [datatype-joda#197]: invalid offset as JSON number must be reported,
    //   not escape as raw exception
    @Test
    public void testDeserFailsForInvalidOffsetNumber() throws Exception
    {
        for (String value : new String[] { "24", "-24", "2147483647" }) {
            try {
                DateTimeZone result = READER.readValue(value);
                fail("Should not pass for "+value+", got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "from number "+value);
                verifyException(e, "Hours out of range");
                assertEquals(Integer.valueOf(value), e.getValue());
            }
        }
        for (String value : new String[] { "2147483648", "-2147483649", "99999999999999999999" }) {
            try {
                DateTimeZone result = READER.readValue(value);
                fail("Should not pass for "+value+", got: "+result);
            } catch (InvalidFormatException e) {
                verifyException(e, "from number "+value);
                verifyException(e, "Offset value out of range of `int`");
                assertEquals(value, String.valueOf(e.getValue()));
            }
        }
    }

    // [datatype-joda#197]: in-range offset as `long` (as with typed binary
    //   formats like Avro) must still be accepted; out-of-range reported
    @Test
    public void testDeserFromOffsetAsLong() throws Exception
    {
        assertEquals(DateTimeZone.forOffsetHours(5), _readLong(5L));
        try {
            DateTimeZone result = _readLong(5_000_000_000L);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Offset value out of range of `int`");
            assertEquals(Long.valueOf(5_000_000_000L), e.getValue());
        }
    }

    // [datatype-joda#197]: `_fromOffset()` override must be used for
    //   timestamp String too, not just number
    @Test
    public void testFromOffsetOverrideUsedForTimestampString() throws Exception
    {
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .addModule(new SimpleModule().addDeserializer(DateTimeZone.class,
                        new DateTimeZoneDeserializer() {
                            @Override
                            protected DateTimeZone _fromOffset(DeserializationContext ctxt,
                                    int offset) {
                                return DateTimeZone.forOffsetMillis(offset * 60_000);
                            }
                        }))
                .build();
        final DateTimeZone expected = DateTimeZone.forOffsetHoursMinutes(5, 30);
        assertEquals(expected, mapper.readValue("330", DateTimeZone.class));
        try (JsonParser p = new UntypedScalarsParser(mapper.createParser(quote("330")))) {
            assertEquals(expected, mapper.readValue(p, DateTimeZone.class));
        }
    }

    private DateTimeZone _readUntyped(String value) throws Exception
    {
        try (JsonParser p = new UntypedScalarsParser(MAPPER.createParser(quote(value)))) {
            return MAPPER.readValue(p, DateTimeZone.class);
        }
    }

    private DateTimeZone _readLong(long value) throws Exception
    {
        try (TokenBuffer buf = new TokenBuffer(MAPPER, false)) {
            buf.writeNumber(value);
            try (JsonParser p = buf.asParser()) {
                assertNotNull(p.nextToken());
                assertEquals(JsonParser.NumberType.LONG, p.getNumberType());
                return MAPPER.readValue(p, DateTimeZone.class);
            }
        }
    }
}
