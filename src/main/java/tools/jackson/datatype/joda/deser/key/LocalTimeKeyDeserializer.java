package tools.jackson.datatype.joda.deser.key;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalTimeKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localTimeParser();

    @Override
    protected LocalTime deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return parser.parseLocalTime(key);
    }
}
