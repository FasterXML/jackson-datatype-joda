package com.fasterxml.jackson.datatype.joda;

import org.joda.time.*;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.joda.deser.*;
import com.fasterxml.jackson.datatype.joda.ser.*;

public class JodaModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    public JodaModule()
    {
        super(PackageVersion.VERSION);
        // first deserializers
        addDeserializer(DateMidnight.class, new DateMidnightDeserializer());
        addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class));
        addDeserializer(DateTimeZone.class, new DateTimeZoneDeserializer());
                
        addDeserializer(Duration.class, new DurationDeserializer());
        addDeserializer(Instant.class, new InstantDeserializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
        addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        addDeserializer(Period.class, new PeriodDeserializer());
        addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class));
        addDeserializer(ReadableInstant.class, DateTimeDeserializer.forType(ReadableInstant.class));
        addDeserializer(Interval.class, new IntervalDeserializer());
        addDeserializer(MonthDay.class, new MonthDayDeserializer());
        addDeserializer(YearMonth.class, new YearMonthDeserializer());

        // then serializers:
        final JsonSerializer<Object> stringSer = ToStringSerializer.instance;
        addSerializer(DateMidnight.class, new DateMidnightSerializer());
        addSerializer(DateTime.class, new DateTimeSerializer());
        addSerializer(DateTimeZone.class, new DateTimeZoneSerializer());
        addSerializer(Duration.class, new DurationSerializer());
        addSerializer(Instant.class, new InstantSerializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addSerializer(LocalTime.class, new LocalTimeSerializer());
        addSerializer(Period.class, new PeriodSerializer());
        addSerializer(Interval.class, new IntervalSerializer());
        addSerializer(MonthDay.class, stringSer);
        addSerializer(YearMonth.class, stringSer);

        // then key deserializers - only one included for DateTime here.
        addKeyDeserializer(DateTime.class, new DateTimeKeyDeserializer());
    }

    @Override
    public int hashCode()
    {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        return this == o;
    }
}
