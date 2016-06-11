package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Instant;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.*;

/**
 * Basic deserializer for {@link org.joda.time.ReadableDateTime} and its subtypes.
 * Accepts JSON String and Number values and passes those to single-argument constructor.
 * Does not (yet?) support JSON object; support can be added if desired.
 */
public class InstantDeserializer
    extends JodaDeserializerBase<Instant>
{
    private static final long serialVersionUID = 1L;

    public InstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        JsonToken t = p.getCurrentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Instant(p.getLongValue());
        }
        if (t == JsonToken.VALUE_STRING) {
            String str = p.getText().trim();
            if (str.length() == 0) { // [JACKSON-360]
                return null;
            }
            return new Instant(str);
        }
        return (Instant) ctxt.handleUnexpectedToken(handledType(), p);
    }
}
