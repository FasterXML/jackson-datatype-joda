package tools.jackson.datatype.joda.ser;

import tools.jackson.core.*;

import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.YearMonth;

public class YearMonthSerializer extends JodaDateSerializerBase<YearMonth>
{
    public YearMonthSerializer() { this(FormatConfig.DEFAULT_YEAR_MONTH_FORMAT, 0); }
    public YearMonthSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public YearMonthSerializer(JacksonJodaDateFormat format,
                               int shapeOverride) {
        super(YearMonth.class, format, DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_STRING, shapeOverride);
    }

    @Override
    public YearMonthSerializer withFormat(JacksonJodaDateFormat formatter, int shapeOverride)
    {
        return new YearMonthSerializer(formatter, shapeOverride);
    }

    @Override
    public void serialize(YearMonth value, JsonGenerator g, SerializationContext ctxt)
        throws JacksonException
    {
        g.writeString(_format.createFormatter(ctxt).print(value));
    }
}
