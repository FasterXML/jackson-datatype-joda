package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;

public final class MoneySerializerTest extends JodaTestBase
{
    public void testShouldSerialize() throws JsonProcessingException {
        final ObjectMapper objectMapper = mapperWithModule();

        final String expected = "{\"amount\":19.99,\"currency\":\"EUR\"}";
        final String actual = objectMapper.writeValueAsString(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99)));

        assertEquals(expected, actual);
    }
}
