package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.LocalTime;
import org.joda.time.chrono.ISOChronology;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.joda.JodaTestBase;

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
    public void testLocalTimeDeserWithTimeZone() throws IOException
    {
        final String trickyInstant = "1238558582001";

        // MAPPER is using default TimeZone (GMT)
        LocalTime time = MAPPER.readValue(trickyInstant, LocalTime.class);
        assertEquals(4, time.getHourOfDay());
        assertEquals(3, time.getMinuteOfHour());
        assertEquals(2, time.getSecondOfMinute());
        assertEquals(1, time.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), time.getChronology());

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
}
