package com.fasterxml.jackson.datatype.joda;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.cfg.MapperBuilder;
import com.fasterxml.jackson.databind.json.JsonMapper;

import junit.framework.TestCase;

import java.io.IOException;
import java.util.Arrays;

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

    protected static ObjectMapper jodaMapper() {
        return mapperWithModuleBuilder().build();
    }

    protected static MapperBuilder<?,?> mapperWithModuleBuilder() {
        return JsonMapper.builder()
                .addModule(new JodaModule());
    }

    // @since 2.12
    protected static ObjectMapper mapperWithFailFromEmptyString() {
        ObjectMapper mapper = jodaMapper();
        mapper.coercionConfigDefaults()
            .setCoercion(CoercionInputShape.EmptyString, CoercionAction.Fail);
        return mapper;
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
