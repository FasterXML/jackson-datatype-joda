package com.fasterxml.jackson.datatype.joda;

import org.joda.time.*;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

import com.fasterxml.jackson.datatype.joda.deser.*;
import com.fasterxml.jackson.datatype.joda.ser.DateMidnightSerializer;
import com.fasterxml.jackson.datatype.joda.ser.DateTimeSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.joda.ser.LocalDateTimeSerializer;

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
        
        // First: add deserializers
        setDeserializers(new JodaDeserializers());
        
        // then serializers:
        addSerializer(DateTime.class, new DateTimeSerializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addSerializer(DateMidnight.class, new DateMidnightSerializer());
        addSerializer(Period.class, ToStringSerializer.instance);
    }
}