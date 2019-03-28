package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class KeyDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests for key deserializers
    /**********************************************************
     */
    
    public void testDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("1970-01-01T00:00:00.000Z") + ":0}";
        final Map<DateTime,?> map = MAPPER.readValue(json, new TypeReference<Map<DateTime, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(DateTime.parse("1970-01-01T00:00:00.000Z")));
    }

    public void testLocalDateKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23") + ":0}";
        final Map<LocalDate,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalDate, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(LocalDate.parse("2014-05-23")));
    }

    public void testLocalTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("00:00:00.000") + ":0}";
        final Map<LocalTime,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalTime.parse("00:00:00.000")));
    }
    public void testLocalDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23T00:00:00.000") + ":0}";
        final Map<LocalDateTime,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalDateTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalDateTime.parse("2014-05-23T00:00:00.000")));
    }
}
