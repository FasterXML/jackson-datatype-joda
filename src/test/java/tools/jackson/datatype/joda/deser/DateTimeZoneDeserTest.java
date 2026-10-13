package tools.jackson.datatype.joda.deser;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tools.jackson.core.JsonParser;
import tools.jackson.core.StreamReadCapability;
import tools.jackson.core.util.JacksonFeatureSet;
import tools.jackson.core.util.JsonParserDelegate;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.joda.JodaTestBase;
import tools.jackson.datatype.joda.deser.DateTimeDeserTest.DateTimeZoneWrapper;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeZoneDeserTest extends JodaTestBase
{
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface ObjectConfiguration {
    }

    // Simulates a textual format (like XML, CSV) where scalars are untyped
    static class UntypedScalarsParser extends JsonParserDelegate {
        public UntypedScalarsParser(JsonParser p) { super(p); }

        @Override
        public JacksonFeatureSet<StreamReadCapability> streamReadCapabilities() {
            return super.streamReadCapabilities().with(StreamReadCapability.UNTYPED_SCALARS);
        }
    }

    private final ObjectMapper MAPPER = mapperWithModule();
    private final ObjectReader READER = MAPPER.readerFor(DateTimeZone.class);

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    // [datatype-joda#82]
    @Test
    public void testSimpleDateTimeZone() throws Exception
    {
        TimeZone timeZone = TimeZone.getTimeZone("GMT-6");
        DateTimeZone input = DateTimeZone.forTimeZone(timeZone);
        String json = MAPPER.writeValueAsString(input);
        assertEquals(quote("-06:00"), json);

        DateTimeZone result = READER.readValue(json);
        assertEquals(result, input);

        // But then verify polymorphic handling
        DateTimeZoneWrapper input2 = new DateTimeZoneWrapper(input);
        json = MAPPER.writeValueAsString(input2);
        if (json.indexOf("DateTimeZone") < 0) {
            fail("Should contain type information, json = "+json);
        }

        DateTimeZoneWrapper result2 = MAPPER.readValue(json, DateTimeZoneWrapper.class);    
        assertNotNull(result2.tz);
        assertEquals(input, result2.tz);
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
            m.readerFor(DateTimeZone.class)
                .readValue(quote(""));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot coerce empty String");
            verifyException(e, DateTimeZone.class.getName());
        }
    }

    // [datatype-joda#197]: out-of-`int`-range timestamp String must be reported,
    //   not escape as a raw `NumberFormatException` from `parseInt`
    @Test
    public void testDeserFailsForOutOfRangeTimestampString() throws Exception
    {
        for (String value : new String[] { "2147483648", "9999999999", "-2147483649" }) {
            try (JsonParser p = new UntypedScalarsParser(MAPPER.createParser(quote(value)))) {
                DateTimeZone result = MAPPER.readValue(p, DateTimeZone.class);
                fail("Should not pass for '"+value+"', got: "+result);
            } catch (MismatchedInputException e) {
                verifyException(e, "out of range");
            }
        }
    }
}
