package com.fasterxml.jackson.datatype.joda;

import java.io.*;

import com.fasterxml.jackson.core.Versioned;

/**
 * Simple verification that version access works.
 */
public class TestVersions extends JodaTestBase
{
    public void testVersions() throws IOException
    {
        JodaModule m = new JodaModule();
        assertVersion(m);
    }

    /*
    /**********************************************************
    /* Helper methods
    /**********************************************************
     */

    private void assertVersion(Versioned v)
    {
        assertEquals(PackageVersion.VERSION, v.version());
    }
}

