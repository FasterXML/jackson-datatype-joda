package com.fasterxml.jackson.datatype.joda.ser;

import java.util.*;

import org.joda.time.*;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

/**
 * Provider for serializers that handle some basic data types
 * for <a href="http://joda-time.sourceforge.net/">Joda</a> date/time library.
 */
public class JodaSerializers
{
    final static HashMap<Class<?>,JsonSerializer<?>> _serializers = new HashMap<Class<?>,JsonSerializer<?>>();
    static {
        _serializers.put(DateTime.class, new DateTimeSerializer());
        _serializers.put(LocalDateTime.class, new LocalDateTimeSerializer());
        _serializers.put(LocalDate.class, new LocalDateSerializer());
        _serializers.put(DateMidnight.class, new DateMidnightSerializer());
        // [JACKSON-706]:
        _serializers.put(Period.class, ToStringSerializer.instance);
    }

    public JodaSerializers() { }
}
