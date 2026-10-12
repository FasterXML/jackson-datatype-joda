package tools.jackson.datatype.joda;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import tools.jackson.datatype.joda.deser.DateTimeDeserializer;
import tools.jackson.datatype.joda.ser.LocalDateTimeSerializer;

import static org.junit.jupiter.api.Assertions.*;

// [datatype-joda#194]: `@JsonFormat(timezone)` must be considered explicit even if
// it is the same as the (non-explicit) zone of the default format; verify for
// serialization and String parsing (numeric deserialization tested elsewhere)
public class JsonFormatTimeZoneSameAsDefaultTest extends JodaTestBase
{
    // 2009-04-01T04:03:02.001Z == 2009-04-01T12:03:02.001 in Asia/Taipei
    private final static long TIMESTAMP = 1238558582001L;

    private final static DateTimeZone TAIPEI = DateTimeZone.forID("Asia/Taipei");

    // Default formats use JVM default zone: mimic that with a non-explicit
    // Taipei zone to avoid dependency on JVM zone
    private final static JacksonJodaDateFormat TAIPEI_DEFAULT_PARSER = new JacksonJodaDateFormat(
            ISODateTimeFormat.dateTimeParser().withZone(TAIPEI));

    // String without offset must be parsed using configured zone, not context zone (UTC)
    @SuppressWarnings("unchecked")
    @Test
    public void testDateTimeDeserFromStringWithFormatTimeZoneSameAsDefault() throws Exception
    {
        ValueDeserializer<DateTime> deser = (ValueDeserializer<DateTime>) (ValueDeserializer<?>)
                new DateTimeDeserializer(DateTime.class, TAIPEI_DEFAULT_PARSER);
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("UTC"))
                .addModule(new SimpleModule()
                        .addDeserializer(DateTime.class, deser))
                .withConfigOverride(DateTime.class,
                        o -> o.setFormat(JsonFormat.Value.empty().withTimeZone(TAIPEI.toTimeZone())))
                .build();
        DateTime result = mapper.readValue(q("2009-04-01T12:03:02.001"), DateTime.class);
        assertEquals(TIMESTAMP, result.getMillis());
    }

    @Test
    public void testLocalDateTimeSerAsTimestampWithFormatTimeZoneSameAsDefault() throws Exception
    {
        JacksonJodaDateFormat fmt = new JacksonJodaDateFormat(
                ISODateTimeFormat.dateTime().withZone(TAIPEI));
        ObjectMapper mapper = jodaMapperBuilder(TimeZone.getTimeZone("UTC"))
                .addModule(new SimpleModule()
                        .addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(fmt)))
                .withConfigOverride(LocalDateTime.class,
                        o -> o.setFormat(JsonFormat.Value.forShape(JsonFormat.Shape.NUMBER)
                                .withTimeZone(TAIPEI.toTimeZone())))
                .build();
        assertEquals(String.valueOf(TIMESTAMP),
                mapper.writeValueAsString(new LocalDateTime(2009, 4, 1, 12, 3, 2, 1)));
    }
}
