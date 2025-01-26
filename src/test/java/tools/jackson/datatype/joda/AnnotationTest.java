package tools.jackson.datatype.joda;

import org.junit.jupiter.api.Test;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;
import tools.jackson.datatype.joda.deser.DateTimeDeserializer;
import tools.jackson.datatype.joda.ser.DateTimeSerializer;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AnnotationTest extends DateTimeTest
{
    static class AClass{
        @JsonSerialize(using = DateTimeSerializer.class)
        @JsonDeserialize(using = DateTimeDeserializer.class)
        private DateTime createdOn = DateTime.now(DateTimeZone.UTC);

        public DateTime getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(DateTime createdOn) {
            this.createdOn = createdOn;
        }
    }

    @Test
    public void testDateTimeViaAnnotation() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        AClass initialObject = new AClass();
        String serializedObject = objectMapper.writeValueAsString(initialObject);
        AClass deserializedObject = objectMapper.readValue(serializedObject, AClass.class);
        assertEquals(deserializedObject.getCreatedOn(), initialObject.getCreatedOn());
    }

}
