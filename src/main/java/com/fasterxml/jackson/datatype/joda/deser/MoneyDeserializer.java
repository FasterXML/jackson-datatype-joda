package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Collections;

public class MoneyDeserializer extends StdDeserializer<Money>
{
    public MoneyDeserializer() {
        super(Money.class);
    }

    @Override
    public Money deserialize(
            final JsonParser jsonParser,
            final DeserializationContext deserializationContext
    ) throws IOException {
        BigDecimal amount = null;
        CurrencyUnit currencyUnit = null;
        while (jsonParser.nextToken() != JsonToken.END_OBJECT) {
            final String field = jsonParser.currentName();

            jsonParser.nextToken();

            if ("amount".equals(field)) {
                amount = deserializationContext.readValue(jsonParser, BigDecimal.class);
            } else if ("currency".equals(field)) {
                currencyUnit = deserializationContext.readValue(jsonParser, CurrencyUnit.class);
            } else if (deserializationContext.isEnabled(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)) {
                throw UnrecognizedPropertyException.from(
                        jsonParser, Money.class, field, Collections.singletonList("amount, currency")
                );
            } else {
                jsonParser.skipChildren();
            }
        }

        return Money.of(currencyUnit, amount);
    }
}
