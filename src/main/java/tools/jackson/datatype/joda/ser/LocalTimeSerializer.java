package tools.jackson.datatype.joda.ser;

import org.joda.time.LocalTime;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class LocalTimeSerializer
    extends JodaDateSerializerBase<LocalTime>
{
    public LocalTimeSerializer() {
        this(FormatConfig.DEFAULT_LOCAL_TIMEONLY_PRINTER, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public LocalTimeSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(LocalTime.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_ARRAY, shapeOverride);
    }

    @Override
    public LocalTimeSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new LocalTimeSerializer(formatter, shapeOverride);
    }

    // is there a natural "empty" value to check against?
    /*
    @Override
    public boolean isEmpty(LocalTime value) {
        return (value.getMillis() == 0L);
    }
    */

    @Override
    public void serialize(LocalTime value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            g.writeString(_format.createFormatter(provider).print(value));
            return;
        }
        // Timestamp here actually means an array of values
        g.writeStartArray();
        g.writeNumber(value.hourOfDay().get());
        g.writeNumber(value.minuteOfHour().get());
        g.writeNumber(value.secondOfMinute().get());
        g.writeNumber(value.millisOfSecond().get());
        g.writeEndArray();
    }
}
