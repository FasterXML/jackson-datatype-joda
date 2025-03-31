package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.*;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.util.Random;

public class JodaSerializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    static class Container<T> {
        T contents;

        public Container(T contents) {
            this.contents = contents;
        }

        public T getContents() {
            return contents;
        }
    }

    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
        .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
        .build();

    private final ObjectWriter WRITER = MAPPER.writer();
    
    /*
    /**********************************************************
    /* Tests for LocalDate type
    /**********************************************************
     */
    
    public void testLocalDateSer() throws IOException
    {
        LocalDate date = new LocalDate(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        assertEquals(q("2001-05-25"),
                WRITER.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).writeValueAsString(date));

        // We can also configure beans to not include empty values. In this case,
        // JodaDateSerializerBase#isEmpty is called to check if the value is empty.
        ObjectMapper mapper = jodaMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        assertEquals("{\"contents\":[2001,5,25]}", mapper.writeValueAsString(new Container<LocalDate>(date)));

        // also verify pruning by NON_EMPTY
        assertEquals("{}", mapper.writeValueAsString(new Container<LocalDate>(null)));
    }

    public void testLocalDateSerWithTypeInfo() throws IOException
    {
        LocalDate date = new LocalDate(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalDate.class, ObjectConfiguration.class)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals("[\"org.joda.time.LocalDate\",\"2001-05-25\"]", mapper.writeValueAsString(date));
    }

    /*
    /**********************************************************
    /* Tests for LocalTime type
    /**********************************************************
     */

    public void testLocalTimeSer() throws IOException
    {
        LocalTime date = new LocalTime(13,20,54);
        // default format is that of JSON array...
        assertEquals("[13,20,54,0]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals(q("13:20:54.000"), mapper.writeValueAsString(date));

    }

    public void testLocalTimeSerWithFormatOverride() throws IOException
    {
        LocalTime date = new LocalTime(13,20,54);

        // configure a custom serialzer fot the LocalTime
        SimpleModule testModule = new SimpleModule("TestModule");
        testModule.addSerializer(LocalTime.class, new LocalTimeSerializer(
                new JacksonJodaDateFormat(ISODateTimeFormat.hourMinute()), 1));

        ObjectMapper mapper = mapperWithModuleBuilder()
                .addModule(testModule)
                .build();

        assertEquals(q("13:20"), mapper.writeValueAsString(date));

        assertEquals(a2q("{'contents':'13:20'}"), mapper.writeValueAsString(new Container<>(date)));

    }

    public void testLocalTimeSerWithTypeInfo() throws IOException
    {
        LocalTime date = new LocalTime(13,20,54);
        // default format is that of JSON array...
        assertEquals("[13,20,54,0]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalTime.class, ObjectConfiguration.class)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals("[\"org.joda.time.LocalTime\",\"13:20:54.000\"]",
                mapper.writeValueAsString(date));
    }

    /*
    /**********************************************************
    /* Tests for LocalDateTime type
    /**********************************************************
     */
    
    public void testLocalDateTimeSer() throws IOException
    {
        LocalDateTime date = new LocalDateTime(2001, 5, 25,
                10, 15, 30, 37);
        // default format is that of JSON array...
        assertEquals("[2001,5,25,10,15,30,37]", MAPPER.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals(q("2001-05-25T10:15:30.037"), mapper.writeValueAsString(date));
    }
    
    public void testLocalDateTimeSerWithTypeInfo() throws IOException
    {
        LocalDateTime date = new LocalDateTime(2001, 5, 25,
                10, 15, 30, 37);
        // default format is that of JSON array...
        assertEquals("[2001,5,25,10,15,30,37]", MAPPER.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(LocalDateTime.class, ObjectConfiguration.class)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        assertEquals("[\"org.joda.time.LocalDateTime\",\"2001-05-25T10:15:30.037\"]",
                mapper.writeValueAsString(date));
    }

    /*
    /**********************************************************
    /* Tests for Period type
    /**********************************************************
     */

    public void testPeriodSer() throws IOException
    {
        Period in = new Period(1, 2, 3, 4);
        assertEquals(q("PT1H2M3.004S"), MAPPER.writeValueAsString(in));
    }
    
    public void testPeriodSerWithTypeInfo() throws IOException
    {
        Period in = new Period(1, 2, 3, 4);
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(Period.class, ObjectConfiguration.class)
                .build();
        assertEquals("[\"org.joda.time.Period\",\"PT1H2M3.004S\"]", mapper.writeValueAsString(in));
    }

    /*
    /**********************************************************
    /* Tests for Duration type
    /**********************************************************
     */

    public void testDurationSer() throws IOException
    {
        Duration d = new Duration(3123422);
        String json = MAPPER.writeValueAsString(d);
        assertEquals("3123422", json);

        assertEquals(q("PT3123.422S"), MAPPER.writer()
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(d));
    }
    
    public void testDurationSerWithTypeInfo() throws IOException
    {
        Duration d = new Duration(3123422);
        ObjectMapper mapper = mapperWithModuleBuilder()
                .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .addMixIn(Duration.class, ObjectConfiguration.class)
                .build();
        String json = mapper.writeValueAsString(d);
        assertEquals("[\"org.joda.time.Duration\",3123422]", json);
    }

    public void testMonthDaySer() throws Exception
    {
        MonthDay monthDay = new MonthDay(7, 23);
        String json = MAPPER.writeValueAsString(monthDay);
        assertEquals(q("--07-23"), json);
    }

    public void testCustomMonthDaySer() throws Exception
    {
        MonthDay monthDay = new MonthDay(7, 23);
        String json = MAPPER.writeValueAsString(new FormattedMonthDay(monthDay));
        assertEquals(a2q("{'value':'07:23'}"), json);
    }

    public void testYearMonthSer() throws Exception
    {
        YearMonth yearMonth = new YearMonth(2013, 8);
        String json = MAPPER.writeValueAsString(yearMonth);
        assertEquals(q("2013-08"), json);
    }

    public void testCustomYearMonthSer() throws Exception
    {
        YearMonth yearMonth = new YearMonth(2013, 8);
        String json = MAPPER.writeValueAsString(new FormattedYearMonth(yearMonth));
        assertEquals(a2q("{'value':'2013/08'}"), json);
    }

    @Test
    public void testHoursSer() throws Exception
    {
        Hours hours = Hours.hours(1);
        String json = MAPPER.writeValueAsString(hours);
        assertEquals("1", json);
    }

    @Test
    public void testMinutesSer() throws Exception
    {
        Minutes minutes = Minutes.minutes(2);
        String json = MAPPER.writeValueAsString(minutes);
        assertEquals("2", json);
    }

    @Test
    public void testSecondsSer() throws Exception
    {
        Seconds seconds = Seconds.seconds(3);
        String json = MAPPER.writeValueAsString(seconds);
        assertEquals("3", json);
    }

    @Test
    public void testMonthsSer() throws Exception
    {
        Months months = Months.months(4);
        String json = MAPPER.writeValueAsString(months);
        assertEquals("4", json);
    }

    @Test
    public void testYearsSer() throws Exception
    {
        Years years = Years.years(5);
        String json = MAPPER.writeValueAsString(years);
        assertEquals("5", json);
    }

    @Test
    public void testWeeksSer() throws Exception
    {
        Weeks weeks = Weeks.weeks(6);
        String json = MAPPER.writeValueAsString(weeks);
        assertEquals("6", json);
    }

}
