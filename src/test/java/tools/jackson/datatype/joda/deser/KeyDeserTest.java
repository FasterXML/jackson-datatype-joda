package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

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
    
    @Test
    public void testDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("1970-01-01T00:00:00.000Z") + ":0}";
        final Map<DateTime,?> map = MAPPER.readValue(json, new TypeReference<Map<DateTime, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(DateTime.parse("1970-01-01T00:00:00.000Z")));
    }

    // Key without offset must be resolved using context time zone (and not
    // JVM default one), with or without adjustment
    @Test
    public void testDateTimeKeyDeserializeWithoutOffset() throws IOException {
        final String json = "{" + quote("2020-01-01T00:00:00") + ":0}";
        for (String zoneId : new String[] { "Asia/Kolkata", "America/Los_Angeles" }) {
            final DateTimeZone tz = DateTimeZone.forID(zoneId);
            final DateTime exp = new DateTime(2020, 1, 1, 0, 0, 0, tz);
            for (boolean adjust : new boolean[] { true, false }) {
                final ObjectMapper mapper = jodaMapperBuilder(tz.toTimeZone())
                        .configure(DateTimeFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, adjust)
                        .build();
                final Map<DateTime,?> map = mapper.readValue(json,
                        new TypeReference<Map<DateTime, String>>() { });
                assertEquals(1, map.size());
                final DateTime key = map.keySet().iterator().next();
                assertEquals(exp.getMillis(), key.getMillis(), "adjust: "+adjust);
                assertEquals(tz, key.getZone(), "adjust: "+adjust);
            }
        }
    }

    @Test
    public void testLocalDateKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23") + ":0}";
        final Map<LocalDate,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalDate, String>>() { });

        assertNotNull(map);
        assertTrue(map.containsKey(LocalDate.parse("2014-05-23")));
    }

    // Keys with year outside of supported range must be reported: Joda would
    // silently wrap around to the other end of the range
    @Test
    public void testLocalDateKeysFailForOutOfRangeYear() throws IOException {
        _verifyOutOfRangeYearKey("-292275055-01-01", new TypeReference<Map<LocalDate, String>>() { });
        _verifyOutOfRangeYearKey("292278994-12-31", new TypeReference<Map<LocalDate, String>>() { });
        _verifyOutOfRangeYearKey("-292275055-01-01T00:00",
                new TypeReference<Map<LocalDateTime, String>>() { });
        _verifyOutOfRangeYearKey("292278994-12-31T00:00",
                new TypeReference<Map<LocalDateTime, String>>() { });
    }

    private void _verifyOutOfRangeYearKey(String key, TypeReference<?> type) {
        final String json = "{" + quote(key) + ":0}";
        try {
            Object result = MAPPER.readValue(json, type);
            fail("Should not pass, got: "+result.getClass().getName());
        } catch (InvalidFormatException e) {
            verifyException(e, "for year must be in the range");
            assertEquals(key, e.getValue());
        }
    }

    @Test
    public void testLocalTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("00:00:00.000") + ":0}";
        final Map<LocalTime,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalTime.parse("00:00:00.000")));
    }
    @Test
    public void testLocalDateTimeKeyDeserialize() throws IOException {

        final String json = "{" + quote("2014-05-23T00:00:00.000") + ":0}";
        final Map<LocalDateTime,?> map = MAPPER.readValue(json, new TypeReference<Map<LocalDateTime, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(LocalDateTime.parse("2014-05-23T00:00:00.000")));
    }
}
