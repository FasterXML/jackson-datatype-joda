package tools.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;

import org.joda.time.YearMonth;
import org.joda.time.chrono.ISOChronology;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ObjectReader;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.MismatchedInputException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class YearMonthDeserTest extends JodaTestBase
{
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = mapperWithModule();
    private final ObjectReader READER = MAPPER.readerFor(YearMonth.class);

    @Test
    public void testDeserYearMonth() throws Exception
    {
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = READER.readValue(quote(yearMonthString));
        assertEquals(new YearMonth(2013, 8), yearMonth);
    }

    @Test
    public void testDeserYearMonthWithTimeZone() throws Exception
    {
        final ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        
        String yearMonthString = new YearMonth(2013, 8).toString();
        YearMonth yearMonth = mapper.readValue(quote(yearMonthString), YearMonth.class);
        assertEquals(new YearMonth(2013, 8), yearMonth);
        assertEquals(ISOChronology.getInstanceUTC(), yearMonth.getChronology());
    }

    @Test
    public void testDeserYearMonthFailsForUnexpectedType() throws IOException
    {
        try {
            READER.readValue("{\"year\":2013}");
            fail("Should not pass");
        } catch (MismatchedInputException e) {
            verifyException(e, "Cannot deserialize value of type ");
            verifyException(e, "from Object value");
        }
    }

    @Test
    public void testDeserYearMonthCustomFormat() throws IOException
    {
        FormattedYearMonth input = MAPPER.readValue(aposToQuotes(
                "{'value':'2013/8'}"),
                FormattedYearMonth.class);
        YearMonth yearMonth = input.value;
        assertEquals(2013, yearMonth.getYear());
        assertEquals(8, yearMonth.getMonthOfYear());
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
            m.readerFor(YearMonth.class)
                .readValue(quote(""));
            fail("Should not pass");
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot coerce empty String");
            verifyException(e, YearMonth.class.getName());
        }
    }
}
