package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.chrono.ISOChronology;
import org.joda.time.format.ISODateTimeFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.joda.JodaTestBase;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import static org.junit.jupiter.api.Assertions.*;

public class LocalDateTimeDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    @Test
    public void testLocalDateTimeDeser() throws IOException
    {
        ObjectMapper mapper = mapperWithModule();
        // couple of acceptable formats again:
        LocalDateTime date = mapper.readValue("[2001,5,25,10,15,30,37]", LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());

        LocalDateTime date2 = mapper.readValue(quote("2007-06-30T08:34:09.001"), LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());

        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), LocalDateTime.class));

        // MAPPER is using default TimeZone (GMT)
        LocalDateTime date3 = mapper.readValue("1238558582001", LocalDateTime.class);
        assertEquals(2009, date3.getYear());
        assertEquals(4, date3.getMonthOfYear());
        assertEquals(1, date3.getDayOfMonth());
        assertEquals(4, date3.getHourOfDay());
        assertEquals(3, date3.getMinuteOfHour());
        assertEquals(2, date3.getSecondOfMinute());
        assertEquals(1, date3.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date3.getChronology());
    }

    @Test
    public void testLocalDateTimeDeserWithTimeZone() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        
        // couple of acceptable formats again:
        LocalDateTime date = mapper.readValue("[2001,5,25,10,15,30,37]", LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());

        mapper = mapperWithModule(TimeZone.getTimeZone("Asia/Taipei"));
        LocalDateTime date2 = mapper.readValue(quote("2007-06-30T08:34:09.001"), LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());
        assertEquals(ISOChronology.getInstanceUTC(), date.getChronology());
        
        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), LocalDateTime.class));
    }
    
    @Test
    public void testLocalDateTimeDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalDateTime.class, ObjectConfiguration.class)
                .build();

        // couple of acceptable formats again:
        LocalDateTime date = mapper.readValue("[\"org.joda.time.LocalDateTime\",[2001,5,25,10,15,30,37]]",
                LocalDateTime.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        assertEquals(10, date.getHourOfDay());
        assertEquals(15, date.getMinuteOfHour());
        assertEquals(30, date.getSecondOfMinute());
        assertEquals(37, date.getMillisOfSecond());

        LocalDateTime date2 = mapper.readValue("[\"org.joda.time.LocalDateTime\",\"2007-06-30T08:34:09.001\"]",
                LocalDateTime.class);
        assertEquals(2007, date2.getYear());
        assertEquals(6, date2.getMonthOfYear());
        assertEquals(30, date2.getDayOfMonth());

        assertEquals(8, date2.getHourOfDay());
        assertEquals(34, date2.getMinuteOfHour());
        assertEquals(9, date2.getSecondOfMinute());
        assertEquals(1, date2.getMillisOfSecond());
    }

    @Test
    public void testLocalDateTimeDeserFromTimestampWithoutAdjust() throws IOException
    {
        final TimeZone la = TimeZone.getTimeZone("America/Los_Angeles");
        // with adjustment (default), context zone is used
        assertEquals(new LocalDateTime(2009, 3, 31, 21, 3, 2, 1),
                mapperWithModule(la).readValue("1238558582001", LocalDateTime.class));
        // without, timestamp resolved as UTC
        ObjectMapper mapper = jodaMapperBuilder(la)
                .disable(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
                .build();
        assertEquals(new LocalDateTime(2009, 4, 1, 4, 3, 2, 1),
                mapper.readValue("1238558582001", LocalDateTime.class));
    }

    @Test
    public void testLocalDateTimeDeserWithFormatTimeZoneSameAsDefault() throws IOException
    {
        // Default format whose (non-explicit) zone is the same as the configured one:
        // mimics default formats (that use JVM default zone) without depending on JVM zone.
        // Configured zone must still be considered explicit and win over context zone (UTC)
        final DateTimeZone taipei = DateTimeZone.forID("Asia/Taipei");
        JacksonJodaDateFormat fmt = new JacksonJodaDateFormat(
                ISODateTimeFormat.localDateOptionalTimeParser().withZone(taipei));
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addModule(new SimpleModule()
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(fmt)))
                .withConfigOverride(LocalDateTime.class,
                        o -> o.setFormat(JsonFormat.Value.empty().withTimeZone(taipei.toTimeZone())))
                .build();
        assertEquals(new LocalDateTime(2009, 4, 1, 12, 3, 2, 1),
                mapper.readValue("1238558582001", LocalDateTime.class));
    }
}
