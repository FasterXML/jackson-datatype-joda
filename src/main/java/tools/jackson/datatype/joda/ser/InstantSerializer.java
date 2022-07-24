package tools.jackson.datatype.joda.ser;

import org.joda.time.Instant;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public class InstantSerializer
    extends JodaDateSerializerBase<Instant>
{
    public InstantSerializer() { this(FormatConfig.DEFAULT_DATETIME_PRINTER, 0); }
    public InstantSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }

    public InstantSerializer(JacksonJodaDateFormat format,
            int shapeOverride) {
        super(Instant.class, format, SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                FORMAT_TIMESTAMP, shapeOverride);
    }

    @Override
    public InstantSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new InstantSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Instant value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(Instant value, JsonGenerator gen, SerializerProvider provider)
        throws JacksonException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            gen.writeString(_format.createFormatter(provider).print(value));
        } else {
            gen.writeNumber(value.getMillis());
        }
    }
}
