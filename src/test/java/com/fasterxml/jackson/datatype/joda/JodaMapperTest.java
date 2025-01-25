package com.fasterxml.jackson.datatype.joda;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.SerializationFeature;

import static org.junit.jupiter.api.Assertions.*;

public class JodaMapperTest
{

    @Test
    public void test_writeDatesAsTimestamps_property()
    {
        JodaMapper objectUnderTest = new JodaMapper();

        objectUnderTest.setWriteDatesAsTimestamps(true);
        assertTrue(objectUnderTest.getWriteDatesAsTimestamps());

        objectUnderTest.setWriteDatesAsTimestamps(false);
        assertFalse(objectUnderTest.getWriteDatesAsTimestamps());
    }

    @Test
    public void setWriteDatesAsTimestamps_sets_the_WRITE_DATES_AS_TIMESTAMPS_configuration()
    {
        JodaMapper objectUnderTest = new JodaMapper();

        objectUnderTest.setWriteDatesAsTimestamps(true);

        assertTrue(objectUnderTest.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));
    }
}
