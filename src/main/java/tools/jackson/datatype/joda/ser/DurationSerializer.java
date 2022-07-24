package tools.jackson.datatype.joda.ser;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.SerializationFeature;
import tools.jackson.databind.SerializerProvider;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

import org.joda.time.Duration;

/**
 * Serializes a Duration; either as number of millis, or, if textual output
 * requested, using ISO-8601 format.
 */
public class DurationSerializer
    extends JodaDateSerializerBase<Duration>
{
    // NOTE: formatter is not really used directly for printing, but we do need
    // it as container for numeric/textual distinction
    
    public DurationSerializer() { this(FormatConfig.DEFAULT_DATEONLY_FORMAT, 0); }

    public DurationSerializer(JacksonJodaDateFormat format) {
        this(format, 0);
    }
    
    public DurationSerializer(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        // false -> no arrays (numbers)
        super(Duration.class, formatter,
                SerializationFeature.WRITE_DURATIONS_AS_TIMESTAMPS,
                FORMAT_TIMESTAMP, shapeOverride);
    }

    @Override
    public DurationSerializer withFormat(JacksonJodaDateFormat formatter,
            int shapeOverride) {
        return new DurationSerializer(formatter, shapeOverride);
    }

    @Override
    public boolean isEmpty(SerializerProvider prov, Duration value) {
        return (value.getMillis() == 0L);
    }

    @Override
    public void serialize(Duration value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        if (_serializationShape(provider) == FORMAT_STRING) {
            g.writeString(value.toString());
        } else {
            g.writeNumber(value.getMillis());
        }
    }
}
