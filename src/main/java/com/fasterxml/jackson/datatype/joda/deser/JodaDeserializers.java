package com.fasterxml.jackson.datatype.joda.deser;

import org.joda.time.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;

/**
 * To support types and some of subtypes as well, need to register
 * deserializers using bit more advanced matching.
 */
public class JodaDeserializers
    // extends SimpleDeserializer just so we can override one SimpleModule uses
    extends SimpleDeserializers
{
    @Override
    public JsonDeserializer<?> findBeanDeserializer(JavaType type,
            DeserializationConfig config, BeanDescription beanDesc)
        throws JsonMappingException
    {
        Class<?> raw = type.getRawClass();
        
        if (raw == DateMidnight.class) {
            return new DateMidnightDeserializer();
        }
        if (raw == LocalDateTime.class) {
            return new LocalDateTimeDeserializer();
        }
        if (raw == LocalDate.class) {
            return new LocalDateDeserializer();
        }
        if (raw.isAssignableFrom(Period.class)) {
            return new PeriodDeserializer();
        }
        if (raw.isAssignableFrom(DateTime.class)) {
            return new DateTimeDeserializer();
        }
        return null;
    }
}
