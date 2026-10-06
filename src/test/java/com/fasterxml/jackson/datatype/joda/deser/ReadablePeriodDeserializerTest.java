package com.fasterxml.jackson.datatype.joda.deser;

import org.junit.jupiter.api.Test;

import org.joda.time.*;

import com.fasterxml.jackson.core.exc.InputCoercionException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class ReadablePeriodDeserializerTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = jodaMapper();

    @Test
    public void testDeserializeSeconds() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"seconds\"},\"seconds\":12,\"periodType\":{\"name\":\"Seconds\"}}",
                ReadablePeriod.class );
        assertEquals( Seconds.seconds( 12 ), readablePeriod );
    }

    @Test
    public void testDeserializeMinutes() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"minutes\"},\"minutes\":1,\"periodType\":{\"name\":\"Minutes\"}}",
                ReadablePeriod.class );
        assertEquals( Minutes.minutes( 1 ), readablePeriod );
    }

    @Test
    public void testDeserializeHours() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"hours\"},\"hours\":2,\"periodType\":{\"name\":\"Hours\"}}",
                ReadablePeriod.class );
        assertEquals( Hours.hours( 2 ), readablePeriod );
    }

    @Test
    public void testDeserializeDays() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"days\"},\"days\":2,\"periodType\":{\"name\":\"Days\"}}",
                ReadablePeriod.class );
        assertEquals( Days.days( 2 ), readablePeriod );
    }
	
    @Test
    public void testDeserializeWeeks() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"weeks\"},\"weeks\":2,\"periodType\":{\"name\":\"Weeks\"}}",
                ReadablePeriod.class );
        assertEquals( Weeks.weeks( 2 ), readablePeriod );
    }
	
    @Test
    public void testDeserializeMonths() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"months\"},\"months\":2,\"periodType\":{\"name\":\"Months\"}}",
                ReadablePeriod.class );
        assertEquals( Months.months( 2 ), readablePeriod );
    }

    @Test
    public void testDeserializeYears() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"years\"},\"years\":2,\"periodType\":{\"name\":\"Years\"}}",
                ReadablePeriod.class );
        assertEquals( Years.years( 2 ), readablePeriod );
    }

    @Test
    public void testDeserializeOutOfRangeValueFails() throws Exception
    {
        // 4294967297 used to wrap silently to 1, 2147483648 to Integer.MIN_VALUE
        for (String value : new String[] { "4294967297", "2147483648", "-2147483649",
                "18446744073709551623", "1e10",
                "\"4294967297\"", "\"1e10\"" }) {
            for (Class<?> type : new Class<?>[] { ReadablePeriod.class, Period.class }) {
                try {
                    MAPPER.readValue(_daysJson(value), type);
                    fail("Should not pass for value "+value+" as "+type.getSimpleName());
                } catch (InputCoercionException | MismatchedInputException e) {
                    // same failure as for regular `int` properties
                    verifyException(e, "int");
                }
            }
        }
    }

    @Test
    public void testDeserializeInvalidValueFails() throws Exception
    {
        // fractional (with `ACCEPT_FLOAT_AS_INT` disabled) or non-numeric values
        final ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(DeserializationFeature.ACCEPT_FLOAT_AS_INT)
                .build();
        for (String value : new String[] { "1.5", "true", "[5]", "{\"x\":1}", "\"abc\"" }) {
            try {
                mapper.readValue(_daysJson(value), ReadablePeriod.class);
                fail("Should not pass for value "+value);
            } catch (MismatchedInputException e) {
                verifyException(e, "int");
            }
        }
    }

    @Test
    public void testDeserializeMissingValueFails() throws Exception
    {
        try {
            MAPPER.readValue("{\"fieldType\":{\"name\":\"days\"},\"periodType\":{\"name\":\"Days\"}}",
                    ReadablePeriod.class);
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Missing value property 'days'");
        }
    }

    @Test
    public void testDeserializeIntBoundaries() throws Exception
    {
        assertEquals(Days.days(Integer.MAX_VALUE),
                MAPPER.readValue(_daysJson(String.valueOf(Integer.MAX_VALUE)), ReadablePeriod.class));
        assertEquals(Days.days(Integer.MIN_VALUE),
                MAPPER.readValue(_daysJson(String.valueOf(Integer.MIN_VALUE)), ReadablePeriod.class));
        assertEquals(Days.days(Integer.MAX_VALUE).toPeriod(),
                MAPPER.readValue(_daysJson(String.valueOf(Integer.MAX_VALUE)), Period.class));
        // String-valued numbers still accepted (as with other formats, like XML)
        assertEquals(Days.days(7),
                MAPPER.readValue(_daysJson("\"7\""), ReadablePeriod.class));
    }

    @Test
    public void testDeserializeOutOfRangeScalarFails() throws Exception
    {
        // millis that do not fit `int` seconds etc
        try {
            MAPPER.readValue(String.valueOf(Long.MAX_VALUE), Period.class);
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Invalid Period value");
        }
        // ISO-8601 String with out-of-range component
        for (String value : new String[] { "P4294967297D", "P10000000000D" }) {
            try {
                MAPPER.readValue(q(value), Period.class);
                fail("Should not pass for value "+value);
            } catch (InvalidFormatException e) {
                verifyException(e, "Invalid Period value");
            }
        }
    }

    private static String _daysJson(String value) {
        return "{\"fieldType\":{\"name\":\"days\"},\"days\":"+value+",\"periodType\":{\"name\":\"Days\"}}";
    }
}
