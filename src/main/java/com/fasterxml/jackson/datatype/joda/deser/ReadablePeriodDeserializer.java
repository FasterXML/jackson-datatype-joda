package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Minutes;
import org.joda.time.Months;
import org.joda.time.ReadablePeriod;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

import java.io.IOException;

public class ReadablePeriodDeserializer extends JodaDeserializerBase<ReadablePeriod>
{
	public ReadablePeriodDeserializer()
	{
		super( ReadablePeriod.class );
	}

	@Override
	public ReadablePeriod deserialize( JsonParser jsonParser, DeserializationContext deserializationContext ) throws IOException, JsonProcessingException
	{
		TreeNode treeNode = jsonParser.getCodec().readTree( jsonParser );
		String periodType = ((TextNode)treeNode.get( "fieldType" ).get( "name" )).textValue();
		String periodName = ((TextNode)treeNode.get( "periodType" ).get( "name" )).textValue();
		int periodValue = ((IntNode)treeNode.get( periodType )).intValue();
		if (periodName.equals( "Seconds" ))
		{
			return Seconds.seconds( periodValue );
		}
		else if (periodName.equals( "Minutes" ))
		{
			return Minutes.minutes( periodValue );
		}
		else if (periodName.equals( "Hours" ))
		{
			return Hours.hours( periodValue );
		}
		else if (periodName.equals( "Days" ))
		{
			return Days.days( periodValue );
		}
		else if (periodName.equals( "Weeks" ))
		{
			return Weeks.weeks( periodValue );
		}
		else if (periodName.equals( "Months" ))
		{
			return Months.months( periodValue );
		}
		else if (periodName.equals( "Years" ))
		{
			return Years.years( periodValue );
		}
		else
		{
			return null;
		}
	}
}
