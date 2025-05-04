package com.fasterxml.jackson.datatype.joda.ser;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFormatTimeZoneWithPattern98Test extends JodaTestBase {
    static class Wrapper3Z<T> {
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS ZZZ",
                timezone = "Europe/Budapest"   // +01:00 in winter
        )
        public T value;
        Wrapper3Z(T v) { value = v; }
    }

    static class Wrapper2Z<T> {
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS ZZ",
                timezone = "Europe/Budapest"
        )
        public T value;
        Wrapper2Z(T v) { value = v; }
    }

    static class Wrapper1Z<T> {
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS Z",
                timezone = "Europe/Budapest"   // +01:00 in winter
        )
        public T value;
        Wrapper1Z(T v) { value = v; }
    }

    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void patternShouldNotEraseTimeZone()
        throws Exception
    {
        // DateTime already in Europe/Budapest zone (no shift)
        _testSerialization(
                "{\"value\":\"2018-01-01T12:01:02.003 Europe/Budapest\"}",
                new Wrapper3Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Budapest")))));
        // DateTime in UTC, should shift +1h
        _testSerialization(
                "{\"value\":\"2018-01-01T13:01:02.003 Europe/Budapest\"}",
                new Wrapper3Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")))));
        // LocalDate
        _testSerialization(
                "{\"value\":\"2018-01-01T��:��:��.000 \"}",
                new Wrapper3Z<>(new LocalDate(2018,1,1)));
        // LocalTime
        _testSerialization(
                "{\"value\":\"����-��-��T12:01:02.003 \"}",
                new Wrapper3Z<>(new LocalTime(12,1,2,3)));
        // LocalDateTime
        _testSerialization(
                "{\"value\":\"2018-01-01T12:01:02.003 \"}",
                new Wrapper3Z<>(new LocalDateTime(2018,1,1,12,1,2,3)));
    }

    @Test
    public void patternShouldNotEraseTimeZoneWithZZ()
            throws Exception
    {
        // DateTime already in Europe/Budapest zone (no shift)
        _testSerialization(
                "{\"value\":\"2018-01-01T12:01:02.003 +01:00\"}",
                new Wrapper2Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Budapest")))));
        // DateTime in UTC, should shift +1h
        _testSerialization(
                "{\"value\":\"2018-01-01T13:01:02.003 +01:00\"}",
                new Wrapper2Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")))));
    }

    @Test
    public void patternShouldNotEraseTimeZoneWithZ()
        throws Exception
    {
        // DateTime already in Europe/Budapest zone (no shift)
        _testSerialization(
                "{\"value\":\"2018-01-01T12:01:02.003 +0100\"}",
                new Wrapper1Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Budapest")))));
        // DateTime in UTC, should shift +1h
        _testSerialization(
                "{\"value\":\"2018-01-01T13:01:02.003 +0100\"}",
                new Wrapper1Z<>(new DateTime(2018,1,1,12,1,2,3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")))));
    }

    private void _testSerialization(String expectedJson, Object wrapper) throws Exception {
        String actual = MAPPER.writeValueAsString(wrapper);
        assertEquals(expectedJson, actual);
    }

}