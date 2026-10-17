package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.core.JsonParser;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.joda.JodaTestBase;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class LocalTimeDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    static class LocalTimeAsTimestamp {
        @JsonFormat(timezone = "America/Los_Angeles")
        public LocalTime value;
    }

    static class LocalTimeTaipei {
        @JsonFormat(timezone = "Asia/Taipei")
        public LocalTime value;
    }

    static class LocalTimeNoAdjust {
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        public LocalTime value;
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests for LocalTime type
    /**********************************************************
     */

    @Test
    public void testLocalTimeDeser() throws IOException
    {
        // couple of acceptable formats, so:
        LocalTime time = MAPPER.readValue("[23,59,1,222]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(222, time.getMillisOfSecond());

        LocalTime time2 = MAPPER.readValue(quote("13:45:22"), LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), LocalTime.class));
    }

    @Test
    public void testLocalTimeDeserWithPartsAsString() throws IOException
    {
        LocalTime time = MAPPER.readValue("[\"23\",\"59\",\"1\",\"222\"]", LocalTime.class);
        assertEquals(new LocalTime(23, 59, 1, 222), time);
    }

    // Partial or malformed JSON Array must be reported with a clear message
    @Test
    public void testLocalTimeDeserFailsForPartialArray() throws IOException
    {
        for (String doc : new String[] { "[]", "[23]", "[23,59]" }) {
            try {
                LocalTime result = MAPPER.readValue(doc, LocalTime.class);
                fail("Should not pass for '"+doc+"', got: "+result);
            } catch (MismatchedInputException e) {
                verifyException(e, "expected 3 or 4 int values for LocalTime");
            }
        }
        try {
            LocalTime result = MAPPER.readValue("[23,59,1,\"x\"]", LocalTime.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "not a valid `int` value");
        }
        try {
            LocalTime result = MAPPER.readValue("[23,59,1,222,1]", LocalTime.class);
            fail("Should not pass, got: "+result);
        } catch (MismatchedInputException e) {
            verifyException(e, "after LocalTime ints");
        }
        try {
            LocalTime result = MAPPER.readValue("[24,59,1]", LocalTime.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Value 24 for hourOfDay must be in the range [0,23]");
        }
    }

    @Test
    public void testLocalTimeDeserWithTimeZone() throws IOException
    {
        final String trickyInstant = "1238558582001";

        // MAPPER is using default TimeZone (GMT)
        LocalTime time = MAPPER.readValue(trickyInstant, LocalTime.class);
        assertEquals(4, time.getHourOfDay());
        assertEquals(3, time.getMinuteOfHour());
        assertEquals(2, time.getSecondOfMinute());
        assertEquals(1, time.getMillisOfSecond());

        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        LocalTime time2 = mapper.readValue(trickyInstant, LocalTime.class);
        assertEquals(21, time2.getHourOfDay());
        assertEquals(3, time2.getMinuteOfHour());
        assertEquals(2, time2.getSecondOfMinute());
        assertEquals(1, time2.getMillisOfSecond());

        mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        LocalTime time3 = mapper.readValue(trickyInstant, LocalTime.class);
        assertEquals(12, time3.getHourOfDay());
        assertEquals(3, time3.getMinuteOfHour());
        assertEquals(2, time3.getSecondOfMinute());
        assertEquals(1, time3.getMillisOfSecond());
    }

    // Edge-of-long timestamp with non-UTC time zone makes Joda's zone offset
    // addition overflow: must be reported, not escape as raw `ArithmeticException`
    @Test
    public void testLocalTimeDeserFailsForOverflowingTimestamp() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        try {
            LocalTime result = mapper.readValue(String.valueOf(Long.MAX_VALUE), LocalTime.class);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Adding time zone offset caused overflow");
            assertEquals(Long.valueOf(Long.MAX_VALUE), e.getValue());
        }
        // and from the other end, with a negative-offset zone
        mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        try {
            LocalTime result = mapper.readValue(String.valueOf(Long.MIN_VALUE), LocalTime.class);
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
    public void testLocalTimeDeserFailsForOutOfRangeTimestamp() throws IOException
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
            LocalTime result = mapper.readValue(String.valueOf(ts), LocalTime.class);
            fail("Should not pass, got: "+result.getClass().getName());
        } catch (InvalidFormatException e) {
            verifyException(e, "for year must be in the range");
            assertEquals(Long.valueOf(ts), e.getValue());
        }
        // and same for "timestamp as String" from textual formats (like XML, CSV):
        // reported with original String value
        final String text = String.valueOf(ts);
        try (JsonParser p = new UntypedScalarsParser(mapper.createParser(quote(text)))) {
            LocalTime result = mapper.readValue(p, LocalTime.class);
            fail("Should not pass, got: "+result.getClass().getName());
        } catch (InvalidFormatException e) {
            verifyException(e, "for year must be in the range");
            assertEquals(text, e.getValue());
        }
    }

    @Test
    public void testLocalTimeDeserWithFormatTimeZone() throws IOException
    {
        // and `@JsonFormat.timezone` has to win over the context one
        LocalTimeAsTimestamp wrapper = MAPPER.readValue(
                a2q("{'value':1238558582001}"), LocalTimeAsTimestamp.class);
        assertEquals(21, wrapper.value.getHourOfDay());
        assertEquals(3, wrapper.value.getMinuteOfHour());
        assertEquals(2, wrapper.value.getSecondOfMinute());
        assertEquals(1, wrapper.value.getMillisOfSecond());

        // also with a non-UTC context time zone, to verify precedence
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        wrapper = mapper.readValue(
                a2q("{'value':1238558582001}"), LocalTimeAsTimestamp.class);
        assertEquals(21, wrapper.value.getHourOfDay());
        assertEquals(3, wrapper.value.getMinuteOfHour());
    }

    @Test
    public void testLocalTimeDeserWithoutAdjustToContextTimeZone() throws IOException
    {
        // Context zone must not be applied if adjustment is disabled: resolved as UTC
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        LocalTimeNoAdjust wrapper = mapper.readValue(
                a2q("{'value':1238558582001}"), LocalTimeNoAdjust.class);
        assertEquals(4, wrapper.value.getHourOfDay());
        assertEquals(3, wrapper.value.getMinuteOfHour());
        assertEquals(2, wrapper.value.getSecondOfMinute());
        assertEquals(1, wrapper.value.getMillisOfSecond());

        // and same if disabled globally
        mapper = jodaMapperBuilder(TimeZone.getTimeZone("Asia/Taipei"))
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        assertEquals(new LocalTime(4, 3, 2, 1),
                mapper.readValue("1238558582001", LocalTime.class));
    }

    @Test
    public void testLocalTimeDeserFromTimestampString() throws IOException
    {
        final String trickyInstant = quote("1238558582001");

        LocalTime time;
        try (JsonParser p = new UntypedScalarsParser(MAPPER.createParser(trickyInstant))) {
            time = MAPPER.readValue(p, LocalTime.class);
        }
        assertEquals(4, time.getHourOfDay());
        assertEquals(3, time.getMinuteOfHour());
        assertEquals(2, time.getSecondOfMinute());
        assertEquals(1, time.getMillisOfSecond());

        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        try (JsonParser p = new UntypedScalarsParser(mapper.createParser(trickyInstant))) {
            time = mapper.readValue(p, LocalTime.class);
        }
        assertEquals(12, time.getHourOfDay());
        assertEquals(3, time.getMinuteOfHour());
    }

    @Test
    public void testLocalTimeDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalTime.class, ObjectConfiguration.class)
                .build();

        // couple of acceptable formats, so:
        LocalTime time = mapper.readValue("[\"org.joda.time.LocalTime\",[23,59,1,10]]", LocalTime.class);
        assertEquals(23, time.getHourOfDay());
        assertEquals(59, time.getMinuteOfHour());
        assertEquals(1, time.getSecondOfMinute());
        assertEquals(10, time.getMillisOfSecond());

        LocalTime time2 = mapper.readValue("[\"org.joda.time.LocalTime\",\"13:45:22\"]", LocalTime.class);
        assertEquals(13, time2.getHourOfDay());
        assertEquals(45, time2.getMinuteOfHour());
        assertEquals(22, time2.getSecondOfMinute());
        assertEquals(0, time2.getMillisOfSecond());
    }

    @Test
    public void testLocalTimeDeserWithFormatTimeZoneSameAsDefault() throws IOException
    {
        // Default format whose (non-explicit) zone is the same as the annotation's:
        // mimics default formats (that use JVM default zone) without depending on JVM zone.
        // Annotation zone must still be considered explicit and win over context zone (UTC)
        JacksonJodaDateFormat fmt = new JacksonJodaDateFormat(
                ISODateTimeFormat.localTimeParser().withZone(DateTimeZone.forID("Asia/Taipei")));
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addModule(new SimpleModule()
                        .addDeserializer(LocalTime.class, new LocalTimeDeserializer(fmt)))
                .build();
        LocalTimeTaipei wrapper = mapper.readValue(
                a2q("{'value':1238558582001}"), LocalTimeTaipei.class);
        assertEquals(new LocalTime(12, 3, 2, 1), wrapper.value);
    }
}
