package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.*;
import org.joda.time.format.DateTimeFormatter;

public class IntervalSerializer extends JodaDateSerializerBase<Interval>
{
    private static final long serialVersionUID = 1L;

    public IntervalSerializer() { this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0); }
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
    public boolean isEmpty(SerializerProvider prov, Interval value) {
        return (value.getStartMillis() == value.getEndMillis());
    }

    @Override
    public void serialize(Interval interval, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        // 19-Nov-2014, tatu: Support textual representation similar to what Joda uses
        //   (and why not exact one? In future we'll make it configurable)
        String repr;

        if (_serializationShape(provider) == FORMAT_STRING) {
            DateTimeFormatter f = _format.createFormatter(provider);
            repr = f.print(interval.getStart()) + "/" + f.print(interval.getEnd());
        } else {
            // !!! TODO: maybe allow textual format too?
            repr = interval.getStartMillis() + "-" + interval.getEndMillis();
        }
        gen.writeString(repr);
    }
}
