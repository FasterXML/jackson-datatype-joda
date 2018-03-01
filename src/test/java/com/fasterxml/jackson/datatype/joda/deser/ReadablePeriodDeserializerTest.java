package com.fasterxml.jackson.datatype.joda.deser;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

public class ReadablePeriodDeserializerTest extends JodaTestBase
{

	public void testDeserializeSeconds() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"seconds\"},\"seconds\":12,\"periodType\":{\"name\":\"Seconds\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Seconds.seconds( 12 ), readablePeriod );
	}

	public void testDeserializeMinutes() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"minutes\"},\"minutes\":1,\"periodType\":{\"name\":\"Minutes\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Minutes.minutes( 1 ), readablePeriod );
	}

	public void testDeserializeHours() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"hours\"},\"hours\":2,\"periodType\":{\"name\":\"Hours\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Hours.hours( 2 ), readablePeriod );
	}

	public void testDeserializeDays() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"days\"},\"days\":2,\"periodType\":{\"name\":\"Days\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Days.days( 2 ), readablePeriod );
	}
	
	public void testDeserializeWeeks() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"weeks\"},\"weeks\":2,\"periodType\":{\"name\":\"Weeks\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Weeks.weeks( 2 ), readablePeriod );
	}
	
	public void testDeserializeMonths() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"months\"},\"months\":2,\"periodType\":{\"name\":\"Months\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Months.months( 2 ), readablePeriod );
	}

	public void testDeserializeYears() throws Exception
	{
		ObjectMapper objectMapper = mapperWithModule();
		ReadablePeriod readablePeriod = objectMapper.readValue( "{\"fieldType\":{\"name\":\"years\"},\"years\":2,\"periodType\":{\"name\":\"Years\"}}", ReadablePeriod.class );
		assertNotNull( readablePeriod );
		assertEquals( Years.years( 2 ), readablePeriod );
	}
}