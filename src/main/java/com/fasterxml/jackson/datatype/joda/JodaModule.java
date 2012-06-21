package com.fasterxml.jackson.datatype.joda;

import org.joda.time.*;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.fasterxml.jackson.datatype.joda.deser.*;
import com.fasterxml.jackson.datatype.joda.ser.*;

public class JodaModule extends SimpleModule
{
    /*
    /**********************************************************
    /* Life-cycle
    /**********************************************************
     */
    
    public JodaModule()
    {
        super(ModuleVersion.instance.version());
        addDeserializer(DateMidnight.class, new DateMidnightDeserializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
        addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        addDeserializer(Period.class, new PeriodDeserializer());
        addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class));
        addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class));
        addDeserializer(ReadableInstant.class, DateTimeDeserializer.forType(ReadableInstant.class));
        addDeserializer(Duration.class, new DurationDeserializer());
        
        // then serializers:
        addSerializer(DateTime.class, new DateTimeSerializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addSerializer(LocalTime.class, new LocalTimeSerializer());
        addSerializer(DateMidnight.class, new DateMidnightSerializer());
        addSerializer(Period.class, ToStringSerializer.instance);
        addSerializer(Duration.class, new DurationSerializer());
    }
}