package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.core.*;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import org.joda.time.YearMonth;

import java.io.IOException;

public class YearMonthSerializer extends JodaDateSerializerBase<YearMonth>
{
    private static final long serialVersionUID = 1L;

    public YearMonthSerializer() { this(FormatConfig.DEFAULT_YEAR_MONTH_FORMAT, 0); }
    public YearMonthSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public YearMonthSerializer(JacksonJodaDateFormat format,
                               int shapeOverride) {
        super(YearMonth.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_STRING, shapeOverride);
    }



    @Override
    public YearMonthSerializer withFormat(JacksonJodaDateFormat formatter,
                                          int shapeOverride) {
        return new YearMonthSerializer(formatter, shapeOverride);
    }

    @Override
    public void serialize(YearMonth value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        gen.writeString(_format.createFormatter(provider).print(value));
    }
}
