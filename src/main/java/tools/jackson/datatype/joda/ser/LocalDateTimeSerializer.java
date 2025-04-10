package tools.jackson.datatype.joda.ser;

import org.joda.time.*;

import tools.jackson.core.*;

import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.SerializationContext;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalDateTimeSerializer // non final since 2.6.1
    extends JodaDateSerializerBase<LocalDateTime>
{
    public LocalDateTimeSerializer() { this(FormatConfig.DEFAULT_LOCAL_DATETIME_PRINTER, 0); }
    public LocalDateTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public LocalDateTimeSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(LocalDateTime.class, format, DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_ARRAY, shapeOverride);
    }


    @Override
    public LocalDateTimeSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new LocalDateTimeSerializer(formatter, shapeOverride);
    }

    // is there a natural "empty" value to check against?
 /*
    @Override
    public boolean isEmpty(LocalDateTime value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalDateTime value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        switch (_serializationShape(ctxt)) {
        case FORMAT_STRING:
            gen.writeString(_format.createFormatter(ctxt).print(value));
            break;
        case FORMAT_TIMESTAMP:
            {
                // copied from `LocalDateTimeDeserializer`...
                DateTimeZone tz = _format.isTimezoneExplicit() ? _format.getTimeZone()
                        : DateTimeZone.forTimeZone(ctxt.getTimeZone());
                gen.writeNumber(value.toDateTime(tz).getMillis());
            }
            break;
        case FORMAT_ARRAY:
            // Timestamp here actually means an array of values
            gen.writeStartArray();
            gen.writeNumber(value.year().get());
            gen.writeNumber(value.monthOfYear().get());
            gen.writeNumber(value.dayOfMonth().get());
            gen.writeNumber(value.hourOfDay().get());
            gen.writeNumber(value.minuteOfHour().get());
            gen.writeNumber(value.secondOfMinute().get());
            gen.writeNumber(value.millisOfSecond().get());
            gen.writeEndArray();
        }
    }
}
