package tools.jackson.datatype.joda.ser;

import org.joda.time.Days;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Days} class.
 */
public class DaysSerializer
    extends JodaSerializerBase<Days>
{
    public DaysSerializer() {
        super(Days.class);
    }

    @Override
    public void serialize(Days value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getDays());
    }
}
