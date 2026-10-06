package tools.jackson.datatype.joda.deser.key;

import org.joda.time.Period;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;

import java.io.IOException;

public class PeriodKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected Object deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key);
        } catch (IOException e) {
            throw _wrapJodaFailure(e);
        } catch (IllegalArgumentException | ArithmeticException e) {
            // includes `NumberFormatException` for out-of-range components
            return ctxt.handleWeirdKey(Period.class, key,
                    "Invalid Period value: %s", e.getMessage());
        }
    }
}
