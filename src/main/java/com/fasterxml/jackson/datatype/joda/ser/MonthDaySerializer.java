package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.MonthDay;

import java.io.IOException;

public class MonthDaySerializer extends JodaDateSerializerBase<MonthDay>
{
    private static final long serialVersionUID = 1L;

    public MonthDaySerializer() { this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT, 0); }
    public MonthDaySerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public MonthDaySerializer(JacksonJodaDateFormat format,
                              int shapeOverride) {
        super(MonthDay.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_STRING, shapeOverride);
    }



    @Override
    public MonthDaySerializer withFormat(JacksonJodaDateFormat formatter,
                                         int shapeOverride) {
        return new MonthDaySerializer(formatter, shapeOverride);
    }

    @Override
    public void serialize(MonthDay value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        gen.writeString(_format.createFormatter(provider).print(value));
    }
}
