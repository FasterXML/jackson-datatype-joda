package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalTime;
import org.joda.time.format.ISODateTimeFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.util.JacksonFeatureSet;
import com.fasterxml.jackson.core.util.JsonParserDelegate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class LocalTimeDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = jodaMapper();

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

    // Simulates a textual format (like XML, CSV) where scalars are untyped
    static class UntypedScalarsParser extends JsonParserDelegate {
        public UntypedScalarsParser(JsonParser p) { super(p); }

        @Override
        public JacksonFeatureSet<StreamReadCapability> getReadCapabilities() {
            return super.getReadCapabilities().with(StreamReadCapability.UNTYPED_SCALARS);
        }
    }

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
                .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
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
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalTime.class, ObjectConfiguration.class);

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
