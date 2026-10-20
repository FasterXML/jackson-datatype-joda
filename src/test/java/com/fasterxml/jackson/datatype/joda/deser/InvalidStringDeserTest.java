package com.fasterxml.jackson.datatype.joda.deser;

import java.util.Map;

import org.joda.time.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.DeserializationProblemHandler;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

// [datatype-joda#195]: invalid String values must fail with
// `InvalidFormatException`, not raw Joda `IllegalArgumentException`
public class InvalidStringDeserTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testLocalDate() throws Exception
    {
        _verifyInvalid(LocalDate.class, "2001-13-01",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(LocalDate.class, "garbage", "Invalid format");
    }

    @Test
    public void testLocalDateTime() throws Exception
    {
        _verifyInvalid(LocalDateTime.class, "2001-05-25T25:00:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalid(LocalDateTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testLocalTime() throws Exception
    {
        _verifyInvalid(LocalTime.class, "25:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalid(LocalTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testDateTime() throws Exception
    {
        _verifyInvalid(DateTime.class, "2001-13-01",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(DateTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testOtherTypes() throws Exception
    {
        _verifyInvalid(Instant.class, "2001-13-01T00:00:00Z",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(YearMonth.class, "2001-13",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(MonthDay.class, "--02-30",
                "Value 30 for dayOfMonth must be in the range [1,29]");
    }

    @Test
    public void testIntervalDurationTimeZone() throws Exception
    {
        _verifyInvalid(Interval.class, "2001-13-01T00:00:00Z/2002-01-01T00:00:00Z",
                "Value 13 for monthOfYear must be in the range [1,12]");
        // end before start
        _verifyInvalid(Interval.class, "200-100",
                // (Joda 2.9 message has a typo: "must be greater the start")
                "The end instant must be greater");
        _verifyInvalid(Duration.class, "garbage", "Invalid format");
        // years/months cannot be converted to Duration
        _verifyInvalid(Duration.class, "P1Y", "Invalid Duration value");
        _verifyInvalid(DateTimeZone.class, "Not/AZone", "not recognised");
        try {
            Object result = MAPPER.readValue("99", DateTimeZone.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `org.joda.time.DateTimeZone` from number 99");
            assertEquals(99, e.getValue());
        }
    }

    @Test
    public void testMapKeys() throws Exception
    {
        _verifyInvalidKey(new TypeReference<Map<LocalDate, Integer>>() { },
                LocalDate.class, "2001-13-01",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalidKey(new TypeReference<Map<LocalDateTime, Integer>>() { },
                LocalDateTime.class, "2001-05-25T25:00:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalidKey(new TypeReference<Map<LocalTime, Integer>>() { },
                LocalTime.class, "25:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalidKey(new TypeReference<Map<DateTime, Integer>>() { },
                DateTime.class, "garbage", "Invalid format");
        _verifyInvalidKey(new TypeReference<Map<Duration, Integer>>() { },
                Duration.class, "P1Y", "Invalid Duration value");
    }

    @Test
    public void testAsProperty() throws Exception
    {
        try {
            Map<String, LocalDate> result = MAPPER.readValue(a2q("{'date':'2001-13-01'}"),
                    new TypeReference<Map<String, LocalDate>>() { });
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            assertEquals("2001-13-01", e.getValue());
            assertEquals(LocalDate.class, e.getTargetType());
            assertEquals("date", e.getPath().get(0).getFieldName());
        }
    }

    // Should allow `DeserializationProblemHandler` to provide replacement value
    @Test
    public void testWithProblemHandler() throws Exception
    {
        final LocalDate fallback = new LocalDate(1970, 1, 1);
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt,
                            Class<?> targetType, String valueToConvert, String failureMsg) {
                        if (targetType == LocalDate.class) {
                            return fallback;
                        }
                        return NOT_HANDLED;
                    }
                })
                .build();
        assertEquals(fallback, mapper.readValue(q("2001-13-01"), LocalDate.class));
        // but other types not handled
        try {
            Object result = mapper.readValue(q("25:00"), LocalTime.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Value 25 for hourOfDay must be in the range [0,23]");
        }
    }

    // Joda exception retained as cause (for String, timestamp and Array values)
    @Test
    public void testJodaExceptionRetainedAsCause() throws Exception
    {
        for (String doc : new String[] { q("2019-02-30"), "[2019,2,30]",
                String.valueOf(Long.MAX_VALUE) }) {
            try {
                Object result = MAPPER.readValue(doc, LocalDate.class);
                fail("Should not pass, got: "+result);
            } catch (InvalidFormatException e) {
                assertInstanceOf(IllegalArgumentException.class, e.getCause(), "For: "+doc);
            }
        }
    }

    // ... but not added to exception thrown by problem handler, if explicitly
    // constructed without cause
    @Test
    public void testProblemHandlerExceptionWithNullCause() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addHandler(new DeserializationProblemHandler() {
                    @Override
                    public Object handleWeirdStringValue(DeserializationContext ctxt,
                            Class<?> targetType, String valueToConvert, String failureMsg)
                        throws java.io.IOException {
                        throw JsonMappingException.from(ctxt.getParser(), "Handler failure", (Throwable) null);
                    }
                })
                .build();
        try {
            Object result = mapper.readValue(q("2019-02-30"), LocalDate.class);
            fail("Should not pass, got: "+result);
        } catch (DatabindException e) {
            verifyException(e, "Handler failure");
            assertNull(e.getCause());
        }
    }

    private void _verifyInvalidKey(TypeReference<?> mapType, Class<?> keyType,
            String key, String expMsg)
        throws Exception
    {
        try {
            Object result = MAPPER.readValue(a2q("{'"+key+"':1}"), mapType);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize Map key of type `"+keyType.getName()+"`");
            verifyException(e, expMsg);
            assertEquals(key, e.getValue());
            assertEquals(keyType, e.getTargetType());
        }
    }

    private void _verifyInvalid(Class<?> type, String value, String expMsg)
        throws Exception
    {
        try {
            Object result = MAPPER.readValue(q(value), type);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `"+type.getName()+"` from String");
            verifyException(e, expMsg);
            assertEquals(value, e.getValue());
            assertEquals(type, e.getTargetType());
        } catch (DatabindException e) {
            fail("Expected InvalidFormatException, got: "+e);
        }
    }
}
