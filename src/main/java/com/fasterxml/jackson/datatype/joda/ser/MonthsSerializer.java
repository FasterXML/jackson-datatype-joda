package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.Months;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;

/**
 * Serializer for Joda {@link Months} class.
 *
 * @since 2.19
 */
public class MonthsSerializer
    extends JodaSerializerBase<Months>
{

    private static final long serialVersionUID = 1L;

    public MonthsSerializer() {
        super(Months.class);
    }

    @Override
    public void serialize(Months value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeNumber(value.getMonths());
    }
}
