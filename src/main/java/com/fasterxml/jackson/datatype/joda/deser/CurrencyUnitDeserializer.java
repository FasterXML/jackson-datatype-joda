package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import org.joda.money.CurrencyUnit;

import java.io.IOException;

public class CurrencyUnitDeserializer extends StdDeserializer<CurrencyUnit>
{
    public CurrencyUnitDeserializer() {
        super(CurrencyUnit.class);
    }

    @Override
    public CurrencyUnit deserialize(
            final JsonParser jsonParser,
            final DeserializationContext deserializationContext
    ) throws IOException {
        final String currencyCode = jsonParser.getValueAsString();

        return CurrencyUnit.of(currencyCode);
    }
}
