package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;

abstract class JodaDeserializerBase<T> extends StdScalarDeserializer<T>
{
    final static DateTimeFormatter _localDateTimeFormat = ISODateTimeFormat.localDateOptionalTimeParser();

    protected JodaDeserializerBase(Class<T> cls) { super(cls); }

    protected DateTime parseLocal(JsonParser jp)
        throws IOException, JsonProcessingException
    {
        String str = jp.getText().trim();
        if (str.length() == 0) { // [JACKSON-360]
            return null;
        }
        return _localDateTimeFormat.parseDateTime(str);
    }
}