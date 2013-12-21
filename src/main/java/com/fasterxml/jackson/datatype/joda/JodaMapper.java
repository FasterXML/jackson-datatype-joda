package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JodaMapper extends ObjectMapper
{
    private static final long serialVersionUID = 1L;

    public JodaMapper() {
        registerModule(new JodaModule());
    }
}
