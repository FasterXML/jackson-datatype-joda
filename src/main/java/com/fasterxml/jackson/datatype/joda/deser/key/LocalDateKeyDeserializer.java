package com.fasterxml.jackson.datatype.joda.deser.key;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationContext;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

public class LocalDateKeyDeserializer extends JodaKeyDeserializer {
    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter parser = ISODateTimeFormat.localDateParser();

    @Override
    protected LocalDate deserialize(String key, DeserializationContext ctxt) throws IOException {
        return parser.parseLocalDate(key);
    }
}