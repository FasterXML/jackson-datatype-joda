package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaTestBase;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.IOException;
import java.math.BigDecimal;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

public final class MoneyDeserializerTest extends JodaTestBase
{
    public void testShouldDeserialize() throws IOException {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"amount\":19.99,\"currency\":\"EUR\"}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(19.99)), actualAmount);
        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenAmountIsAStringValue() throws IOException {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\",\"amount\":\"19.99\"}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldDeserializeWhenOrderIsDifferent() throws IOException {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\",\"amount\":19.99}";
        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(BigDecimal.valueOf(19.99), actualAmount.getAmount());
        assertEquals(actualAmount.getCurrencyUnit().getCode(), "EUR");
    }

    public void testShouldFailDeserializationWithoutAmount() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"currency\":\"EUR\"}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Amount must not be null");
        } catch (final IOException e) {
            fail("NullPointerException should have been thrown");
        }
    }

    public void testShouldFailDeserializationWithoutCurrency() {
        final ObjectMapper objectMapper = mapperWithModule();

        final String content = "{\"amount\":5000}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final NullPointerException e) {
            verifyException(e, "Currency must not be null");
        } catch (final IOException e) {
            fail("NullPointerException should have been thrown");
        }
    }

    public void testShouldFailDeserializationWithUnknownProperties() {
        final ObjectMapper objectMapper = mapperWithModuleBuilder()
                .enable(FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        try {
            objectMapper.readValue(content, Money.class);
            fail();
        } catch (final IOException e) {
            verifyException(e, "test");
        }
    }

    public void testShouldPerformDeserializationWithUnknownProperties() throws IOException {
        final ObjectMapper objectMapper = mapperWithModuleBuilder()
                .disable(FAIL_ON_UNKNOWN_PROPERTIES)
                .build();

        final String content = "{\"amount\":5000,\"currency\":\"EUR\",\"unknown\":\"test\"}";

        final Money actualAmount = objectMapper.readValue(content, Money.class);

        assertEquals(Money.of(CurrencyUnit.EUR, BigDecimal.valueOf(5000)), actualAmount);
    }
}
