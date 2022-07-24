package tools.jackson.datatype.joda.deser.key;

import java.io.IOException;

import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.WrappedIOException;
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

    protected JacksonException _wrapJodaFailure(IOException e) {
        // 18-Jan-2021, tatu: Start by simply reusing functionality of more
        //   general handling, for now
        return WrappedIOException.construct(e);
    }
}
