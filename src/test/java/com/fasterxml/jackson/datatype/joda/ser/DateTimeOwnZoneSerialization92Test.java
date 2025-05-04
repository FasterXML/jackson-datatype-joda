package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Reproduces jackson-datatype-joda#92:
 * DateTime loses its own zone during serialization (normalised to UTC),
 * so the JSON differs from Java 8 ZonedDateTime output.
 *
 * Expected (zone kept):  "2017‑01‑01T01:01:01.000+08:00"
 * Actual (current bug):  "2016‑12‑31T17:01:01.000Z"
 */
public class DateTimeOwnZoneSerialization92Test
    extends JodaTestBase
{
    // Wrapper matches style used in other failing tests
    static class Wrapper {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS ZZZ")   // default ISO pattern
        public DateTime value;
        Wrapper(DateTime v) { value = v; }
    }

    private final ObjectMapper MAPPER = mapperWithModuleBuilder()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // string mode
            .build();

    @Test
    public void dateTimeShouldRetainItsOwnZone() throws Exception {
        DateTime europe = new DateTime(
                2017, 1, 1, 12, 1, 1, 0,
                DateTimeZone.forID("Europe/Budapest")      // UTC+8
        );

        String actual = MAPPER.writeValueAsString(new Wrapper(europe));

        // This assertion FAILS today, demonstrating the bug
        assertEquals("{\"value\":\"2017-01-01T11:01:01.000 Europe/Budapest\"}", actual);
    }
}