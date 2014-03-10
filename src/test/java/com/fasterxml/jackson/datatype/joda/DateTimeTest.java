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

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }
    
    private final ObjectMapper MAPPER = jodaMapper();
    
    /**
     * First: let's ensure that serialization does not fail
     * with an error (see [JACKSON-157]).
     */
    public void testSerializationDefaultAsTimestamp() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        DateTime dt = new DateTime(0L, DateTimeZone.UTC);
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(dt));
    }

    public void testSerializationFeatureNoTimestamp() throws IOException
    {
        DateTime dt = new DateTime(0L, DateTimeZone.UTC);
        // but if re-configured, as regular ISO-8601 string
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), m.writeValueAsString(dt));

        // or, using annotations
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000Z'}"),
                MAPPER.writeValueAsString(new DateAsText(dt)));
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
}
