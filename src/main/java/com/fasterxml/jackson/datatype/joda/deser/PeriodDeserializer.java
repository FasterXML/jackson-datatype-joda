package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

public class PeriodDeserializer
    extends JodaDeserializerBase<ReadablePeriod>
{
    private static final long serialVersionUID = 1L;

    private final JacksonJodaPeriodFormat _format = FormatConfig.DEFAULT_PERIOD_FORMAT;
    
    private final boolean _requireFullPeriod;
    
    public PeriodDeserializer() {
        this(true);
    }
    
    public PeriodDeserializer(boolean fullPeriod) {
        super(fullPeriod ? Period.class : ReadablePeriod.class);
        _requireFullPeriod = fullPeriod;
    }
   
    @Override
    public ReadablePeriod deserialize(JsonParser p, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return _fromString(p, ctxt, p.getText());
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Period(p.getLongValue());    
        }
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            return (ReadablePeriod) ctxt.handleUnexpectedToken(handledType(), t, p,
                    "expected JSON Number, String or Object");
        }
        return _fromObject(p, ctxt);
    }

    // @since 2.12
    public ReadablePeriod _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws IOException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return getNullValue(ctxt);
        }
        return _format.parsePeriod(ctxt, value);
    }

    // @since 2.12
    public ReadablePeriod _fromObject(final JsonParser p, final DeserializationContext ctxt)
        throws IOException
    {
        // 30-Sep-2020, tatu: This can be problematic with XML, if there
        //   is an element with String but also attribute(s) -- but worry
        //   if we ever hit that.
        
        JsonNode treeNode = p.readValueAsTree();
        String periodType = treeNode.path("fieldType").path("name").asText();
        String periodName = treeNode.path("periodType").path("name").asText();
        // any "weird" numbers we should worry about?
        int periodValue = treeNode.path(periodType).asInt();

        ReadablePeriod rp;
        
        if (periodName.equals( "Seconds" )) {
            rp = Seconds.seconds( periodValue );
        }
        else if (periodName.equals( "Minutes" )) {
            rp = Minutes.minutes( periodValue );
        }
        else if (periodName.equals( "Hours" )) {
            rp = Hours.hours( periodValue );
        }
        else if (periodName.equals( "Days" )) {
            rp = Days.days( periodValue );
        }
        else if (periodName.equals( "Weeks" )) {
            rp = Weeks.weeks( periodValue );
        }
        else if (periodName.equals( "Months" )) {
            rp = Months.months( periodValue );
        }
        else if (periodName.equals( "Years" )) {
            rp = Years.years( periodValue );
        } else {
            ctxt.reportInputMismatch(handledType(),
                    "Don't know how to deserialize %s using periodName '%s'",
                    handledType().getName(), periodName);
            rp = null; // never gets here
        }

        if (_requireFullPeriod && !(rp instanceof Period)) {
            rp = rp.toPeriod();
        }
        return rp;
    }
}
