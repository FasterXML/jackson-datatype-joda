package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;

import org.joda.time.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

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

    private final ObjectMapper MAPPER = jodaMapper();
    {
        MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

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
        assertEquals(quote("2001-05-25"),
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
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalDate.class, ObjectConfiguration.class);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
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
        ObjectMapper mapper = jodaMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals(quote("13:20:54.000"), mapper.writeValueAsString(date));
    }
    
    public void testLocalTimeSerWithTypeInfo() throws IOException
    {
        LocalTime date = new LocalTime(13,20,54);
        // default format is that of JSON array...
        assertEquals("[13,20,54,0]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalTime.class, ObjectConfiguration.class);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
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
        ObjectMapper mapper = jodaMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals(quote("2001-05-25T10:15:30.037"), mapper.writeValueAsString(date));
    }
    
    public void testLocalDateTimeSerWithTypeInfo() throws IOException
    {
        LocalDateTime date = new LocalDateTime(2001, 5, 25,
                10, 15, 30, 37);
        // default format is that of JSON array...
        assertEquals("[2001,5,25,10,15,30,37]", MAPPER.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(LocalDateTime.class, ObjectConfiguration.class);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals("[\"org.joda.time.LocalDateTime\",\"2001-05-25T10:15:30.037\"]", mapper.writeValueAsString(date));
    }

    /*
    /**********************************************************
    /* Tests for Period type
    /**********************************************************
     */

    public void testPeriodSer() throws IOException
    {
        Period in = new Period(1, 2, 3, 4);
        assertEquals(quote("PT1H2M3.004S"), MAPPER.writeValueAsString(in));
    }
    
    public void testPeriodSerWithTypeInfo() throws IOException
    {
        Period in = new Period(1, 2, 3, 4);
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Period.class, ObjectConfiguration.class);
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

        assertEquals(quote("PT3123.422S"), MAPPER.writer()
                .without(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
                .writeValueAsString(d));
    }
    
    public void testDurationSerWithTypeInfo() throws IOException
    {
        Duration d = new Duration(3123422);
        ObjectMapper mapper = jodaMapper();
        mapper.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
        mapper.addMixIn(Duration.class, ObjectConfiguration.class);
        String json = mapper.writeValueAsString(d);
        assertEquals("[\"org.joda.time.Duration\",3123422]", json);
    }

    public void testMonthDaySer() throws Exception
    {
        MonthDay monthDay = new MonthDay(7, 23);
        ObjectMapper mapper = jodaMapper();
        String json = mapper.writeValueAsString(monthDay);
        assertEquals(quote("--07-23"), json);
    }

    public void testYearMonthSer() throws Exception
    {
        YearMonth yearMonth = new YearMonth(2013, 8);
        ObjectMapper mapper = jodaMapper();
        String json = mapper.writeValueAsString(yearMonth);
        assertEquals(quote("2013-08"), json);
    }

}
