package tools.jackson.datatype.joda;

import java.util.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

public class MixedListTest extends JodaTestBase
{
    private final ObjectMapper MAPPER = mapperWithModule();

    public void testMixedList() throws Exception
    {
        final Map<String, Object> map = new HashMap<String, Object>();
        DateTime dt = new DateTime(DateTimeZone.UTC);
        map.put("A", dt);
        map.put("B", 0);
    	
	    final String json = MAPPER.writeValueAsString(map);
	    // by default, timestamps should come out as longs...
	    
	    final Map<String, Object> result = MAPPER.readValue(json,
	    		new TypeReference<Map<String, Object>>() { });

	    assertEquals(2, result.size());
	    Object obB = result.get("B");
	    assertNotNull(obB);
	    if (!(obB instanceof Number)) {
	        fail("Expected 'B' to be a Number; instead of value of type "+obB.getClass().getName());
	    }
	    
	    assertEquals(Integer.valueOf(0), result.get("B"));
	    Object obA = result.get("A");
	    assertNotNull(obA);
	    if (!(obA instanceof Number)) {
	        fail("Expected 'A' to be a number; instead of value of type "+obA.getClass().getName());
	    }
    }    
}
