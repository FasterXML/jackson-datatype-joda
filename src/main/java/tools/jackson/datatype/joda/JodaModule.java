package tools.jackson.datatype.joda;

import tools.jackson.core.Version;

import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.JacksonModule;
import tools.jackson.databind.module.SimpleDeserializers;
import tools.jackson.databind.module.SimpleKeyDeserializers;
import tools.jackson.databind.module.SimpleSerializers;
import tools.jackson.datatype.joda.deser.*;
import tools.jackson.datatype.joda.deser.key.*;
import tools.jackson.datatype.joda.ser.*;

import org.joda.time.*;

public class JodaModule extends JacksonModule
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
                ;
        {
            @SuppressWarnings("unchecked")
            ValueDeserializer<Period> d = (ValueDeserializer<Period>)(ValueDeserializer<?>) 
                    new PeriodDeserializer(true);
            desers.addDeserializer(Period.class, d);
        }
        context.addDeserializers(desers);

        context.addSerializers(new SimpleSerializers()
                .addSerializer(DateTime.class, new DateTimeSerializer())
                .addSerializer(DateTimeZone.class, new DateTimeZoneSerializer())
                .addSerializer(Days.class, new DaysSerializer())
                .addSerializer(Duration.class, new DurationSerializer())
                .addSerializer(Instant.class, new InstantSerializer())
                .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer())
                .addSerializer(LocalDate.class, new LocalDateSerializer())
                .addSerializer(LocalTime.class, new LocalTimeSerializer())
                .addSerializer(Period.class, new PeriodSerializer())
                .addSerializer(Interval.class, new IntervalSerializer())
                .addSerializer(MonthDay.class, new MonthDaySerializer())
                .addSerializer(YearMonth.class, new YearMonthSerializer())
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
