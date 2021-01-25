package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;

public class PeriodDeserializer
    extends JodaDeserializerBase<ReadablePeriod>
{
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
        throws JacksonException
    {
        JsonToken t = p.currentToken();
        if (t == JsonToken.VALUE_STRING) {
            return _fromString(p, ctxt, p.getText());
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Period(p.getLongValue());    
        }
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            return (ReadablePeriod) ctxt.handleUnexpectedToken(getValueType(ctxt), t, p,
                    "expected JSON Number, String or Object");
        }
        return _fromObject(p, ctxt);
    }

    // @since 2.12
    protected ReadablePeriod _fromString(final JsonParser p, final DeserializationContext ctxt,
            String value)
        throws JacksonException
    {
        value = value.trim();
        if (value.isEmpty()) {
            return _fromEmptyString(p, ctxt, value);
        }
        try {
            return _format.parsePeriod(ctxt, value);
        } catch (IOException e) {
            throw _wrapJodaFailure(ctxt, e);
        }
    }

    // @since 2.12
    protected ReadablePeriod _fromObject(final JsonParser p, final DeserializationContext ctxt)
        throws JacksonException
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

        // !!! 05-Jan-2021, tatu: Change to switch for 2.13 or later (Java 8)
        if (periodName.equals( "Seconds" )) {
            rp = Seconds.seconds( periodValue );
        } else if (periodName.equals( "Minutes" )) {
            rp = Minutes.minutes( periodValue );
        } else if (periodName.equals( "Hours" )) {
            rp = Hours.hours( periodValue );
        } else if (periodName.equals( "Days" )) {
            rp = Days.days( periodValue );
        } else if (periodName.equals( "Weeks" )) {
            rp = Weeks.weeks( periodValue );
        } else if (periodName.equals( "Months" )) {
            rp = Months.months( periodValue );
        } else if (periodName.equals( "Years" )) {
            rp = Years.years( periodValue );
        } else {
            final JavaType type = getValueType(ctxt);
            ctxt.reportInputMismatch(type,
                    "Don't know how to deserialize %s using periodName '%s'",
                    ClassUtil.getTypeDescription(type), periodName);
            return null; // never gets here
        }

        if (_requireFullPeriod && !(rp instanceof Period)) {
            rp = rp.toPeriod();
        }
        return rp;
    }
}
