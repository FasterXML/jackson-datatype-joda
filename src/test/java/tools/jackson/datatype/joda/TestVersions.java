package tools.jackson.datatype.joda;

import java.io.*;

import org.junit.jupiter.api.Test;

import tools.jackson.core.Versioned;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Simple verification that version access works.
 */
public class TestVersions extends JodaTestBase
{
    @Test
    public void testVersions() throws Exception
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

