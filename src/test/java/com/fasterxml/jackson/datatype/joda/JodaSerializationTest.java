package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JodaSerializationTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Tests for DateTime (and closely related)
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();
    
    /**
     * First: let's ensure that serialization does not fail
     * with an error (see [JACKSON-157]).
     */
    public void testSerialization() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        DateTime dt = new DateTime(0L, DateTimeZone.UTC);
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(dt));

        // but if re-configured, as regular ISO-8601 string
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), m.writeValueAsString(dt));
    }
    
    public void testSerializationWithTypeInfo() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        DateTime dt = new DateTime(0L, DateTimeZone.UTC);
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(dt));

        // but if re-configured, as regular ISO-8601 string
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m.addMixInAnnotations(DateTime.class, ObjectConfiguration.class);
        assertEquals("[\"org.joda.time.DateTime\",\"1970-01-01T00:00:00.000Z\"]", m.writeValueAsString(dt));
    }
    
    /*
    /**********************************************************
    /* Tests for DateMidnight type
    /**********************************************************
     */
    
    public void testDateMidnightSer() throws IOException
    {
        DateMidnight date = new DateMidnight(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", MAPPER.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = jodaMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals(quote("2001-05-25"), mapper.writeValueAsString(date));
    }
    
    public void testDateMidnightSerWithTypeInfo() throws IOException
    {
        DateMidnight date = new DateMidnight(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", MAPPER.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = jodaMapper();
        mapper.addMixInAnnotations(DateMidnight.class, ObjectConfiguration.class);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals("[\"org.joda.time.DateMidnight\",\"2001-05-25\"]", mapper.writeValueAsString(date));
    }
    
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
        ObjectMapper mapper = jodaMapper();
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals(quote("2001-05-25"), mapper.writeValueAsString(date));
    }
    
    public void testLocalDateSerWithTypeInfo() throws IOException
    {
        LocalDate date = new LocalDate(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", MAPPER.writeValueAsString(date));

        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        ObjectMapper mapper = jodaMapper();
        mapper.addMixInAnnotations(LocalDate.class, ObjectConfiguration.class);
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
        mapper.addMixInAnnotations(LocalTime.class, ObjectConfiguration.class);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);        
        assertEquals("[\"org.joda.time.LocalTime\",\"13:20:54.000\"]", mapper.writeValueAsString(date));
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
        mapper.addMixInAnnotations(LocalDateTime.class, ObjectConfiguration.class);
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
        String json = MAPPER.writeValueAsString(in);
        assertEquals(quote("PT1H2M3.004S"), json);
    }
    
    public void testPeriodSerWithTypeInfo() throws IOException
    {
        Period in = new Period(1, 2, 3, 4);
        ObjectMapper mapper = jodaMapper();
        mapper.addMixInAnnotations(Period.class, ObjectConfiguration.class);
        String json = mapper.writeValueAsString(in);
        assertEquals("[\"org.joda.time.Period\",\"PT1H2M3.004S\"]", json);
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
    }
    
    public void testDurationSerWithTypeInfo() throws IOException
    {
        Duration d = new Duration(3123422);
        ObjectMapper mapper = jodaMapper();
        mapper.addMixInAnnotations(Duration.class, ObjectConfiguration.class);
        String json = mapper.writeValueAsString(d);
        assertEquals("[\"org.joda.time.Duration\",3123422]", json);
    }
    
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    public void testInstantSer() throws IOException {
        Instant instant = new Instant(0L);

        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(instant));

        // but if re-configured, as regular ISO-8601 string
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), m.writeValueAsString(instant));
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
