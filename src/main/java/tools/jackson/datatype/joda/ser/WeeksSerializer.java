package tools.jackson.datatype.joda.ser;

import org.joda.time.Weeks;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Weeks} class.
 */
public class WeeksSerializer
    extends JodaSerializerBase<Weeks>
{
    public WeeksSerializer() {
        super(Weeks.class);
    }

    @Override
    public void serialize(Weeks value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getWeeks());
    }
}
