package com.fasterxml.jackson.datatype.joda;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.*;

// for [datatype-joda#44]
public class TimeZoneTest extends JodaTestBase
{
    // November 3, 2013 at 1:00a is the fall back DST transition that year in much of the US.
    private static final int FALL_BACK_YEAR = 2013;

    private static final int FALL_BACK_MONTH = 11;

    private static final int FALL_BACK_DAY = 3;

    // The first one for America/Los_Angeles happens at 8:00 UTC.
    private static final int FIRST_FALL_BACK_HOUR = 8;

    // And the second one happens at 9:00 UTC
    private static final int SECOND_FALL_BACK_HOUR = 9;

    private static final DateTimeZone AMERICA_LOS_ANGELES = DateTimeZone.forID("America/Los_Angeles");

    private final DateTime DATE_JAN_1_1970_UTC = new DateTime(0L, DateTimeZone.UTC);

    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY, property = "@class")
    private static interface TypeInfoMixIn {
    }
    
    /*
    /**********************************************************
    /* Test methods
    /**********************************************************
     */

    private final ObjectMapper MAPPER = jodaMapper();

    public void testSimple() throws Exception
    {
        // First, no zone id included
        ObjectWriter w = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        
        assertEquals("0",
                w.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .writeValueAsString(DATE_JAN_1_1970_UTC));
        assertEquals(quote("1970-01-01T00:00:00.000Z"),
                w.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .writeValueAsString(DATE_JAN_1_1970_UTC));

        // then with zone id

        w = w.with(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

        // 12-Jul-2015, tatu: Despite initial plans, support for timezone id with timestamps
        //    was not included in 2.6.0 final.
        /*
        assertEquals(quote("0[UTC]"),
                w.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(DATE_JAN_1_1970_UTC));
                */
        assertEquals(quote("1970-01-01T00:00:00.000Z[UTC]"),
                w.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(DATE_JAN_1_1970_UTC));
    }

    public void testRoundTrip()  throws Exception
    {
        ObjectWriter w = MAPPER.writer()
                .with(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        DateTime input = new DateTime(2014, 8, 24, 5, 17, 45, DateTimeZone.forID("America/Chicago")); // arbitrary
        String json;
        DateTime result;

        // Time zone id only supported as regular text
        json = w.without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        result = MAPPER.readValue(json, DateTime.class);
        assertEquals("Actual timepoints differ", input.getMillis(), result.getMillis());
        assertEquals("TimeZones differ", input, result);

        // Then timestamp: will not currently (2.6) write out timezone id
        json = w.with(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .writeValueAsString(input);
        result = MAPPER.readValue(json, DateTime.class);
        assertEquals("Actual timepoints differ", input.getMillis(), result.getMillis());
        
        // .. meaning we can not test this:
//        assertEquals("TimeZones differ", input, result);
    }

    /**
     * Test that de/serializing an ambiguous time (e.g. a 'fall back' DST transition) works and preserves the proper
     * instants in time and time zones.
     */
    public void testFallBackTransition() throws Exception
    {
        DateTime firstOneAmUtc = new DateTime(FALL_BACK_YEAR, FALL_BACK_MONTH, FALL_BACK_DAY, FIRST_FALL_BACK_HOUR, 0, 0,
                                              DateTimeZone.UTC);
        DateTime secondOneAmUtc = new DateTime(FALL_BACK_YEAR, FALL_BACK_MONTH, FALL_BACK_DAY, SECOND_FALL_BACK_HOUR, 0, 0,
                                               DateTimeZone.UTC);

        DateTime firstOneAm = new DateTime(firstOneAmUtc, AMERICA_LOS_ANGELES);
        DateTime secondOneAm = new DateTime(secondOneAmUtc, AMERICA_LOS_ANGELES);

        ObjectWriter w = MAPPER.writer()
                .without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .with(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);

        String firstOneAmStr = w.writeValueAsString(firstOneAm);
        String secondOneAmStr = w.writeValueAsString(secondOneAm);

        DateTime firstRoundTrip = MAPPER.readValue(firstOneAmStr, DateTime.class);
        DateTime secondRoundTrip = MAPPER.readValue(secondOneAmStr, DateTime.class);

        assertEquals("Actual timepoints differ", firstOneAm.getMillis(), firstRoundTrip.getMillis());
        assertEquals("TimeZones differ", firstOneAm, firstRoundTrip);

        assertEquals("Actual timepoints differ", secondOneAm.getMillis(), secondRoundTrip.getMillis());
        assertEquals("TimeZones differ", secondOneAm, secondRoundTrip);
    }

    public void testSerializationWithTypeInfo() throws Exception
    {
        // but if re-configured to include the time zone
        ObjectMapper m = jodaMapper();
        m.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        m.enable(SerializationFeature.WRITE_DATES_WITH_ZONE_ID);
        
        m.addMixIn(DateTime.class, TypeInfoMixIn.class);
        assertEquals("[\"org.joda.time.DateTime\",\"1970-01-01T00:00:00.000Z[UTC]\"]",
                m.writeValueAsString(DATE_JAN_1_1970_UTC));
    }
}
