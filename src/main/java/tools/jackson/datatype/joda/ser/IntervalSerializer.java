package tools.jackson.datatype.joda.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;

public class IntervalSerializer extends JodaDateSerializerBase<Interval>
{
    public IntervalSerializer() { this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0); }
    public IntervalSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public IntervalSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(Interval.class, format, SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                FORMAT_TIMESTAMP, shapeOverride);
    }

    @Override
    public IntervalSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new IntervalSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, Interval value) {
        return (value.getStartMillis() == value.getEndMillis());
    }

    @Override
    public void serialize(Interval interval, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        // 19-Nov-2014, tatu: Support textual representation similar to what Joda uses
        //   (and why not exact one? In future we'll make it configurable)
        String repr;

        if (_serializationShape(ctxt) == FORMAT_STRING) {
            DateTimeFormatter f = _format.createFormatter(ctxt);
            repr = f.print(interval.getStart()) + "/" + f.print(interval.getEnd());
        } else {
            // !!! TODO: maybe allow textual format too?
            repr = interval.getStartMillis() + "-" + interval.getEndMillis();
        }
        gen.writeString(repr);
    }
}
