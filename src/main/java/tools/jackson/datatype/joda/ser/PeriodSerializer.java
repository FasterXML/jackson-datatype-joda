package tools.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;

import tools.jackson.core.JacksonException;
import tools.jackson.core.JsonGenerator;

import tools.jackson.databind.*;
import tools.jackson.databind.jsonFormatVisitors.*;
import tools.jackson.datatype.joda.cfg.FormatConfig;
import tools.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

import org.joda.time.ReadablePeriod;

/**
 * Serializes a {@link ReadablePeriod} using Joda default formatting.
 *<p>
 * TODO: allow serialization as an array of numbers, for numeric ("timestamp")
 * notation?
 */
public class PeriodSerializer
// alas, difficult to extend JodaDateSerializerBase
    extends JodaSerializerBase<ReadablePeriod>
{
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
    public ValueSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property)
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
                format = format.withFormat(ann.getPattern());
                format = format.withLocale(ann.getLocale());
                if (format != _format) {
                    return new PeriodSerializer(format);
                }
            }
        }
        return this;
    }

    @Override
    public void serialize(ReadablePeriod value, JsonGenerator g, SerializerProvider provider)
        throws JacksonException
    {
        g.writeString(_format.createFormatter(provider).print(value));
    }

    @Override
    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint)
    {
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);

        // Alas, nothing really matches Periods; should not call DATE or DATE_TIME
        if (v2 != null) {
//            v2.format(JsonValueFormat.DATE);
        }
    }
}
