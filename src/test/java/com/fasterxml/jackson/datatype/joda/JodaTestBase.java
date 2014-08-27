package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.databind.ObjectMapper;
import junit.framework.TestCase;

import java.io.IOException;

import static org.junit.Assert.*;

public abstract class JodaTestBase extends TestCase
{
    protected static ObjectMapper jodaMapper()
    {
        return new JodaMapper();
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
