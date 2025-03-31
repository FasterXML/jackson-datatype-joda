package tools.jackson.datatype.joda.ser;

import org.joda.time.Years;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Years} class.
 */
public class YearsSerializer
    extends JodaSerializerBase<Years>
{
    public YearsSerializer() {
        super(Years.class);
    }

    @Override
    public void serialize(Years value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getYears());
    }
}
