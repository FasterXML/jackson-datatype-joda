package tools.jackson.datatype.joda.deser;

import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonParser;
import tools.jackson.core.JsonToken;
import tools.jackson.databind.*;
import tools.jackson.databind.util.ClassUtil;
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
     * Helper method for reading the next required int element of a JSON Array
     * value: reports premature end of the Array using given message, and
     * otherwise handles the element (including coercion of Strings, floating-point
     * numbers and {@code null}s) using standard {@code int} handling.
     *
     * @since 3.3
     */
    protected int _nextArrayInt(JsonParser p, DeserializationContext ctxt,
            String endOfArrayMsg)
        throws JacksonException
    {
        int value = p.nextIntValue(-1); // fast speculative case
        if (value == -1) { // either -1, or not an integral number; slow path
            if (p.currentToken() == JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.VALUE_NUMBER_INT,
                        endOfArrayMsg);
            }
            value = _parseIntPrimitive(p, ctxt);
        }
        return value;
    }

    /**
     * Overridden to report {@code null} (or empty String coerced to {@code null})
     * JSON Array element (the only case where {@code int} values are read by
     * date deserializers) with a more accurate message.
     */
    @Override
    protected Object _verifyNullForPrimitive(DeserializationContext ctxt,
            JsonParser p, Object defaultValue)
        throws DatabindException
    {
        if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
            final String inputDesc = p.hasToken(JsonToken.VALUE_NULL)
                    ? "`null`" : "empty String (\"\")";
            return ctxt.handleNullForPrimitives(handledType(), p, this,
"Cannot coerce %s to `int` for element #%d of %s Array value (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)",
                    inputDesc, p.streamReadContext().getCurrentIndex() + 1,
                    ClassUtil.nameOf(handledType()));
        }
        return defaultValue;
    }

    /**
     * Helper method for reporting a JSON Array value with out-of-range
     * component(s) (like month 13) as {@link DatabindException}.
     *
     * @since 3.3
     */
    protected DatabindException _invalidArrayValue(DeserializationContext ctxt,
            IllegalFieldValueException e)
    {
        DatabindException dbe = ctxt.weirdNumberException(e.getIllegalNumberValue(),
                handledType(), e.getMessage());
        dbe.initCause(e);
        return dbe;
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
