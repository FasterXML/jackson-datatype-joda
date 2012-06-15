package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;

abstract class JodaDeserializerBase<T> extends StdScalarDeserializer<T> {

    final static DateTimeFormatter _localDateTimeFormat = ISODateTimeFormat.localDateOptionalTimeParser();

    protected JodaDeserializerBase(Class<T> cls) {
        super(cls);
    }

    protected DateTime parseLocal(JsonParser jp)
            throws IOException, JsonProcessingException {
        String str = jp.getText().trim();
        if (str.length() == 0) { // [JACKSON-360]
            return null;
        }
        return _localDateTimeFormat.parseDateTime(str);
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromAny(jp, ctxt);
    }
}