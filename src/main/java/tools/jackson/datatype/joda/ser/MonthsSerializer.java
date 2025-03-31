package tools.jackson.datatype.joda.ser;

import org.joda.time.Months;

import tools.jackson.core.JsonGenerator;
import tools.jackson.databind.SerializationContext;

/**
 * Serializer for Joda {@link Months} class.
 */
public class MonthsSerializer
    extends JodaSerializerBase<Months>
{
    public MonthsSerializer() {
        super(Months.class);
    }

    @Override
    public void serialize(Months value, JsonGenerator gen, SerializationContext ctxt) {
        gen.writeNumber(value.getMonths());
    }
}
