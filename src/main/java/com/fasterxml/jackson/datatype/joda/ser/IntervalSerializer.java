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
    public IntervalSerializer() { this(FormatConfig.DEFAULT_DATETIME_FORMAT); }
    public IntervalSerializer(JacksonJodaDateFormat format) {
        super(Interval.class, format, false,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS);
    }

    @Override
    public IntervalSerializer withFormat(JacksonJodaDateFormat formatter) {
        return (_format == formatter) ? this : new IntervalSerializer(formatter);
    }

    @Override
    public boolean isEmpty(Interval value) {
        return (value.getStartMillis() == value.getEndMillis());
    }

    @Override
    public void serialize(Interval interval, JsonGenerator jsonGenerator, SerializerProvider provider) throws IOException
    {
        // 19-Nov-2014, tatu: Support textual representation similar to what Joda uses
        //   (and why not exact one? In future we'll make it configurable)
        String repr;
        if (_useTimestamp(provider)) {
            // !!! TODO: maybe allow textual format too?
            repr = interval.getStartMillis() + "-" + interval.getEndMillis();
        } else {
            DateTimeFormatter f = _format.createFormatter(provider);
            repr = f.print(interval.getStart()) + "/" + f.print(interval.getEnd());
        }
        jsonGenerator.writeString(repr);
    }
}
