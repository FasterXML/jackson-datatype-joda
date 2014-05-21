package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import com.fasterxml.jackson.core.*;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import org.joda.time.*;

public class ReadablePeriodDeserializer extends JodaDeserializerBase<ReadablePeriod>
{
    private static final long serialVersionUID = 1L;

    public ReadablePeriodDeserializer() {
        super(ReadablePeriod.class);
    }

    @Override
    public ReadablePeriod deserialize(JsonParser jsonParser, DeserializationContext ctxt)
        throws IOException
    {
        JsonNode treeNode = jsonParser.readValueAsTree();
        String periodType = treeNode.path("fieldType").path("name").asText();
        String periodName = treeNode.path("periodType").path("name").asText();
        // any "weird" numbers we should worry about?
        int periodValue = treeNode.path(periodType).asInt();
        if (periodName.equals( "Seconds" )) {
            return Seconds.seconds( periodValue );
        }
        if (periodName.equals( "Minutes" )) {
            return Minutes.minutes( periodValue );
        }
        if (periodName.equals( "Hours" )) {
            return Hours.hours( periodValue );
        }
        if (periodName.equals( "Days" )) {
            return Days.days( periodValue );
        }
        if (periodName.equals( "Weeks" )) {
            return Weeks.weeks( periodValue );
        }
        if (periodName.equals( "Months" )) {
            return Months.months( periodValue );
        }
        if (periodName.equals( "Years" )) {
            return Years.years( periodValue );
        }
        throw ctxt.mappingException("Don't know how to deserialize ReadablePeriod using periodName '"
                +periodName+"'");
    }
}
