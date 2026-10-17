package tools.jackson.datatype.joda.deser.key;

import java.io.IOException;
import java.util.function.Function;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.JacksonIOException;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.KeyDeserializer;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

abstract class JodaKeyDeserializer extends KeyDeserializer
{
    protected final static JacksonJodaPeriodFormat PERIOD_FORMAT = FormatConfig.DEFAULT_PERIOD_FORMAT;

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt)
            throws JacksonException
    {
        if (key.length() == 0) { // [JACKSON-360]
            return null;
        }
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt)
        throws JacksonException;

    /**
     * Helper method for parsing given key using Joda functionality,
     * so that Joda failures -- {@link IllegalArgumentException} (including
     * {@link org.joda.time.IllegalFieldValueException}) and {@link ArithmeticException}
     * (for overflows) -- are reported via
     * {@link DeserializationContext#handleWeirdKey} instead of escaping
     * as raw Joda exceptions (see [datatype-joda#195], [datatype-joda#200]).
     *
     * @since 3.3
     */
    @SuppressWarnings("unchecked")
    protected <K> K _parseKey(DeserializationContext ctxt, Class<K> keyType, String key,
            Function<String, ? extends K> parser)
        throws JacksonException
    {
        try {
            return parser.apply(key);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return (K) ctxt.handleWeirdKey(keyType, key, "%s", e.getMessage());
        }
    }

    protected JacksonException _wrapJodaFailure(IOException e) {
        // 18-Jan-2021, tatu: Start by simply reusing functionality of more
        //   general handling, for now
        return JacksonIOException.construct(e);
    }
}
