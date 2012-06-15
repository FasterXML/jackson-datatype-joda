package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

abstract class JodaSerializerBase<T> extends StdSerializer<T>
{
    final static DateTimeFormatter _localDateTimeFormat = ISODateTimeFormat.dateTime();
    final static DateTimeFormatter _localDateFormat = ISODateTimeFormat.date();

    protected JodaSerializerBase(Class<T> cls) { super(cls); }

    protected String printLocalDateTime(ReadablePartial dateValue)
        throws IOException, JsonProcessingException
    {
        return _localDateTimeFormat.print(dateValue);
    }

    protected String printLocalDate(ReadablePartial dateValue)
        throws IOException, JsonProcessingException
    {
        return _localDateFormat.print(dateValue);
    }

    protected String printLocalDate(ReadableInstant dateValue)
        throws IOException, JsonProcessingException
    {
        return _localDateFormat.print(dateValue);
    }

    @Override
    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
    
}