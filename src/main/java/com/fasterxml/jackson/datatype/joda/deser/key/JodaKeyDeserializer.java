package com.fasterxml.jackson.datatype.joda.deser.key;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

import java.io.IOException;
import java.util.function.Function;

abstract class JodaKeyDeserializer extends KeyDeserializer
    implements java.io.Serializable
{
    private static final long serialVersionUID = 1L;

    protected final static JacksonJodaPeriodFormat PERIOD_FORMAT = FormatConfig.DEFAULT_PERIOD_FORMAT;

    @Override
    public final Object deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key.length() == 0) { // [JACKSON-360]
            return null;
        }
        return deserialize(key, ctxt);
    }

    protected abstract Object deserialize(String key, DeserializationContext ctxt) throws IOException;

    /**
     * Helper method for parsing given key using Joda functionality,
     * so that Joda failures -- {@link IllegalArgumentException} (including
     * {@link org.joda.time.IllegalFieldValueException}) and {@link ArithmeticException}
     * (for overflows) -- are reported via
     * {@link DeserializationContext#handleWeirdKey} instead of escaping
     * as raw Joda exceptions (see [datatype-joda#195], [datatype-joda#200]).
     *
     * @since 2.23
     */
    @SuppressWarnings("unchecked")
    protected <K> K _parseKey(DeserializationContext ctxt, Class<K> keyType, String key,
            Function<String, ? extends K> parser)
        throws IOException
    {
        try {
            return parser.apply(key);
        } catch (IllegalArgumentException | ArithmeticException e) {
            return (K) ctxt.handleWeirdKey(keyType, key, "%s", e.getMessage());
        }
    }
}
