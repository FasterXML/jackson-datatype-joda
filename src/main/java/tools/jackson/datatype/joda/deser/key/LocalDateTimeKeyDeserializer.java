package tools.jackson.datatype.joda.deser.key;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.DeserializationContext;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateTimeKeyDeserializer extends JodaKeyDeserializer
{
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateOptionalTimeParser();

    @Override
    protected LocalDateTime deserialize(String key, DeserializationContext ctxt)
        throws JacksonException
    {
        return _parseKey(ctxt, LocalDateTime.class, key, k -> {
            final LocalDateTime dt = parser.parseLocalDateTime(k);
            _verifyYear(dt.getYear());
            return dt;
        });
    }
}
