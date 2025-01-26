package com.fasterxml.jackson.datatype.joda;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.*;

public class DateTimeTest extends JodaTestBase
{
    static class DateAsText {
        @JsonFormat(shape=JsonFormat.Shape.STRING)
        public DateTime date;

        public DateAsText(DateTime d) {
            date = d;
        }
    }

    static class DateTimeWrapper {
        public DateTime value;

        public DateTimeWrapper(DateTime v) { value = v; }
        protected DateTimeWrapper() { }
    }

    static class CustomDate {
        // note: 'SS' means 'short representation'
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="SS", locale="en")
        public DateTime date;

        public CustomDate(DateTime d) {
            date = d;
        }
    }

    static class EuroDate {
        @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="dd.MM.YYYY' 'HH:mm")
        public DateTime date;

        public EuroDate() { }
        public EuroDate(DateTime d) {
            date = d;
        }
    }

    @JsonPropertyOrder({"jodaDateTime", "javaUtilDate" })
    static class Beanie {
        public final DateTime jodaDateTime;
        public final java.util.Date javaUtilDate;
        @JsonCreator
        public Beanie(@JsonProperty("jodaDateTime") DateTime jodaDateTime,
            @JsonProperty("javaUtilDate") java.util.Date javaUtilDate) {
          this.jodaDateTime = jodaDateTime;
          this.javaUtilDate = javaUtilDate;
        }
    }
    
    static class FormattedDateTime {
        @JsonFormat(timezone="EST")
        public DateTime dateTime;
    }

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface TypeInfoMixIn {
    }

    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */
    
    private final ObjectMapper MAPPER = jodaMapper();

    private final static ObjectMapper STRING_MAPPER = jodaMapper();
    static {
        STRING_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
    }

    private final DateTime DATE_JAN_1_1970_UTC = new DateTime(0L, DateTimeZone.UTC);
    
    /**
     * First: let's ensure that serialization does not fail
     * with an error (see [JACKSON-157]).
     */
    @Test
    public void testSerializationDefaultAsTimestamp() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(DATE_JAN_1_1970_UTC));
    }

    @Test
    public void testSerializationFeatureNoTimestamp() throws IOException
    {
		String json = MAPPER.writer()
				.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
				.writeValueAsString(DATE_JAN_1_1970_UTC);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), json);
    }

    @Test
    public void testAnnotationAsText() throws IOException
    {
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        // with annotations, doesn't matter if mapper configured to use timestamps
        assertEquals(aposToQuotes("{'date':'1970-01-01T00:00:00.000Z'}"),
                m.writeValueAsString(new DateAsText(DATE_JAN_1_1970_UTC)));
    }

    // for [datatype-joda#70]
    @Test
    public void testAsTextNoMilliseconds() throws Exception
    {
    	DateTime value = MAPPER.readValue(quote("2015-07-27T08:11:07-07:00"), DateTime.class);
    	assertNotNull(value);
    }
    
    @Test
    public void testCustomPatternStyle() throws IOException
    {
        // or, using annotations
        // 05-May-2019, tatu: Looks like "short representation" somehow varies between JDK 8 and JDK 11?
        //   JDK 11 adding an extra comma. No idea how, why but... need to work around
        String json = STRING_MAPPER.writeValueAsString(new CustomDate(DATE_JAN_1_1970_UTC));
        if (json.contains(",")) {
            json = json.replace(", ", " ");
        }
        // https://bugs.openjdk.org/browse/JDK-8284840
        // Java 21 uses NBSP/NNBSP prefixed to AM/PM in time format, instead of a normal space
        if (json.contains("\u202f")) {
            json = json.replace("\u202f", " ");
        }

        assertEquals(aposToQuotes("{'date':'1/1/70 12:00 AM'}"), json);
    }

    @Test
    public void testSerializationWithTypeInfo() throws IOException
    {
        // let's use epoch time (Jan 1, 1970, UTC)
        DateTime dt = new DateTime(0L, DateTimeZone.UTC);
        // by default, dates use timestamp, so:
        assertEquals("0", MAPPER.writeValueAsString(dt));

        // but if re-configured, as regular ISO-8601 string
        ObjectMapper m = jodaMapper();
        m.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        m.addMixIn(DateTime.class, TypeInfoMixIn.class);
        assertEquals("[\"org.joda.time.DateTime\",\"1970-01-01T00:00:00.000Z\"]",
                m.writeValueAsString(dt));
    }

    @Test
    public void testIso8601ThroughJoda() throws Exception {
        ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JodaModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        java.text.DateFormat df = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss zzz");
        java.util.Date targetDate = df.parse("2000-01-01 00:00:00 UTC");
        Beanie expectedBean = new Beanie(new DateTime(targetDate.getTime()), targetDate);

        String expectedJSON =
            "{\"jodaDateTime\":\"2000-01-01T00:00:00.000Z\","
                // 19-Mar-2020, tatu: [databind#2167] changed this to...
                + "\"javaUtilDate\":\"2000-01-01T00:00:00.000+00:00\"}";
        String actualJSON = mapper.writeValueAsString(expectedBean);
        assertEquals(actualJSON, expectedJSON);

        Beanie actualBean = mapper.readValue(actualJSON, Beanie.class);
        assertEquals(actualBean.javaUtilDate, expectedBean.javaUtilDate);
        assertEquals(actualBean.jodaDateTime.getMillis(), expectedBean.jodaDateTime.getMillis());

        assertEquals(expectedBean.jodaDateTime.getMillis(), actualBean.jodaDateTime.getMillis());

        // note: timezone/offset may vary, shouldn't check:
//        assertEquals(expectedBean.jodaDateTime, actualBean.jodaDateTime);
      }

    @Test
    public void testCustomFormat() throws Exception
    {
        String STR = "2015-06-19T19:05Z";
        String ALT = "19.06.2015 19:05";

        final DateTime inputDate = new DateTime(STR);
        EuroDate input = new EuroDate(inputDate);
        String json = MAPPER.writeValueAsString(input);

        if (!json.contains(ALT)) {
            fail("Should contain '"+ALT+"', did not: "+json);
        }
        EuroDate output = MAPPER.readValue(json, EuroDate.class);
        assertNotNull(output.date);
        // Timezone may (and most likely will) differ so...
        assertEquals(inputDate.getMillis(), output.date.getMillis());
    }
    
    @Test
    public void testWithTimeZoneOverride() throws Exception
    {
        DateTime date = MAPPER.readValue(quote("2014-01-20T08:59:01.000-0500"),
                DateTime.class);

        FormattedDateTime input = new FormattedDateTime();
        input.dateTime = date;
        String json = MAPPER.writeValueAsString(input);

        FormattedDateTime result = MAPPER.readValue(json, FormattedDateTime.class);
        assertNotNull(result);

        // Ensure timezone sticks:
        DateTime resultMidnight = result.dateTime;
        assertEquals(2014, resultMidnight.getYear());
        assertEquals(1, resultMidnight.getMonthOfYear());
        assertEquals(20, resultMidnight.getDayOfMonth());

        DateTimeZone resultTz = resultMidnight.getZone();
        // Is this stable enough for testing?
        assertEquals("America/New_York", resultTz.getID());
    }

    // since 2.8
    @Test
    public void testConfigOverrides() throws Exception
    {
        ObjectMapper mapper = jodaMapper();
        mapper.configOverride(DateTime.class)
            .setFormat(JsonFormat.Value.forPattern("dd.MM.YYYY' 'HH:mm")
                    .withShape(JsonFormat.Shape.STRING));
        DateTimeWrapper input = new DateTimeWrapper(DATE_JAN_1_1970_UTC);
        final String EXP_DATE = "\"01.01.1970 00:00\"";
        final String json = "{\"value\":"+EXP_DATE+"}";
        assertEquals(json, mapper.writeValueAsString(input));
        assertEquals(EXP_DATE, mapper.writeValueAsString(DATE_JAN_1_1970_UTC));

        // also read back
        DateTimeWrapper result = mapper.readValue(json, DateTimeWrapper.class);
        assertNotNull(result);
        assertEquals(input.value, result.value);
        DateTime resultDate = mapper.readValue(EXP_DATE, DateTime.class);
        assertEquals(DATE_JAN_1_1970_UTC, resultDate);
    }

    // [datatype-joda#113] (NPE)
    @Test
    public void testWithoutLeniency() throws Exception
    {
        ObjectMapper mapper = mapperWithModuleBuilder()
                .defaultLeniency(false)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .build();
        String json = mapper.writeValueAsString(DATE_JAN_1_1970_UTC);
        assertEquals(quote("1970-01-01T00:00:00.000Z"), json);
    }
}
