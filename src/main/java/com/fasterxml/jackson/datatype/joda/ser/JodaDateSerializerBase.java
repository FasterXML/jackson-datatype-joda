package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public abstract class JodaDateSerializerBase<T> extends JodaSerializerBase<T>
// need contextualization to read per-property annotations
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    protected final JacksonJodaDateFormat _format;

    /**
     * Marker flag that indicates that timestamp-style format uses JSON arrays,
     * not integral numbers.
     * 
     * @since 2.5
     */
    protected final boolean _usesArrays;

    protected final SerializationFeature _featureForNumeric;

    protected JodaDateSerializerBase(Class<T> type, JacksonJodaDateFormat format,
            boolean usesArrays, SerializationFeature numericFeature)
    {
        super(type);
        _format = format;
        _usesArrays = usesArrays;
        _featureForNumeric = numericFeature;
    }

    public abstract JodaDateSerializerBase<T> withFormat(JacksonJodaDateFormat format);

    @Override
    public boolean isEmpty(SerializerProvider prov, T value) {
        return value == null;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value ann = findFormatOverrides(prov, property, handledType());
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
                if (format != _format) {
                    return withFormat(format);
                }
            }
        }
        return this;
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        if (_useTimestamp(provider)) {
            return createSchemaNode(_usesArrays ? "array" : "number", true);
        }
        return createSchemaNode("string", true);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        _acceptJsonFormatVisitor(visitor, typeHint, _useTimestamp(visitor.getProvider()));
    }

    protected void _acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint,
            boolean asNumber) throws JsonMappingException
    {
        if (asNumber) {
            if (_usesArrays) {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            } else {
                JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
                if (v2 != null) {
                    v2.numberType(JsonParser.NumberType.LONG);
                    v2.format(JsonValueFormat.UTC_MILLISEC);
                }
            }
        } else {
            JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
            if (v2 != null) {
                v2.format(JsonValueFormat.DATE_TIME);
            }
        }
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected boolean _useTimestamp(SerializerProvider provider) {
        return _format.useTimestamp(provider, _featureForNumeric);
    }
}
