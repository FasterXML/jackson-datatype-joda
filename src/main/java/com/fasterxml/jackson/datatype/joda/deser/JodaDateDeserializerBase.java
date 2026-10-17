package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;
import java.util.TimeZone;

import org.joda.time.DateTimeZone;
import org.joda.time.IllegalFieldValueException;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.type.LogicalType;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

/**
 * Intermediate base class used by deserializers that allow configuration
 * via <code>JsonFormat</code> annotation
 *
 * @since 2.6
 */
public abstract class JodaDateDeserializerBase<T>
    extends JodaDeserializerBase<T>
//need contextualization to read per-property annotations
    implements ContextualDeserializer
{
    private static final long serialVersionUID = 1L;

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
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt,
            BeanProperty prop) throws JsonMappingException
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
     * @since 2.23
     */
    protected int _nextArrayInt(JsonParser p, DeserializationContext ctxt,
            String endOfArrayMsg)
        throws IOException
    {
        int value = p.nextIntValue(-1); // fast speculative case
        if (value == -1) { // either -1, or not an integral number; slow path
            if (p.currentToken() == JsonToken.END_ARRAY) {
                throw ctxt.wrongTokenException(p, getValueType(ctxt), JsonToken.VALUE_NUMBER_INT,
                        endOfArrayMsg);
            }
            // Report `null` (or empty String coerced to `null`) element with a more
            // accurate message than default handling (which cannot be overridden in 2.x)
            if (ctxt.isEnabled(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)) {
                if (p.hasToken(JsonToken.VALUE_NULL)) {
                    _reportNullArrayElement(p, ctxt, "`null`");
                }
                if (p.hasToken(JsonToken.VALUE_STRING)) {
                    final String text = p.getText().trim();
                    if (text.isEmpty() && _checkFromStringCoercion(ctxt, text,
                            LogicalType.Integer, Integer.TYPE) == CoercionAction.AsNull) {
                        _reportNullArrayElement(p, ctxt, "empty String (\"\")");
                    }
                }
            }
            value = _parseIntPrimitive(p, ctxt);
        }
        return value;
    }

    private void _reportNullArrayElement(JsonParser p, DeserializationContext ctxt,
            String inputDesc)
        throws IOException
    {
        ctxt.reportInputMismatch(this,
"Cannot coerce %s to `int` for element #%d of %s Array value (disable `DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES` to allow)",
                inputDesc, p.getParsingContext().getCurrentIndex() + 1,
                ClassUtil.nameOf(handledType()));
    }

    /**
     * Helper method for reporting a JSON Array value with out-of-range
     * component(s) (like month 13) as {@link JsonMappingException}.
     *
     * @since 2.23
     */
    protected JsonMappingException _invalidArrayValue(DeserializationContext ctxt,
            IllegalFieldValueException e)
    {
        JsonMappingException dbe = ctxt.weirdNumberException(e.getIllegalNumberValue(),
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
        if (cached == null || cached.jdkZone != jdkZone) {
            cached = new ContextZone(jdkZone, DateTimeZone.forTimeZone(jdkZone));
            _contextZone = cached;
        }
        return cached.jodaZone;
    }

    private final static class ContextZone {
        final TimeZone jdkZone;
        final DateTimeZone jodaZone;

        ContextZone(TimeZone jdkZone, DateTimeZone jodaZone) {
            this.jdkZone = jdkZone;
            this.jodaZone = jodaZone;
        }
    }
}
