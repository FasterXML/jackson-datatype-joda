package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import junit.framework.TestCase;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import static org.junit.Assert.*;

public abstract class JodaTestBase extends TestCase
{
    protected static MapperBuilder<?,?> jodaMapperBuilder() {
        return ObjectMapper.builder()
                .addModule(new JodaModule());
    }

    protected static MapperBuilder<?,?> jodaMapperBuilder(DateFormat df) {
        return jodaMapperBuilder()
                .defaultDateFormat(df);
    }
    
    protected static MapperBuilder<?,?> jodaMapperBuilder(TimeZone tz) {
        return jodaMapperBuilder()
                .defaultTimeZone(tz);
    }

    protected static ObjectMapper jodaMapper() {
        return jodaMapperBuilder().build();
    }

    protected static ObjectMapper jodaMapper(DateFormat df) {
        return jodaMapperBuilder(df)
                .build();
    }

    protected static ObjectMapper jodaMapper(TimeZone tz) {
        return jodaMapperBuilder(tz)
                .build();
    }

    /*
    /**********************************************************
    /* Additional assert methods
    /**********************************************************
     */

    protected void assertEquals(int[] exp, int[] act) {
        assertArrayEquals(exp, act);
    }
    
    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    protected void verifyException(Throwable e, String... matches)
    {
        String msg = e.getMessage();
        String lmsg = (msg == null) ? "" : msg.toLowerCase();
        for (String match : matches) {
            String lmatch = match.toLowerCase();
            if (lmsg.indexOf(lmatch) >= 0) {
                return;
            }
        }
        fail("Expected an exception with one of substrings ("+Arrays.asList(matches)+"): got one with message \""+msg+"\"");
    }

    public String quote(String str) {
        return '"'+str+'"';
    }

    protected String aposToQuotes(String json) {
        return json.replace("'", "\"");
    }

    protected <T> T readAndMapFromString(ObjectMapper m, String input, Class<T> cls)
        throws IOException
    {
        return (T) m.readValue("\""+input+"\"", cls);
    }
}
