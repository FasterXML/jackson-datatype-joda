package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.joda.time.Period;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.datatype.joda.JodaTestBase;

public class PeriodDeserializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    private final ObjectMapper MAPPER = mapperWithModule();

    /*
    /**********************************************************
    /* Tests for Period type values
    /**********************************************************
     */

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

    public void testPeriodDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(Period.class, ObjectConfiguration.class)
                .build();

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

    public void testPeriodKeyDeserialize() throws IOException {

        final String json = "{" + quote("PT1H2M3.004S") + ":0}";
        final Map<Period,Long> map = MAPPER.readValue(json, new TypeReference<Map<Period, Long>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(new Period(1, 2, 3, 4)));
    }
}
