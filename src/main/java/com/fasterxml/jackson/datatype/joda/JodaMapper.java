package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JodaMapper extends ObjectMapper {
  public JodaMapper() {
    registerModule(new JodaModule());
  }
}
