package tools.jackson.datatype.joda.deser;

import java.util.Map;

import org.joda.time.*;

import org.junit.jupiter.api.Test;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.datatype.joda.JodaTestBase;

import static org.junit.jupiter.api.Assertions.*;

// [datatype-joda#195]: invalid String values must fail with
// `InvalidFormatException`, not raw Joda `IllegalArgumentException`
public class InvalidStringDeserTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    @Test
    public void testLocalDate() throws Exception
    {
        _verifyInvalid(LocalDate.class, "2001-13-01",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(LocalDate.class, "garbage", "Invalid format");
    }

    @Test
    public void testLocalDateTime() throws Exception
    {
        _verifyInvalid(LocalDateTime.class, "2001-05-25T25:00:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalid(LocalDateTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testLocalTime() throws Exception
    {
        _verifyInvalid(LocalTime.class, "25:00",
                "Value 25 for hourOfDay must be in the range [0,23]");
        _verifyInvalid(LocalTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testDateTime() throws Exception
    {
        _verifyInvalid(DateTime.class, "2001-13-01",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(DateTime.class, "garbage", "Invalid format");
    }

    @Test
    public void testOtherTypes() throws Exception
    {
        _verifyInvalid(Instant.class, "2001-13-01T00:00:00Z",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(YearMonth.class, "2001-13",
                "Value 13 for monthOfYear must be in the range [1,12]");
        _verifyInvalid(MonthDay.class, "--02-30",
                "Value 30 for dayOfMonth must be in the range [1,29]");
    }

    @Test
    public void testAsProperty() throws Exception
    {
        try {
            Map<String, LocalDate> result = MAPPER.readValue(a2q("{'date':'2001-13-01'}"),
                    new TypeReference<Map<String, LocalDate>>() { });
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            assertEquals("2001-13-01", e.getValue());
            assertEquals(LocalDate.class, e.getTargetType());
            assertEquals("date", e.getPath().get(0).getPropertyName());
        }
    }

    private void _verifyInvalid(Class<?> type, String value, String expMsg)
        throws Exception
    {
        try {
            Object result = MAPPER.readValue(q(value), type);
            fail("Should not pass, got: "+result);
        } catch (InvalidFormatException e) {
            verifyException(e, "Cannot deserialize value of type `"+type.getName()+"` from String");
            verifyException(e, expMsg);
            assertEquals(value, e.getValue());
            assertEquals(type, e.getTargetType());
            assertInstanceOf(IllegalArgumentException.class, e.getCause());
        } catch (DatabindException e) {
            fail("Expected InvalidFormatException, got: "+e);
        }
    }
}
