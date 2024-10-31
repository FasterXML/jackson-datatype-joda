package tools.jackson.datatype.joda;

import java.text.DateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import junit.framework.TestCase;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import tools.jackson.databind.cfg.CoercionAction;
import tools.jackson.databind.cfg.CoercionInputShape;
import tools.jackson.databind.json.JsonMapper;

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
    /**********************************************************************
    /* Factory methods
    /**********************************************************************
     */

    protected static JsonMapper.Builder mapperWithModuleBuilder() {
        return JsonMapper.builder()
                .addModule(new JodaModule());
    }

    protected static JsonMapper.Builder jodaMapperBuilder(DateFormat df) {
        return mapperWithModuleBuilder()
                .defaultDateFormat(df);
    }
    
    protected static JsonMapper.Builder jodaMapperBuilder(TimeZone tz) {
        return mapperWithModuleBuilder()
                .defaultTimeZone(tz);
    }

    protected static JsonMapper mapperWithModule() {
        return mapperWithModuleBuilder().build();
    }

    protected static JsonMapper mapperWithModule(DateFormat df) {
        return jodaMapperBuilder(df)
                .build();
    }

    protected static JsonMapper mapperWithModule(TimeZone tz) {
        return jodaMapperBuilder(tz)
                .build();
    }

    protected static JsonMapper mapperWithFailFromEmptyString() {
        return mapperWithModuleBuilder()
                .withCoercionConfigDefaults(cfg ->
                    cfg.setCoercion(CoercionInputShape.EmptyString, CoercionAction.Fail)
                ).build();
    }

    /*
    /**********************************************************************
    /* Additional assert methods
    /**********************************************************************
     */

    protected void assertEquals(int[] exp, int[] act) {
        assertArrayEquals(exp, act);
    }
    
    /*
    /**********************************************************************
    /* Helper methods
    /**********************************************************************
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

    public String q(String str) {
        return '"'+str+'"';
    }

    // @Deprecated
    public String quote(String str) {
        return q(str);
    }

    protected String a2q(String json) {
        return json.replace("'", "\"");
    }

    // @Deprecated
    protected String aposToQuotes(String json) {
        return a2q(json);
    }
}
