package tools.jackson.datatype.joda.ser;

import tools.jackson.core.*;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
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
        super(MonthDay.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_STRING, shapeOverride);
    }

    @Override
    public MonthDaySerializer withFormat(JacksonJodaDateFormat formatter,
                                         int shapeOverride) {
        return new MonthDaySerializer(formatter, shapeOverride);
    }

    @Override
    public void serialize(MonthDay value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        gen.writeString(_format.createFormatter(provider).print(value));
    }
}
