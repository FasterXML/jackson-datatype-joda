package tools.jackson.datatype.joda.deser.key;

import tools.jackson.core.JacksonException;

import tools.jackson.databind.DeserializationContext;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateParser();

    @Override
    protected LocalDate deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return parser.parseLocalDate(key);
    }
}
