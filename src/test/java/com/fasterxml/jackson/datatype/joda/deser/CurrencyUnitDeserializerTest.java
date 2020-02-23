package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.money.CurrencyUnit;
import org.joda.money.IllegalCurrencyException;

import java.io.IOException;

public final class CurrencyUnitDeserializerTest extends JodaTestBase
{
    private final ObjectMapper objectMapper = mapperWithModule();

    public void testShouldDeserialize() throws IOException {
        final CurrencyUnit actualCurrencyUnit = objectMapper.readValue("\"EUR\"", CurrencyUnit.class);

        assertEquals(CurrencyUnit.EUR, actualCurrencyUnit);
    }

    public void testShouldNotDeserializeInvalidCurrency() {
        try {
            objectMapper.readValue("\"UNKNOWN\"", CurrencyUnit.class);
            fail();
        } catch (final IllegalCurrencyException e) {
            verifyException(e, "Unknown currency 'UNKNOWN'");
        } catch (final IOException e) {
            fail("IllegalCurrencyException should have been thrown");
        }
    }
}
