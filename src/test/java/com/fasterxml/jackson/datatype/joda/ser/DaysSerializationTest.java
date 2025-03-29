package com.fasterxml.jackson.datatype.joda.ser;

import java.util.Collections;
import java.util.Map;

import org.joda.time.Days;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

public class DaysSerializationTest extends JodaTestBase
{
    final ObjectMapper MAPPER = mapperWithModuleBuilder().build();

    @Test
    public void testPeriodSerialization()
        throws Exception
    {
        Map<String, Object> map = Collections.singletonMap("days", Days.days(7));

        assertEquals("{\"days\":7}", MAPPER.writeValueAsString(map));
    }
}
