package com.fasterxml.jackson.datatype.joda;

import java.io.*;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import com.fasterxml.jackson.databind.*;

public class JDKSerializabilityTest extends JodaTestBase
{
    public void testMapperWithModule() throws Exception {
        final DateTime input = new DateTime(0L, DateTimeZone.UTC);
        ObjectMapper mapper = mapperWithModule();
        String json1 = mapper.writeValueAsString(input);

        // validate we can still use it to deserialize jackson objects
        ObjectMapper thawedMapper = serializeAndDeserialize(mapper);
        String json2 = thawedMapper.writeValueAsString(input);

        assertEquals(json1, json2);

        DateTime result = thawedMapper.readValue(json1, DateTime.class);
        assertEquals(input, result);
    }

    private ObjectMapper serializeAndDeserialize(ObjectMapper mapper) throws Exception {
        //verify serialization
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(byteArrayOutputStream);

        outputStream.writeObject(mapper);
        byte[] serializedBytes = byteArrayOutputStream.toByteArray();

        //verify deserialization
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(serializedBytes);
        ObjectInputStream inputStream = new ObjectInputStream(byteArrayInputStream);

        Object deserializedObject = inputStream.readObject();

        //validate the object
        assertTrue("Deserialized object should be an instance of ObjectMapper",
                ObjectMapper.class == deserializedObject.getClass());

        return (ObjectMapper) deserializedObject;
    }
}
