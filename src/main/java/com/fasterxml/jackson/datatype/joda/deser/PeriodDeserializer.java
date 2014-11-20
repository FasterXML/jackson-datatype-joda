package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.*;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

public class PeriodDeserializer
    extends JodaDeserializerBase<ReadablePeriod>
{
    private static final long serialVersionUID = 1L;

    private final static PeriodFormatter DEFAULT_FORMAT = ISOPeriodFormat.standard();
    
    private final boolean _requireFullPeriod;
    
    public PeriodDeserializer() {
        this(true);
    }
    
    public PeriodDeserializer(boolean fullPeriod) {
        super(fullPeriod ? Period.class : ReadablePeriod.class);
        _requireFullPeriod = fullPeriod;
    }
   
    @Override
    public ReadablePeriod deserialize(JsonParser jp, DeserializationContext ctxt)
        throws IOException
    {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            if (str.isEmpty()) {
                return null;
            }
            return DEFAULT_FORMAT.parsePeriod(str);
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Period(jp.getLongValue());    
        }
        if (t != JsonToken.START_OBJECT && t != JsonToken.FIELD_NAME) {
            throw ctxt.wrongTokenException(jp, JsonToken.START_ARRAY,
                    "expected JSON Number, String or Object");
        }
        
        JsonNode treeNode = jp.readValueAsTree();
        String periodType = treeNode.path("fieldType").path("name").asText();
        String periodName = treeNode.path("periodType").path("name").asText();
        // any "weird" numbers we should worry about?
        int periodValue = treeNode.path(periodType).asInt();

        ReadablePeriod p;
        
        if (periodName.equals( "Seconds" )) {
            p = Seconds.seconds( periodValue );
        }
        else if (periodName.equals( "Minutes" )) {
            p = Minutes.minutes( periodValue );
        }
        else if (periodName.equals( "Hours" )) {
            p = Hours.hours( periodValue );
        }
        else if (periodName.equals( "Days" )) {
            p = Days.days( periodValue );
        }
        else if (periodName.equals( "Weeks" )) {
            p = Weeks.weeks( periodValue );
        }
        else if (periodName.equals( "Months" )) {
            p = Months.months( periodValue );
        }
        else if (periodName.equals( "Years" )) {
            p = Years.years( periodValue );
        } else {
            throw ctxt.mappingException("Don't know how to deserialize "+handledType().getName()+" using periodName '"
                +periodName+"'");
        }

        if (_requireFullPeriod && !(p instanceof Period)) {
            p = p.toPeriod();
        }
        return p;
    }
}
