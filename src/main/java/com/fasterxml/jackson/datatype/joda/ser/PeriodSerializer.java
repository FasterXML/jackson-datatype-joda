package com.fasterxml.jackson.datatype.joda.ser;

import java.io.IOException;

import org.joda.time.ReadablePeriod;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.jsonFormatVisitors.*;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

/**
 * Serializes a {@link ReadablePeriod} using Joda default formatting.
 *<p>
 * TODO: allow serialization as an array of numbers, for numeric ("timestamp")
 * notation?
 */
public class PeriodSerializer // non final since 2.6.1
// alas, difficult to extend JodaDateSerializerBase
    extends JodaSerializerBase<ReadablePeriod>
    implements ContextualSerializer
{
    private static final long serialVersionUID = 1L;

    protected final JacksonJodaPeriodFormat _format;
    
    public PeriodSerializer() {
        this(FormatConfig.DEFAULT_PERIOD_FORMAT);
    }

    protected PeriodSerializer(JacksonJodaPeriodFormat format) {
        super(ReadablePeriod.class);
        _format = format;
    }

    // anything naturally "empty" to check?
    /*
    @Override
    public boolean isEmpty(ReadablePeriod value) {
        return (value.getMillis() == 0L);
    }
    */

    // Lots of work, although realistically, won't have much or any effect...
    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value ann = findFormatOverrides(prov, property, handledType());
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
    public void serialize(ReadablePeriod value, JsonGenerator gen, SerializerProvider provider) throws IOException
    {
        gen.writeString(_format.createFormatter(provider).print(value));
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
