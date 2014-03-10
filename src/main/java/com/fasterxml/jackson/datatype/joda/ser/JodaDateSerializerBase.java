package com.fasterxml.jackson.datatype.joda.ser;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class JodaDateSerializerBase<T> extends JodaSerializerBase<T>
// need contextualization to read per-property annotations
    implements ContextualSerializer
{
    protected final static DateTimeFormatter DEFAULT_DATEONLY_FORMAT
        = ISODateTimeFormat.date().withZoneUTC();

    protected final static DateTimeFormatter DEFAULT_TIMEONLY_FORMAT
        = ISODateTimeFormat.time().withZoneUTC();

    protected final static DateTimeFormatter DEFAULT_LOCAL_DATETIME_FORMAT
        = ISODateTimeFormat.dateTime().withZoneUTC();

    protected final JacksonJodaFormat _format;
    
    protected JodaDateSerializerBase(Class<T> type, JacksonJodaFormat format)
    {
        super(type);
        _format = format;
    }

    public abstract JodaDateSerializerBase<T> withFormat(JacksonJodaFormat format);

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value ann = prov.getAnnotationIntrospector().findFormat((Annotated)property.getMember());
            if (ann != null) {
                JacksonJodaFormat format = _format;

                Boolean useTimestamp;

                // Simple case first: serialize as numeric timestamp?
                if (ann.getShape().isNumeric()) {
                    useTimestamp = Boolean.TRUE;
                } else if (ann.getShape() == JsonFormat.Shape.STRING) {
                    useTimestamp = Boolean.FALSE;
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

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected boolean _useTimestamp(SerializerProvider provider) {
        return _format.useTimestamp(provider);
    }
}
