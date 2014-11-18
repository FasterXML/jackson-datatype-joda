package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.ReadablePeriod;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

/**
 * Serializes a {@link ReadablePeriod} using Joda default formatting.
 *<p>
 * TODO: allow serialization as an array of numbers, for numeric ("timestamp")
 * notation?
 */
public final class PeriodSerializer
// alas, difficult to extend JodaDateSerializerBase
    extends JodaSerializerBase<ReadablePeriod>
    implements ContextualSerializer
{
    protected final static PeriodFormatter DEFAULT_PERIOD_FORMAT
        = ISOPeriodFormat.standard();

    protected final static JacksonJodaPeriodFormat DEFAULT_FORMAT
        = new JacksonJodaPeriodFormat(DEFAULT_PERIOD_FORMAT);

    protected final JacksonJodaPeriodFormat _format;
    
    public PeriodSerializer() {
        this(DEFAULT_FORMAT);
    }

    protected PeriodSerializer(JacksonJodaPeriodFormat format) {
        super(ReadablePeriod.class);
        _format = format;
    }

    // Lots of work, although realistically, won't have much or any effect...
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value ann = prov.getAnnotationIntrospector().findFormat((Annotated)property.getMember());
            if (ann != null) {
                JacksonJodaPeriodFormat format = _format;

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
                if (format != _format) {
                    return new PeriodSerializer(format);
                }
            }
        }
        return this;
    }
    
    @Override
    public void serialize(ReadablePeriod value, JsonGenerator jgen, SerializerProvider provider) throws IOException
    {
        jgen.writeString(_format.createFormatter(provider).print(value));
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, java.lang.reflect.Type typeHint) {
        return createSchemaNode("string", true);
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException
    {
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);

        // Alas, nothing really matches Periods; should not call DATE or DATE_TIME
        if (v2 != null) {
//            v2.format(JsonValueFormat.DATE);
        }
    }
}
