package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Deserializer for Joda {@link DateTimeZone}.
 */
public class DateTimeZoneDeserializer extends JodaDeserializerBase<DateTimeZone>
{
    public DateTimeZoneDeserializer() { super(DateTimeZone.class); }

    @Override
    public DateTimeZone deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_NUMBER_INT) {
            // for fun let's allow use of offsets...
            return DateTimeZone.forOffsetHours(p.getIntValue());
        } else if (t == JsonToken.VALUE_STRING) {
            return DateTimeZone.forID(p.getText().trim());
        } else if (t == JsonToken.START_OBJECT) {
            JsonNode treeNode = p.readValueAsTree();
            String id = treeNode.path("ID").asText();
            if (id != null && !id.isEmpty()) {
                return DateTimeZone.forID(id);
            }
        }
        return _handleNotNumberOrString(p, ctxt);
    }
}
