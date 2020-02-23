package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.money.Money;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class MoneySerializer extends JodaSerializerBase<Money>
{
    public MoneySerializer() {
        super(Money.class);
    }

    @Override
    public void serialize(
            final Money money,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider
    ) throws IOException {
        final BigDecimal decimal = money.getAmount();
        final int decimalPlaces = money.getCurrencyUnit().getDecimalPlaces();
        final int scale = Math.max(decimal.scale(), decimalPlaces);

        jsonGenerator.writeStartObject();
        jsonGenerator.writeObjectField("amount", decimal.setScale(scale, RoundingMode.UNNECESSARY));
        jsonGenerator.writeObjectField("currency", money.getCurrencyUnit());
        jsonGenerator.writeEndObject();
    }
}
