package tools.jackson.datatype.joda.deser;

import java.util.Map;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeParser;
import org.joda.time.format.DateTimeParserBucket;
import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.joda.JodaTestBase;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import tools.jackson.datatype.joda.deser.key.LocalDateKeyDeserializer;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests to verify that {@link ArithmeticException} thrown by Joda (for overflows)
 * is reported as {@link InvalidFormatException}, not leaked as is
 * ([datatype-joda#200]). Since default ISO parsers do not throw it for any
 * input, a custom parser that always throws is used.
 */
public class ArithmeticFailureDeserTest extends JodaTestBase
{
    private final static String FAIL_MSG = "test overflow";

    static class OverflowingParser implements DateTimeParser {
        @Override
        public int estimateParsedLength() { return 1; }

        @Override
        public int parseInto(DateTimeParserBucket bucket, String text, int position) {
            throw new ArithmeticException(FAIL_MSG);
        }
    }

    static class OverflowingKeyDeserializer extends LocalDateKeyDeserializer {
        @Override
        protected LocalDate deserialize(String key, DeserializationContext ctxt) {
            return (LocalDate) _parseKey(ctxt, LocalDate.class, key, k -> {
                throw new ArithmeticException(FAIL_MSG);
            });
        }
    }

    private final static JacksonJodaDateFormat OVERFLOWING_FORMAT =
            new JacksonJodaDateFormat(new DateTimeFormatter(null, new OverflowingParser()));

    @SuppressWarnings("unchecked")
    private final ObjectMapper MAPPER = JsonMapper.builder()
            .addModule(new SimpleModule()
                    .addDeserializer(DateTime.class,
                            (ValueDeserializer<DateTime>) (ValueDeserializer<?>)
                            new DateTimeDeserializer(DateTime.class, OVERFLOWING_FORMAT))
                    .addDeserializer(Instant.class, new InstantDeserializer(OVERFLOWING_FORMAT))
                    .addDeserializer(LocalDate.class, new LocalDateDeserializer(OVERFLOWING_FORMAT))
                    .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(OVERFLOWING_FORMAT))
                    .addDeserializer(LocalTime.class, new LocalTimeDeserializer(OVERFLOWING_FORMAT))
                    .addDeserializer(MonthDay.class, new MonthDayDeserializer(OVERFLOWING_FORMAT))
                    .addDeserializer(YearMonth.class, new YearMonthDeserializer(OVERFLOWING_FORMAT))
                    .addKeyDeserializer(LocalDate.class, new OverflowingKeyDeserializer()))
            .build();

    @Test
    public void testValueDeserializers() throws Exception
    {
        _verifyFailure(DateTime.class, "2020-01-01");
        _verifyFailure(Instant.class, "2020-01-01");
        _verifyFailure(LocalDate.class, "2020-01-01");
        _verifyFailure(LocalDateTime.class, "2020-01-01");
        _verifyFailure(LocalTime.class, "10:00");
        _verifyFailure(MonthDay.class, "--01-01");
        _verifyFailure(YearMonth.class, "2020-01");
    }

    @Test
    public void testDateTimeWithBrackets() throws Exception
    {
        // with valid zone id suffix
        _verifyFailure(DateTime.class, "2020-01-01[UTC]");
        // not a valid zone id suffix (custom pattern may contain literal '[')
        _verifyFailure(DateTime.class, "2020-01-01[UTC");
    }

    @Test
    public void testKeyDeserializer() throws Exception
    {
        try {
            Object result = MAPPER.readValue(a2q("{'2020-01-01':1}"),
                    new TypeReference<Map<LocalDate, Integer>>() { });
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize Map key of type `org.joda.time.LocalDate`");
            verifyException(e, FAIL_MSG);
            assertEquals("2020-01-01", e.getValue());
        }
    }

    private void _verifyFailure(Class<?> type, String value) throws Exception
    {
        try {
            Object result = MAPPER.readValue(q(value), type);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `"+type.getName()+"` from String");
            verifyException(e, FAIL_MSG);
            assertEquals(value, e.getValue());
        }
    }
}
