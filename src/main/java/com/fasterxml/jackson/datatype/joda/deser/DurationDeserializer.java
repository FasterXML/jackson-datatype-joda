package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

/**
 * Deserializes a Duration from either an int number of millis or using the {@link Duration#Duration(Object)}
 * constructor on a JSON string. By default the only supported string format is that used by {@link
 * Duration#toString()}. (That format for a 3,248 millisecond duration is "PT3.248S".)
 */
public class DurationDeserializer
    extends JodaDeserializerBase<Duration>
{
    private static final long serialVersionUID = 1L;

    protected final JacksonJodaPeriodFormat _format;
    
    public DurationDeserializer() {
        this(FormatConfig.DEFAULT_PERIOD_FORMAT);
    }

    public DurationDeserializer(JacksonJodaPeriodFormat format) {
        super(Duration.class);
        _format = format;
    }

    @Override
    public Duration deserialize(JsonParser p, DeserializationContext ctxt) throws IOException
    {
        switch (p.getCurrentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT: // assume it's millisecond count
            return new Duration(p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _format.parsePeriod(ctxt, p.getText().trim()).toStandardDuration();
        default:
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    protected Duration _deserialize(DeserializationContext ctxt, String str)
            throws IOException
    {
        if (str.length() == 0) {
            if (ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)) {
                return null;
            }
        }
        return Duration.parse(str);
    }
}
