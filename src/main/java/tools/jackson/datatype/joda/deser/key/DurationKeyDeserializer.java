package tools.jackson.datatype.joda.deser.key;

import java.io.IOException;

import org.joda.time.Duration;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;

public class DurationKeyDeserializer extends JodaKeyDeserializer
{
    @Override
    protected Object deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        try {
            return PERIOD_FORMAT.parsePeriod(ctxt, key).toStandardDuration();
        } catch (IOException e) {
            throw _wrapJodaFailure(e);
        } catch (IllegalArgumentException | ArithmeticException
                | UnsupportedOperationException e) {
            // [datatype-joda#195]: do not let raw Joda exception escape;
            // `UnsupportedOperationException` for Period with years/months
            return ctxt.handleWeirdKey(Duration.class, key,
                    "Invalid Duration value: %s", e.getMessage());
        }
    }
}
