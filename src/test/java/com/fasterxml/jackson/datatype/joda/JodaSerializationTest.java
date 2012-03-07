package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.*;

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
}
