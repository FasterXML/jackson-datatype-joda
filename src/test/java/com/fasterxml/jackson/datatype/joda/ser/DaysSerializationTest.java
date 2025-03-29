package com.fasterxml.jackson.datatype.joda.ser;

import java.util.HashMap;
import java.util.Map;

import org.joda.time.Days;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DaysSerializationTest extends JodaTestBase
{
    final ObjectMapper MAPPER = mapperWithModuleBuilder().build();

    @Test
    public void testPeriodSerialization()
        throws Exception
    {
        Days days = Days.days(7);
        Map<String, Object> map = new HashMap<>();
        map.put("days", days);

        String json = MAPPER.writeValueAsString(map);

        assertEquals("{\"days\":7}", json);
    }

}
