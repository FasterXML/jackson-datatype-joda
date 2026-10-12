package tools.jackson.datatype.joda.deser;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.databind.*;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Intermediate base class used by deserializers that allow configuration
 * via <code>JsonFormat</code> annotation
 */
public abstract class JodaDateDeserializerBase<T>
    extends JodaDeserializerBase<T>
{
    protected final JacksonJodaDateFormat _format;

    /**
     * Cached conversion of the last seen context {@link TimeZone} into
     * {@link DateTimeZone}, to avoid re-resolving it for every value.
     */
    private transient volatile ContextZone _contextZone;

    protected JodaDateDeserializerBase(Class<?> type, JacksonJodaDateFormat format)
    {
        super(type);
        _format = format;
    }

    public abstract JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat format);

    @Override
    public ValueDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty prop)
    {
        JsonFormat.Value ann = findFormatOverrides(ctxt, prop, handledType());
        if (ann != null) {
            JacksonJodaDateFormat format = _format;
            Boolean useTimestamp;

            // Simple case first: serialize as numeric timestamp?
            if (ann.getShape().isNumeric()) {
                useTimestamp = Boolean.TRUE;
            } else if (ann.getShape() == JsonFormat.Shape.STRING) {
                useTimestamp = Boolean.FALSE;
            } else if (ann.getShape() == JsonFormat.Shape.ARRAY) {
                // 17-Nov-2014, tatu: also, arrays typically contain non-string representation
                useTimestamp = Boolean.TRUE;
            } else  {
                useTimestamp = null;
            }
            // must not call if flag defined, to rely on defaults:
            if (useTimestamp != null) {
                format = format.withUseTimestamp(useTimestamp);
            }
            // for others, safe to call, null/empty just ignored
            format = format.with(ann);
            if (format != _format) {
                return withFormat(format);
            }
        }
        return this;
    }

    /**
     * Helper method for resolving the time zone to use for converting a
     * timestamp: explicit format time zone if one is specified; context
     * time zone if adjustment to it is enabled; UTC otherwise.
     */
    protected DateTimeZone _timestampZone(DeserializationContext ctxt) {
        if (_format.isTimezoneExplicit()) {
            return _format.getTimeZone();
        }
        if (_format.shouldAdjustToContextTimeZone(ctxt)) {
            return _contextTimeZone(ctxt);
        }
        // No adjustment allowed: timestamp is resolved as-is (UTC)
        return DateTimeZone.UTC;
    }

    /**
     * Helper method for accessing the context time zone as {@link DateTimeZone}.
     */
    protected DateTimeZone _contextTimeZone(DeserializationContext ctxt) {
        final TimeZone jdkZone = ctxt.getTimeZone();
        ContextZone cached = _contextZone;
        if (cached == null || cached.jdkZone() != jdkZone) {
            cached = new ContextZone(jdkZone, DateTimeZone.forTimeZone(jdkZone));
            _contextZone = cached;
        }
        return cached.jodaZone();
    }

    private record ContextZone(TimeZone jdkZone, DateTimeZone jodaZone) { }
}
