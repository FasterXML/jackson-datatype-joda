package com.fasterxml.jackson.datatype.joda.deser;

import org.junit.jupiter.api.Test;

import org.joda.time.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class ReadablePeriodDeserializerTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = jodaMapper();

    @Test
    public void testDeserializeSeconds() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"seconds\"},\"seconds\":12,\"periodType\":{\"name\":\"Seconds\"}}",
                ReadablePeriod.class );
        assertEquals( Seconds.seconds( 12 ), readablePeriod );
    }

    @Test
    public void testDeserializeMinutes() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"minutes\"},\"minutes\":1,\"periodType\":{\"name\":\"Minutes\"}}",
                ReadablePeriod.class );
        assertEquals( Minutes.minutes( 1 ), readablePeriod );
    }

    @Test
    public void testDeserializeHours() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"hours\"},\"hours\":2,\"periodType\":{\"name\":\"Hours\"}}",
                ReadablePeriod.class );
        assertEquals( Hours.hours( 2 ), readablePeriod );
    }

    @Test
    public void testDeserializeDays() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"days\"},\"days\":2,\"periodType\":{\"name\":\"Days\"}}",
                ReadablePeriod.class );
        assertEquals( Days.days( 2 ), readablePeriod );
    }
	
    @Test
    public void testDeserializeWeeks() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"weeks\"},\"weeks\":2,\"periodType\":{\"name\":\"Weeks\"}}",
                ReadablePeriod.class );
        assertEquals( Weeks.weeks( 2 ), readablePeriod );
    }
	
    @Test
    public void testDeserializeMonths() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"months\"},\"months\":2,\"periodType\":{\"name\":\"Months\"}}",
                ReadablePeriod.class );
        assertEquals( Months.months( 2 ), readablePeriod );
    }

    @Test
    public void testDeserializeYears() throws Exception
    {
        ReadablePeriod readablePeriod = MAPPER.readValue(
                "{\"fieldType\":{\"name\":\"years\"},\"years\":2,\"periodType\":{\"name\":\"Years\"}}",
                ReadablePeriod.class );
        assertEquals( Years.years( 2 ), readablePeriod );
    }
}
