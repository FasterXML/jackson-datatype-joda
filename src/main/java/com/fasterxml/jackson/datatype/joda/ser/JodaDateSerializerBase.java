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

    // // Since 2.9

    protected final static int FORMAT_STRING = 1;
    protected final static int FORMAT_TIMESTAMP = 2;
    protected final static int FORMAT_ARRAY = 3;

    protected final JacksonJodaDateFormat _format;

    protected final SerializationFeature _featureForNumeric;

    /**
     * Shape to use for generic "use numeric" feature (instead of more specific
     * JsonFormat.shape).
     *
     * @since 2.9
     */
    protected final int _defaultNumericShape;

    /**
     * Marker set to non-0 if (and only if) property or type override exists.
     *
     * @since 2.9
     */
    protected final int _shapeOverride;

    protected JodaDateSerializerBase(Class<T> type, JacksonJodaDateFormat format,
            SerializationFeature numericFeature,
            int defaultNumericShape, int shapeOverride)
    {
        super(type);
        _format = format;
        _featureForNumeric = numericFeature;
        _defaultNumericShape = defaultNumericShape;
        _shapeOverride = shapeOverride;
    }

    /**
     * @since 2.9
     */
    public abstract JodaDateSerializerBase<T> withFormat(JacksonJodaDateFormat format,
            int shapeOverride);

    @Override
    public boolean isEmpty(SerializerProvider prov, T value) {
        return value == null;
    }

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        JsonFormat.Value ann = findFormatOverrides(prov, property, handledType());
        if (ann != null) {
            int shapeOverride;
            Boolean useTimestamp;

            // Simple case first: serialize as numeric timestamp?
            final JsonFormat.Shape shape = ann.getShape();
            if (shape.isNumeric()) {
                useTimestamp = Boolean.TRUE;
                shapeOverride = FORMAT_TIMESTAMP;
            } else if (shape == JsonFormat.Shape.STRING) {
                useTimestamp = Boolean.FALSE;
                shapeOverride = FORMAT_STRING;
            } else if (shape == JsonFormat.Shape.ARRAY) {
                // 17-Nov-2014, tatu: also, arrays typically contain non-string representation
                useTimestamp = Boolean.TRUE;
                shapeOverride = FORMAT_ARRAY;
            } else  {
                useTimestamp = null;
                shapeOverride = _shapeOverride;
            }
            JacksonJodaDateFormat format = _format;
            // must not call if flag defined, to rely on defaults:
            if (useTimestamp != null) {
                format = format.withUseTimestamp(useTimestamp);
            }
            format = format.with(ann);
            if ((format != _format) || (shapeOverride != _shapeOverride)) {
                return withFormat(format, shapeOverride);
            }
        }
        return this;
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        switch (_serializationShape(provider)) {
        case FORMAT_TIMESTAMP:
            return createSchemaNode("array", true);
        case FORMAT_ARRAY:
            return createSchemaNode("number", true);
        case FORMAT_STRING:
        default:
            return createSchemaNode("string", true);
        }
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
            throws JsonMappingException
    {
        switch (_serializationShape(visitor.getProvider())) {
        case FORMAT_TIMESTAMP:
            {
                JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
                if (v2 != null) {
                    v2.numberType(JsonParser.NumberType.LONG);
                    v2.format(JsonValueFormat.UTC_MILLISEC);
                }
            }
            break;
        case FORMAT_ARRAY:
            {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 != null) {
                    v2.itemsFormat(JsonFormatTypes.INTEGER);
                }
            }
            break;
        case FORMAT_STRING:
        default:
            {
                JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
                if (v2 != null) {
                    v2.format(JsonValueFormat.DATE_TIME);
                }
            }
        }
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    /**
     * @since 2.8
     */
    protected boolean writeWithZoneId(SerializerProvider provider) {
        return _format.shouldWriteWithZoneId(provider);
    }

    /**
     * @since 2.9
     */
    protected int _serializationShape(SerializerProvider provider) {
        int shape = _shapeOverride;
        if (shape == 0) {
            if (_format.useTimestamp(provider, _featureForNumeric)) {
                shape = _defaultNumericShape;
            } else {
                shape = FORMAT_STRING;
            }
        }
        return shape;
    }
}
