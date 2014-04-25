package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.SerializationFeature;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JodaMapperTest
{
    @Test
    public void test_writeDatesAsTimestamps_property()
    {
        JodaMapper objectUnderTest = new JodaMapper();

        objectUnderTest.setWriteDatesAsTimestamps(true);
        assertThat(objectUnderTest.getWriteDatesAsTimestamps(), is(true));

        objectUnderTest.setWriteDatesAsTimestamps(false);
        assertThat(objectUnderTest.getWriteDatesAsTimestamps(), is(false));
    }

    @Test
    public void setWriteDatesAsTimestamps_sets_the_WRITE_DATES_AS_TIMESTAMPS_configuration()
    {
        JodaMapper objectUnderTest = new JodaMapper();

        objectUnderTest.setWriteDatesAsTimestamps(true);

        assertThat(objectUnderTest.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS), is(true));
    }
}
