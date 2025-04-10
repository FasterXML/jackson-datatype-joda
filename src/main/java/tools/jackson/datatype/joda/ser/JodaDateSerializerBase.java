package tools.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import tools.jackson.core.JsonParser;

import tools.jackson.databind.*;
import tools.jackson.databind.cfg.DateTimeFeature;
import tools.jackson.databind.jsonFormatVisitors.*;
import tools.jackson.datatype.joda.cfg.JacksonJodaDateFormat;

public abstract class JodaDateSerializerBase<T> extends JodaSerializerBase<T>
{
    protected final static int FORMAT_STRING = 1;
    protected final static int FORMAT_TIMESTAMP = 2;
    protected final static int FORMAT_ARRAY = 3;

    protected final JacksonJodaDateFormat _format;

    protected final DateTimeFeature _featureForNumeric;

    /**
     * Shape to use for generic "use numeric" feature (instead of more specific
     * JsonFormat.shape).
     */
    protected final int _defaultNumericShape;

    /**
     * Marker set to non-0 if (and only if) property or type override exists.
     */
    protected final int _shapeOverride;

    protected JodaDateSerializerBase(Class<T> type, JacksonJodaDateFormat format,
            DateTimeFeature numericFeature,
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
    public boolean isEmpty(SerializationContext ctxt, T value) {
        return value == null;
    }

    @Override
    public ValueSerializer<?> createContextual(SerializationContext ctxt,
            BeanProperty property)
    {
        JsonFormat.Value ann = findFormatOverrides(ctxt, property, handledType());
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
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        switch (_serializationShape(visitor.getContext())) {
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

    protected boolean writeWithZoneId(SerializationContext ctxt) {
        return _format.shouldWriteWithZoneId(ctxt);
    }

    /**
     * @since 2.9
     */
    protected int _serializationShape(SerializationContext ctxt) {
        int shape = _shapeOverride;
        if (shape == 0) {
            if (_format.useTimestamp(ctxt, _featureForNumeric)) {
                shape = _defaultNumericShape;
            } else {
                shape = FORMAT_STRING;
            }
        }
        return shape;
    }
}
