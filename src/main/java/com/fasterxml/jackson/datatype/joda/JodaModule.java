package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.core.Version;

import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleKeyDeserializers;
import com.fasterxml.jackson.databind.module.SimpleSerializers;

import com.fasterxml.jackson.datatype.joda.deser.*;
import com.fasterxml.jackson.datatype.joda.deser.key.*;
import com.fasterxml.jackson.datatype.joda.ser.*;

import org.joda.time.*;

public class JodaModule extends Module
    implements java.io.Serializable
{
    private static final long serialVersionUID = 3L;

    public JodaModule() { }

    @Override
    public String getModuleName() {
        return getClass().getName();
    }

    @Override
    public Version version() {
        return PackageVersion.VERSION;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void setupModule(SetupContext context)
    {
        SimpleDeserializers desers = new SimpleDeserializers()
                .addDeserializer(DateTime.class, DateTimeDeserializer.forType(DateTime.class))
                .addDeserializer(DateTimeZone.class, new DateTimeZoneDeserializer())
                .addDeserializer(Duration.class, new DurationDeserializer())
                .addDeserializer(Instant.class, new InstantDeserializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer())
                .addDeserializer(LocalDate.class, new LocalDateDeserializer())
                .addDeserializer(LocalTime.class, new LocalTimeDeserializer())
                .addDeserializer(ReadablePeriod.class, new PeriodDeserializer(false))
                .addDeserializer(ReadableDateTime.class, DateTimeDeserializer.forType(ReadableDateTime.class))
                .addDeserializer(ReadableInstant.class, DateTimeDeserializer.forType(ReadableInstant.class))
                .addDeserializer(Interval.class, new IntervalDeserializer())
                .addDeserializer(MonthDay.class, new MonthDayDeserializer())
                .addDeserializer(YearMonth.class, new YearMonthDeserializer())
                // DateMidnight deprecated since at least Joda 2.4, but not removed
                .addDeserializer(DateMidnight.class, new DateMidnightDeserializer())
                ;
        {
            @SuppressWarnings("unchecked")
            JsonDeserializer<Period> d = (JsonDeserializer<Period>)(JsonDeserializer<?>) 
                    new PeriodDeserializer(true);
            desers.addDeserializer(Period.class, d);
        }
        context.addDeserializers(desers);

        context.addSerializers(new SimpleSerializers()
                .addSerializer(DateTime.class, new DateTimeSerializer())
                .addSerializer(DateTimeZone.class, new DateTimeZoneSerializer())
                .addSerializer(Duration.class, new DurationSerializer())
                .addSerializer(Instant.class, new InstantSerializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                .addSerializer(LocalDate.class, new LocalDateSerializer())
                .addSerializer(LocalTime.class, new LocalTimeSerializer())
                .addSerializer(Period.class, new PeriodSerializer())
                .addSerializer(Interval.class, new IntervalSerializer())
                .addSerializer(MonthDay.class, new MonthDaySerializer())
                .addSerializer(YearMonth.class, new YearMonthSerializer())
                // DateMidnight deprecated since at least Joda 2.4, but not removed
                .addSerializer(DateMidnight.class, new DateMidnightSerializer())
        );

        // then key deserializers
        context.addKeyDeserializers(new SimpleKeyDeserializers()
                .addDeserializer(DateTime.class, new DateTimeKeyDeserializer())
                .addDeserializer(LocalTime.class, new LocalTimeKeyDeserializer())
                .addDeserializer(LocalDate.class, new LocalDateKeyDeserializer())
                .addDeserializer(LocalDateTime.class, new LocalDateTimeKeyDeserializer())
                .addDeserializer(Duration.class, new DurationKeyDeserializer())
                .addDeserializer(Period.class, new PeriodKeyDeserializer())
        );
    }
}
