package com.fasterxml.jackson.datatype.joda;

import java.io.*;

import junit.framework.TestCase;

import static org.junit.Assert.*;

import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class JodaTestBase extends TestCase
{
    /*
    /**********************************************************
    /* Initialization
    /**********************************************************
     */

    protected ObjectMapper jodaMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        return mapper;
    }
    
    /*
    /**********************************************************
    /* Additional assert methods
    /**********************************************************
     */

    protected void assertEquals(int[] exp, int[] act)
    {
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

    protected <T> T readAndMapFromString(ObjectMapper m, String input, Class<T> cls)
        throws IOException
    {
        return (T) m.readValue("\""+input+"\"", cls);
    }
}
