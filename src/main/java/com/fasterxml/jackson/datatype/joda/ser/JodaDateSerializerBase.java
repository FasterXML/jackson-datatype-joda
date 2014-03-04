package com.fasterxml.jackson.datatype.joda.ser;

import java.util.TimeZone;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.introspect.Annotated;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

public abstract class JodaDateSerializerBase<T> extends JodaSerializerBase<T>
// need contextualization to read per-property annotations
    implements ContextualSerializer
{
    protected final static DateTimeFormatter DEFAULT_DATEONLY_FORMAT
        = ISODateTimeFormat.date();

    protected final static DateTimeFormatter DEFAULT_TIMEONLY_FORMAT
        = ISODateTimeFormat.time();

    protected final static DateTimeFormatter DEFAULT_LOCAL_DATETIME_FORMAT
        = ISODateTimeFormat.dateTime();
    
    /**
     * Flag that indicates that serialization must be done as the
     * Java timestamp, regardless of other settings.
     */
    protected final Boolean _useTimestamp;
    
    protected JodaDateSerializerBase(Class<T> type,
            Boolean useTimestamp)
    {
        super(type);
        _useTimestamp = useTimestamp;
    }

    public abstract JodaDateSerializerBase<T> withFormat(Boolean useTimestamp,
            TimeZone jdkTimezone);

    @Override
    public JsonSerializer<?> createContextual(SerializerProvider prov,
            BeanProperty property) throws JsonMappingException
    {
        if (property != null) {
            JsonFormat.Value format = prov.getAnnotationIntrospector().findFormat((Annotated)property.getMember());
            if (format != null) {
                Boolean useTimestamp;
                
                // Simple case first: serialize as numeric timestamp?
                if (format.getShape().isNumeric()) {
                    useTimestamp = Boolean.TRUE;
                } else if (format.getShape() == JsonFormat.Shape.STRING) {
                    useTimestamp = Boolean.FALSE;
                } else  {
                    useTimestamp = null;
                }
                // If not, do we have timezone?
                TimeZone tz = format.getTimeZone();

                if ((useTimestamp != _useTimestamp) || (tz != null)) {
                    return withFormat(useTimestamp, tz);
                }
                    
                    // !!! TODO
                    /*
                    DateFormat df = prov.getConfig().getDateFormat();
                    // one shortcut: with our custom format, can simplify handling a bit
                    if (df.getClass() == StdDateFormat.class) {
                        df = StdDateFormat.getISO8601Format(tz);
                    } else {
                        // otherwise need to clone, re-set timezone:
                        df = (DateFormat) df.clone();
                        df.setTimeZone(tz);
                    }
                    return withFormat(false, df);
                    */
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
        if (_useTimestamp != null) {
            return _useTimestamp.booleanValue();
        }
        return provider.isEnabled(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }
}
