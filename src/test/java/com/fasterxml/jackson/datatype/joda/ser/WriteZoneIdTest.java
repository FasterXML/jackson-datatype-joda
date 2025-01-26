package com.fasterxml.jackson.datatype.joda.ser;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

public class WriteZoneIdTest extends JodaTestBase
{
    static class DummyClassWithDate {
        @JsonFormat(shape = JsonFormat.Shape.STRING,
                pattern = "dd-MM-yyyy hh:mm:ss Z",
                with = JsonFormat.Feature.WRITE_DATES_WITH_ZONE_ID)
        public DateTime date;

        DummyClassWithDate() { }

        public DummyClassWithDate(DateTime date) {
            this.date = date;
        }
    }

    @Test
    public void testJacksonAnnotatedPOJOWithDateWithTimezoneToJson() throws Exception
    {
        final ObjectMapper mapper = jodaMapper();
        String ZONE_ID = "Asia/Krasnoyarsk";

        DummyClassWithDate input = new DummyClassWithDate(new DateTime(2015, 11, 23, 22, 06, 39,
                DateTimeZone.forID(ZONE_ID)));
        // 30-Jun-2016, tatu: Exact time seems to vary a bit based on DST, so let's actually
        //    just verify appending of timezone id itself:
        String json = mapper.writeValueAsString(input);
        if (!json.contains("\"23-11-2015")) {
            fail("Should contain time prefix, did not: "+json);
        }
        String match = String.format("[%s]", ZONE_ID);
        if (!json.contains(match)) {
            fail("Should contain zone id "+match+", does not: "+json);
        }
    }
}
