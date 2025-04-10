package tools.jackson.datatype.joda.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.*;

public class DateTimeSerializer
    extends JodaDateSerializerBase<DateTime>
{
    public DateTimeSerializer() {
        this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0);
    }

    public DateTimeSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public DateTimeSerializer(JacksonJodaDateFormat format,
            int shapeOverride)
    {
        // false -> no arrays (numbers)
        super(DateTime.class, format,
                DateTimeFeature.WRITE_DATES_AS_TIMESTAMPS, FORMAT_TIMESTAMP,
                shapeOverride);
    }

    @Override
    public DateTimeSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new DateTimeSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializationContext ctxt, DateTime value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(DateTime value, JsonGenerator gen, SerializationContext ctxt)
        throws JacksonException
    {
        boolean numeric = (_serializationShape(ctxt) != FORMAT_STRING);

        // First: simple, non-timezone-included output
        if (!writeWithZoneId(ctxt)) {
            if (numeric) {
                gen.writeNumber(value.getMillis());
            } else {
                gen.writeString(_format.createFormatter(ctxt).print(value));
            }
        } else {
            // and then as per [datatype-joda#44], optional TimeZone inclusion
            if (numeric) {
                /* 12-Jul-2015, tatu: Initially planned to support "timestamp[zone-id]"
                 *    format as well as textual, but since JSR-310 datatype (Java 8 datetime)
                 *    does not support it, was left out of 2.6.
                 */
                /*
                sb = new StringBuilder(20)
                .append(value.getMillis());
                */

                gen.writeNumber(value.getMillis());
                return;
            }
            StringBuilder sb = new StringBuilder(40)
                    .append(_format.createFormatter(ctxt).withOffsetParsed().print(value));
            sb = sb.append('[')
                    .append(value.getZone())
                    .append(']');
            gen.writeString(sb.toString());
        }
    }
}
