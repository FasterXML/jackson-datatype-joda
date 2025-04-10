package tools.jackson.datatype.joda.ser;

import tools.jackson.core.*;

import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.MonthDay;

public class MonthDaySerializer extends JodaDateSerializerBase<MonthDay>
{
    public MonthDaySerializer() { this(FormatConfig.DEFAULT_MONTH_DAY_FORMAT, 0); }
    public MonthDaySerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    public MonthDaySerializer(JacksonJodaDateFormat format,
                              int shapeOverride) {
        super(MonthDay.class, format, DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_STRING, shapeOverride);
    }

    @Override
    public MonthDaySerializer withFormat(JacksonJodaDateFormat formatter,
                                         int shapeOverride) {
        return new MonthDaySerializer(formatter, shapeOverride);
    }

    @Override
    public void serialize(MonthDay value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        gen.writeString(_format.createFormatter(ctxt).print(value));
    }
}
