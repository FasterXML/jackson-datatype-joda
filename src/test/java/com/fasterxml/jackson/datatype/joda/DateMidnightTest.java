package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.DateMidnight;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class DateMidnightTest extends JodaTestBase
{
    // let's default to String serialization
    private final ObjectMapper MAPPER = jodaMapper()
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface MixInForTypeId {
    }

    static class AlternateFormat {
        @JsonFormat(pattern="dd'.'MM'.'YYYY")
        public DateMidnight value;

        public AlternateFormat() { }
        public AlternateFormat(DateMidnight v) {
            value = v;
        }
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    public void testDateMidnightDeser() throws IOException
    {
        // couple of acceptable formats, so:
        DateMidnight date = MAPPER.readValue("[2001,5,25]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        DateMidnight date2 = MAPPER.readValue(quote("2005-07-13"), DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(MAPPER.readValue(quote(""), DateMidnight.class));
    }

    public void testDateMidnightDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = jodaMapper();
        mapper.addMixIn(DateMidnight.class, MixInForTypeId.class);

        // couple of acceptable formats, so:
        DateMidnight date = mapper.readValue("[\"org.joda.time.DateMidnight\",[2001,5,25]]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        DateMidnight date2 = mapper.readValue("[\"org.joda.time.DateMidnight\",\"2005-07-13\"]", DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());
    }

    public void testCustomFormat() throws Exception
    {
        String STR = "2015-06-19";
        String ALT = "19.06.2015";

        final DateMidnight inputDate = new DateMidnight(STR);
        AlternateFormat input = new AlternateFormat(inputDate);
        String json = MAPPER.writeValueAsString(input);

        if (!json.contains(ALT)) {
            fail("Should contain '"+ALT+"', did not: "+json);
        }
        AlternateFormat output = MAPPER.readValue(json, AlternateFormat.class);
        assertNotNull(output.value);
        assertEquals(inputDate, output.value);
    }
}
