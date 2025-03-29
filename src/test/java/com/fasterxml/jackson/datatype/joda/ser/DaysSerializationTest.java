package com.fasterxml.jackson.datatype.joda.ser;

import java.util.*;

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
        Map<String, Object> map = Collections.singletonMap("days", Days.days(7));

        assertEquals("{\"days\":7}", MAPPER.writeValueAsString(map));
    }
}
