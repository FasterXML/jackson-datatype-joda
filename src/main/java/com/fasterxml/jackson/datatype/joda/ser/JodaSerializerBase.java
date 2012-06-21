package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import java.io.IOException;

import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

abstract class JodaSerializerBase<T> extends StdSerializer<T>
{

    protected JodaSerializerBase(Class<T> cls) { super(cls); }

    // protected String printDateTime(ReadablePartial dateValue)
    //     throws IOException, JsonProcessingException
    // {
    //     return dateTimeFormat.print(dateValue);
    // }
    
    // protected String printDate(ReadablePartial dateValue)
    //     throws IOException, JsonProcessingException
    // {
    //     return dateFormat.print(dateValue);
    // }

    // protected String printTime(ReadablePartial timeValue)
    //     throws IOException, JsonProcessingException
    // {
    //     return timeFormat.print(timeValue);
    // }

    @Override
    public void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonProcessingException {
        typeSer.writeTypePrefixForScalar(value, jgen);
        serialize(value, jgen, provider);
        typeSer.writeTypeSuffixForScalar(value, jgen);
    }
    
}