package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.joda.time.Period;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class PeriodDeserializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = jodaMapper();

    /*
    /**********************************************************
    /* Tests for Period type values
    /**********************************************************
     */

    @Test
    public void testPeriodDeser() throws IOException
    {
        Period out = MAPPER.readValue(quote("PT1H2M3.004S"), Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        assertEquals(4, out.getMillis());

        // also, should work as number:
        String json = String.valueOf(1000 * out.toStandardSeconds().getSeconds());
        out = MAPPER.readValue(json, Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        // but millis are actually truncated...
        assertEquals(0, out.getMillis());
    }

    @Test
    public void testPeriodDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(Period.class, ObjectConfiguration.class);

        Period out = mapper.readValue("[\"org.joda.time.Period\",\"PT1H2M3.004S\"]", Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        assertEquals(4, out.getMillis());

        // also, should work as number:
        String json = "[\"org.joda.time.Period\"," + String.valueOf(1000 * out.toStandardSeconds().getSeconds()) + "]";
        out = mapper.readValue(json, Period.class);
        assertEquals(1, out.getHours());
        assertEquals(2, out.getMinutes());
        assertEquals(3, out.getSeconds());
        // but millis are actually truncated...
        assertEquals(0, out.getMillis());
    }

    /*
    /**********************************************************
    /* Tests for Period type keys
    /**********************************************************
     */

    @Test
    public void testPeriodKeyDeserialize() throws IOException {

        final String json = "{" + quote("PT1H2M3.004S") + ":0}";
        final Map<Period,Long> map = MAPPER.readValue(json, new TypeReference<Map<Period, Long>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(new Period(1, 2, 3, 4)));
    }
}
