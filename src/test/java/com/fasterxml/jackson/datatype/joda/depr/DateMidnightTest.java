package com.fasterxml.jackson.datatype.joda.depr;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

@SuppressWarnings("deprecation") // because DateMidnight deprecated by Joda
public class DateMidnightTest extends JodaTestBase
{
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
    
    static class FormattedDateMidnight {
        @JsonFormat(timezone="EST")
        public DateMidnight dateMidnight;
    }

    static class FormattedDateAsTimestamp {
        @JsonFormat(shape=JsonFormat.Shape.NUMBER)
        public DateMidnight value;

        protected FormattedDateAsTimestamp() { }
        public FormattedDateAsTimestamp(DateMidnight d) {
            value = d;
        }
    }

    /*
    /**********************************************************
    /* Test methods, deserialization
    /**********************************************************
     */

    public void testDateMidnightDeserWithTimeZone() throws IOException
    {
        ObjectMapper mapper = mapperWithModule(TimeZone.getTimeZone("Europe/Paris"));
        // couple of acceptable formats, so:
        DateMidnight date = mapper.readValue("[2001,5,25]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        DateMidnight date2 = mapper.readValue(quote("2005-07-13"), DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), DateMidnight.class));

        mapper = mapperWithModule(TimeZone.getTimeZone("America/Los_Angeles"));
        // couple of acceptable formats, so:
        date = mapper.readValue("[2001,5,25]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        date2 = mapper.readValue(quote("2005-07-13"), DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), DateMidnight.class));
    }
    
    public void testDateMidnightDeser() throws IOException
    {
        ObjectMapper mapper = mapperWithModule();
        
        // couple of acceptable formats, so:
        DateMidnight date = mapper.readValue("[2001,5,25]", DateMidnight.class);
        assertEquals(2001, date.getYear());
        assertEquals(5, date.getMonthOfYear());
        assertEquals(25, date.getDayOfMonth());

        DateMidnight date2 = mapper.readValue(quote("2005-07-13"), DateMidnight.class);
        assertEquals(2005, date2.getYear());
        assertEquals(7, date2.getMonthOfYear());
        assertEquals(13, date2.getDayOfMonth());

        // since 1.6.1, for [JACKSON-360]
        assertNull(mapper.readValue(quote(""), DateMidnight.class));
    }

    public void testDateMidnightDeserWithTypeInfo() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .addMixIn(DateMidnight.class, MixInForTypeId.class)
                .build();
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

    /*
    /**********************************************************
    /* Test methods, serialization
    /**********************************************************
     */

    public void testSerializeAsTimestamp() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();
        assertEquals(aposToQuotes("{'value':0}"),
                mapper.writeValueAsString(new FormattedDateAsTimestamp(
                        new DateMidnight(0, DateTimeZone.UTC))));
    }

    public void testDateMidnightSer() throws IOException
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
            .enable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .enable(SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS)
            .build();
        final ObjectWriter writer = mapper.writer();

        DateMidnight date = new DateMidnight(2001, 5, 25);
        // default format is that of JSON array...
        assertEquals("[2001,5,25]", writer.writeValueAsString(date));
        // but we can force it to be a String as well (note: here we assume this is
        // dynamically changeable)
        assertEquals(quote("2001-05-25"),
                writer.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(date));

        mapper = mapperWithModuleBuilder()
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
                .addMixIn(DateMidnight.class, MixInForTypeId.class)
                .build();
        assertEquals("[\"org.joda.time.DateMidnight\",\"2001-05-25\"]", mapper.writeValueAsString(date));
    }

    /*
    /**********************************************************
    /* Test methods, custom format
    /**********************************************************
     */

    public void testDeserWithCustomFormat() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        String STR = "2015-06-19";
        String ALT = "19.06.2015";

        final DateMidnight inputDate = new DateMidnight(STR);
        AlternateFormat input = new AlternateFormat(inputDate);
        String json = mapper.writeValueAsString(input);

        if (!json.contains(ALT)) {
            fail("Should contain '"+ALT+"', did not: "+json);
        }
        AlternateFormat output = mapper.readValue(json, AlternateFormat.class);
        assertNotNull(output.value);
        assertEquals(inputDate, output.value);
    }
    
    public void testWithTimeZoneOverride() throws Exception
    {
        ObjectMapper mapper = mapperWithModule();

        DateMidnight date = mapper.readValue("[2001,5,25]", DateMidnight.class);
        FormattedDateMidnight input = new FormattedDateMidnight();
        input.dateMidnight = date;
        String json = mapper.writeValueAsString(input);

        FormattedDateMidnight result = mapper.readValue(json, FormattedDateMidnight.class);
        assertNotNull(result);

        // Ensure timezone sticks:
        DateMidnight resultMidnight = result.dateMidnight;
        assertEquals(2001, resultMidnight.getYear());
        assertEquals(5, resultMidnight.getMonthOfYear());
        assertEquals(25, resultMidnight.getDayOfMonth());

        DateTimeZone resultTz = resultMidnight.getZone();
        // Is this stable enough for testing?
        assertEquals("America/New_York", resultTz.getID());
    }

    public void testDeserDateMidnightWhichWasSerializedWithoutJodaModule() throws IOException {
        DateMidnight expectedDateMidnight = new DateMidnight(2020, 1, 1, DateTimeZone.forID("America/New_York"));
        String json = mapper().writeValueAsString(expectedDateMidnight);
        DateMidnight dateMidnight = mapperWithModule().readValue(json, DateMidnight.class);
        assertEquals(expectedDateMidnight, dateMidnight);
    }
}
