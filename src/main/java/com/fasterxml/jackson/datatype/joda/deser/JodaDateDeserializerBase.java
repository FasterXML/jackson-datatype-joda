package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;
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
            format = format.withFormat(ann.getPattern().trim());
            format = format.withLocale(ann.getLocale());
            format = format.withTimeZone(ann.getTimeZone());
            format = format.withAdjustToContextTZOverride(
                    ann.getFeature(JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE));
            if (format != _format) {
                return withFormat(format);
            }
        }
        return this;
    }
}
