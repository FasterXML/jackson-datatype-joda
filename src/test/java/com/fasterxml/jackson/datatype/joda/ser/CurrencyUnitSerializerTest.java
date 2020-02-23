package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.money.CurrencyUnit;

public final class CurrencyUnitSerializerTest extends JodaTestBase
{
    private final ObjectMapper objectMapper = mapperWithModule();

    public void testShouldSerialize() throws JsonProcessingException {
        final String expectedCurrencyUnit = "EUR";

        final String actual = objectMapper.writeValueAsString(CurrencyUnit.EUR);

        assertEquals('"' + expectedCurrencyUnit + '"', actual);
    }
}
