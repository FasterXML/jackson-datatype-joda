package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.deser.*;
import com.fasterxml.jackson.datatype.joda.deser.key.*;
import com.fasterxml.jackson.datatype.joda.ser.*;

import org.joda.time.*;

public class JodaModule extends SimpleModule
{
    private static final long serialVersionUID = 1L;

    @SuppressWarnings({ "unchecked", "deprecation" })
    public JodaModule()
    {
        super(PackageVersion.VERSION);
        // first deserializers
        addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class));
        addDeserializer(DateTimeZone.class, new DateTimeZoneDeserializer());

        addDeserializer(Duration.class, new DurationDeserializer());
        addDeserializer(Instant.class, new InstantDeserializer());
        addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        addDeserializer(LocalDate.class, new LocalDateDeserializer());
        addDeserializer(LocalTime.class, new LocalTimeDeserializer());
        JsonDeserializer<?> deser = new PeriodDeserializer(true);
        addDeserializer(Period.class, (JsonDeserializer<Period>) deser);
        addDeserializer(ReadablePeriod.class, new PeriodDeserializer(false));
        addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class));
        addDeserializer(ReadableInstant.class, DateTimeDeserializer.forType(ReadableInstant.class));
        addDeserializer(Interval.class, new IntervalDeserializer());
        addDeserializer(MonthDay.class, new MonthDayDeserializer());
        addDeserializer(YearMonth.class, new YearMonthDeserializer());

        // then serializers:
        addSerializer(DateTime.class, new DateTimeSerializer());
        addSerializer(DateTimeZone.class, new DateTimeZoneSerializer());
        addSerializer(Duration.class, new DurationSerializer());
        addSerializer(Instant.class, new InstantSerializer());
        addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        addSerializer(LocalDate.class, new LocalDateSerializer());
        addSerializer(LocalTime.class, new LocalTimeSerializer());
        addSerializer(Period.class, new PeriodSerializer());
        addSerializer(Interval.class, new IntervalSerializer());
        addSerializer(MonthDay.class, new MonthDaySerializer());
        addSerializer(YearMonth.class, new YearMonthSerializer());

        // then key deserializers
        addKeyDeserializer(DateTime.class, new DateTimeKeyDeserializer());
        addKeyDeserializer(LocalTime.class, new LocalTimeKeyDeserializer());
        addKeyDeserializer(LocalDate.class, new LocalDateKeyDeserializer());
        addKeyDeserializer(LocalDateTime.class, new LocalDateTimeKeyDeserializer());
        addKeyDeserializer(Duration.class, new DurationKeyDeserializer());
        addKeyDeserializer(Period.class, new PeriodKeyDeserializer());

        // 26-Dec-2015, tatu: Joda has deprecated following types:
        
        // DateMidnight since at least Joda 2.4:
        addDeserializer(DateMidnight.class, new DateMidnightDeserializer());
        addSerializer(DateMidnight.class, new DateMidnightSerializer());
    }

    // yes, will try to avoid duplicate registrations (if MapperFeature enabled)
    @Override
    public String getModuleName() {
        return getClass().getSimpleName();
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }
}
