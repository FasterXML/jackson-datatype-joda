package com.fasterxml.jackson.datatype.joda.deser;

import java.io.IOException;

import org.joda.time.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;

public class DateTimeKeyDeserializer extends KeyDeserializer {

  @Override
  public Object deserializeKey(final String key, final DeserializationContext ctxt) throws IOException,
      JsonProcessingException {
    if (key.length() == 0) { // [JACKSON-360]
        return null;
    }
    return new DateTime(key, DateTimeZone.forTimeZone(ctxt.getTimeZone()));
  }

}
