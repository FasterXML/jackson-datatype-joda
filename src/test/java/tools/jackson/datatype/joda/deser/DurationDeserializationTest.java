package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.Map;

import org.joda.time.Duration;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.core.type.TypeReference;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class DurationDeserializationTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    private static interface ObjectConfiguration {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();
    private final ObjectReader READER = MAPPER.readerFor(Duration.class);

    @Test
    public void testDurationDeserFromInt() throws IOException
    {
        Duration d = READER.readValue("1234");
        assertEquals(1234, d.getMillis());
    }

    @Test
    public void testDurationDeserFromString() throws IOException
    {
        Duration d = READER.readValue(quote("PT1.234S"));
        assertEquals(1234, d.getMillis());
    }

    @Test
    public void testDurationRoundtrip() throws IOException
    {
        Duration d = new Duration(5513);
        assertEquals(d, READER.readValue(MAPPER.writeValueAsString(d)));
    }

    @Test
    public void testDurationFailsDeserializingUnexpectedType() throws IOException
    {
        try {
            READER.readValue("{\"foo\":1234}");
            fail();
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type `org.joda.time.Duration`");
            verifyException(e, "from Object value");
        }
    }

    @Test
    public void testDurationDeserFromIntWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(Duration.class, ObjectConfiguration.class)
                .build();
        Duration d = mapper.readValue("[\"org.joda.time.Duration\",1234]", Duration.class);
        assertEquals(1234, d.getMillis());
    }

    /*
    /**********************************************************
    /* Test methods, key deserialization
    /**********************************************************
     */

    @Test
    public void testDurationKeyDeserialize() throws IOException
    {
        final String json = "{" + quote("PT60s") + ":0}";
        final Map<Duration,?> map = MAPPER.readValue(json,
                new TypeReference<Map<Duration, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(Duration.standardMinutes(1L)));
    }

    /*
    /**********************************************************
    /* Test methods, alternate representations
    /**********************************************************
     */

    // [datatype-joda#90]: Possibly support wider set of representations

    @Test
    public void testDurationAltFromString() throws IOException
    {
        Duration d = MAPPER.readValue(quote("PT1H"), Duration.class);
        assertEquals(60, d.getStandardMinutes());

        d = MAPPER.readValue(quote("PT4H30M"), Duration.class);
        assertEquals(4 * 60 + 30, d.getStandardMinutes());
    }

    @Test
    public void testDurationAltKeyDeserialize() throws IOException
    {
        final String json = "{" + quote("PT4H30M") + ":0}";
        final Map<Duration,?> map = MAPPER.readValue(json,
                new TypeReference<Map<Duration, String>>() { });
        assertNotNull(map);
        assertTrue(map.containsKey(Duration.standardMinutes(4 * 60 + 30)));
    }

    /*
    /**********************************************************
    /* Coercion tests
    /**********************************************************
     */

    // @since 2.12
    @Test
    public void testReadFromEmptyString() throws Exception
    {
        // By default, fine to deser from empty or blank
        assertNull(READER.readValue(quote("")));
        assertNull(READER.readValue(quote("    ")));

        final ObjectMapper m = mapperWithFailFromEmptyString();
        try {
            m.readerFor(Duration.class)
                .readValue(quote(""));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot coerce empty String");
            verifyException(e, Duration.class.getName());
        }
    }
}
