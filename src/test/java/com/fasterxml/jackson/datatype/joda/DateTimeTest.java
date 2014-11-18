package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DateTimeTest extends JodaTestBase
{
    private static class DateAsText {
        @JsonFormat(shape=JsonFormat.Shape.STRING)
        public DateTime date;

        public DateAsText(DateTime d) {
            date = d;
        }
    }

    private static class CustomDate {
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="SS")
        public DateTime date;

        public CustomDate(DateTime d) {
            date = d;
        }
    }
    
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = jodaMapper();

    private final static ObjectMapper STRING_MAPPER = jodaMapper();
    static {
        STRING_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final DateTime DATE_JAN_1_1970_UTC = new DateTime(0L, DateTimeZone.UTC);
    
    /**
     * First: let's ensure that serialization does not fail
     * with an error (see [JACKSON-157]).
     */
    public void testSerializationDefaultAsTimestamp() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(DATE_JAN_1_1970_UTC));
    }

    public void testSerializationFeatureNoTimestamp() throws IOException
    {
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), m.writeValueAsString(DATE_JAN_1_1970_UTC));
    }

    public void testAnnotationAsText() throws IOException
    {
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // with annotations, doesn't matter if mapper configured to use timestamps
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000Z'}"),
                m.writeValueAsString(new DateAsText(DATE_JAN_1_1970_UTC)));
    }

    public void testCustomPatternStyle() throws IOException
    {
        // or, using annotations
        assertEquals(aposToQuotes("{'date':'1/1/70 12:00 AM'}"),
                STRING_MAPPER.writeValueAsString(new CustomDate(DATE_JAN_1_1970_UTC)));
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
        m.addMixIn(DateTime.class, ObjectConfiguration.class);
        assertEquals("[\"org.joda.time.DateTime\",\"1970-01-01T00:00:00.000Z\"]",
                m.writeValueAsString(dt));
    }
}
