package com.fasterxml.jackson.datatype.joda.ser;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonFormatTimeZoneWithPattern98Test extends JodaTestBase {
    static class Wrapper<T> {
        @JsonFormat(
                shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS ZZZ",
                timezone = "Europe/Budapest"   // +01:00 in winter
        )
        public T value;

        Wrapper(T v) {
            value = v;
        }

    }

    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .build();

    @Test
    public void patternShouldNotEraseTimeZone()
            throws Exception {
        // Explicity set with Timezone
        _testSerializationOutput(
                /* expectedHour */ "12",
                new Wrapper(new DateTime(2018, 1, 1, 12, 1, 2, 3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("Europe/Budapest")))));
        // Using @JsonFormat
        _testSerializationOutput(
                /* expectedHour */ "13",
                new Wrapper(new DateTime(2018, 1, 1, 12, 1, 2, 3,
                        DateTimeZone.forTimeZone(TimeZone.getTimeZone("UTC")))));
    }

    private <T> void _testSerializationOutput(String expectedHour, Wrapper<T> wrapper)
            throws Exception {
        String actual = MAPPER.writeValueAsString(wrapper);
        String exp = "{\"value\":\"2018-01-01T" + expectedHour + ":01:02.003 Europe/Budapest\"}";
        assertEquals(exp, actual);
    }

}