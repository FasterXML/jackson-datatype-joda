package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;

import junit.framework.TestCase;

import java.io.IOException;
import java.text.DateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.joda.time.Instant;
import org.joda.time.YearMonth;
import org.joda.time.MonthDay;

import static org.junit.Assert.*;

public abstract class JodaTestBase extends TestCase
{
    protected static class FormattedInstant {
        @JsonFormat(pattern = "dd/MM/yyyy HH_mm_ss_SSS")
        public Instant value;

        public FormattedInstant(Instant v) { value = v; }
        protected FormattedInstant() { }
    }

    protected static class FormattedYearMonth {
        @JsonFormat(pattern = "yyyy/MM")
        public YearMonth value;

        public FormattedYearMonth(YearMonth v) { value = v; }
        protected FormattedYearMonth() { }
    }

    protected static class FormattedMonthDay {
        @JsonFormat(pattern = "MM:dd")
        public MonthDay value;

        public FormattedMonthDay(MonthDay v) { value = v; }
        protected FormattedMonthDay() { }
    }

    // Mix-in class for forcing polymorphic handling
    @JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.WRAPPER_ARRAY)
    protected static interface MixinForPolymorphism {
    }

    /*
    /**********************************************************
    /* Factory methods
    /**********************************************************
     */

    protected static MapperBuilder<?,?> mapperWithModuleBuilder() {
        return ObjectMapper.builder()
                .addModule(new JodaModule());
    }

    protected static MapperBuilder<?,?> jodaMapperBuilder(DateFormat df) {
        return mapperWithModuleBuilder()
                .defaultDateFormat(df);
    }
    
    protected static MapperBuilder<?,?> jodaMapperBuilder(TimeZone tz) {
        return mapperWithModuleBuilder()
                .defaultTimeZone(tz);
    }

    protected static ObjectMapper mapperWithModule() {
        return mapperWithModuleBuilder().build();
    }

    protected static ObjectMapper mapperWithModule(DateFormat df) {
        return jodaMapperBuilder(df)
                .build();
    }

    protected static ObjectMapper mapperWithModule(TimeZone tz) {
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
