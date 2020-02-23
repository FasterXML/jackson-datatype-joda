package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.joda.money.CurrencyUnit;

import java.io.IOException;

public class CurrencyUnitSerializer extends JodaSerializerBase<CurrencyUnit>
{
    public CurrencyUnitSerializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public void serialize(
            final CurrencyUnit currencyUnit,
            final JsonGenerator jsonGenerator,
            final SerializerProvider serializerProvider
    ) throws IOException {
        jsonGenerator.writeString(currencyUnit.getCode());
    }
}
