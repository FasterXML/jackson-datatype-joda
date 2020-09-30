package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.Duration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.core.StreamReadCapability;
import com.fasterxml.jackson.core.io.NumberInput;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
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
        switch (p.currentTokenId()) {
        case JsonTokenId.ID_NUMBER_INT: // assume it's millisecond count
            return _fromTimestamp(ctxt, p.getLongValue());
        case JsonTokenId.ID_STRING:
            return _fromString(p, ctxt, p.getText());
        case JsonTokenId.ID_START_OBJECT:
            // 30-Sep-2020, tatu: New! "Scalar from Object" (mostly for XML)
            return _fromString(p, ctxt,
                    ctxt.extractScalarFromObject(p, this, handledType()));
        default:
        }
        return _handleNotNumberOrString(p, ctxt);
    }

    protected Duration _fromString(JsonParser p, DeserializationContext ctxt,
            String value) throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return getNullValue(ctxt);
        }
        // 14-Jul-2020: [datatype-joda#117] Should allow use of "Timestamp as String" for
        //     some textual formats
        if (ctxt.isEnabled(StreamReadCapability.UNTYPED_SCALARS)
                && _isValidTimestampString(value)) {
            return _fromTimestamp(ctxt, NumberInput.parseLong(value));
        }
        
        return _format.parsePeriod(ctxt, value).toStandardDuration();
    }

    // @since 2.12
    protected Duration _fromTimestamp(DeserializationContext ctxt, long ts) {
        return new Duration(ts);
    }
}
