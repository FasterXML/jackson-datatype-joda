package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class DurationDeserializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();

    public void testDurationDeserFromInt() throws IOException
    {
        Duration d = MAPPER.readValue("1234", Duration.class);
        assertEquals(1234, d.getMillis());
    }

    public void testDurationDeserFromString() throws IOException
    {
        Duration d = MAPPER.readValue(quote("PT1.234S"), Duration.class);
        assertEquals(1234, d.getMillis());
    }

    public void testDurationRoundtrip() throws IOException
    {
        Duration d = new Duration(5513);
        assertEquals(d, MAPPER.readValue(MAPPER.writeValueAsString(d), Duration.class));
    }

    public void testDurationFailsDeserializingUnexpectedType() throws IOException
    {
        try {
            MAPPER.readValue("{\"foo\":1234}", Duration.class);
            fail();
        } catch (JsonMappingException e) {
            // there's location info involving a string object id on the second line, so just use the first line
            assertEquals("expected JSON Number or String", e.getMessage().split("\n")[0]);
        }
    }

    public void testDurationDeserFromIntWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Duration.class, ObjectConfiguration.class);

        Duration d = mapper.readValue("[\"org.joda.time.Duration\",1234]", Duration.class);
        assertEquals(1234, d.getMillis());
    }

    /*
    /**********************************************************
    /* Test methods, key deserialization
    /**********************************************************
     */

    public void testDurationKeyDeserialize() throws IOException
    {
        final String json = "{" + quote("PT60s") + ":0}";
        final Map<Duration, Long> map = MAPPER.readValue(json,
                new TypeReference<Map<Duration, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(Duration.standardMinutes(1L)));
    }

    /*
    /**********************************************************
    /* Test methods, alternate representations
    /**********************************************************
     */

    // [datatype-joda#90]: Possibly support wider set of representations

    public void testDurationAltFromString() throws IOException
    {
        Duration d = MAPPER.readValue(quote("PT1H"), Duration.class);
        assertEquals(60, d.getStandardMinutes());

        d = MAPPER.readValue(quote("PT4H30M"), Duration.class);
        assertEquals(4 * 60 + 30, d.getStandardMinutes());
    }

    public void testDurationAltKeyDeserialize() throws IOException
    {
        final String json = "{" + quote("PT4H30M") + ":0}";
        final Map<Duration, Long> map = MAPPER.readValue(json,
                new TypeReference<Map<Duration, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(Duration.standardMinutes(4 * 60 + 30)));
    }
}
