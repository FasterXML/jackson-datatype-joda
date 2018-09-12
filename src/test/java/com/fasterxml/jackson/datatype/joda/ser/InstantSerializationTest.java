package com.fasterxml.jackson.datatype.joda.ser;

import java.text.SimpleDateFormat;

import org.joda.time.DateTime;
import org.joda.time.Instant;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class InstantSerializationTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = jodaMapper();
    {
        MAPPER.enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        MAPPER.enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    public void testInstantSer() throws Exception {
        Instant instant = new Instant(0L);

        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(instant));

        // but if re-configured, as regular ISO-8601 string
        assertEquals(quote("1970-01-01T00:00:00.000Z"), MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(instant));
    }

    public void testCustomFormatInstantSer() throws Exception
    {
        final String json = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(new FormattedInstant(new Instant(0L)));
        assertEquals(aposToQuotes(
                "{'value':'01/01/1970 00_00_00_000'}"), json);
    }

    // [datatype-joda#60]
    public void testInstantConversion() throws Exception
    {
        final ObjectMapper mapper = jodaMapper();

        // Configure Date Formatting
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));

        // Create an instant and serialize and additionally serialize the instant as DateTime to demonstrate the difference
        org.joda.time.Instant now = new DateTime(1431498572205L).toInstant();
        
        String instantString = mapper.writeValueAsString(now);

        assertEquals("\"2015-05-13T06:29:32.205Z\"", instantString);
    }    
}
