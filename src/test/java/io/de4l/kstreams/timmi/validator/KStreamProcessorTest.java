package io.de4l.kstreams.timmi.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@SpringBootTest(classes = KStreamProcessor.class)
@ContextConfiguration(classes = ObjectMapper.class)
class KStreamProcessorTest {

    @Autowired
    KStreamProcessor processor;

    String dummyKey = "foo";
    final String schemaVersion = "2-2-1";
    final String path = "/tests/"+ schemaVersion +"/";

    @Test
    void filterDocuments_False_IfValueIsAnInvalidObject() {
        String value1 = "{\"Bad\"= \"JSON\"}";
        String value2 = "Bad JSON";
        assertFalse(processor.filterDocuments(dummyKey, value1));
        assertFalse(processor.filterDocuments(dummyKey, value2));
    }

    @Test
    void isCompliant_True_MultipleScenarios() throws IOException {
        final JsonNode validObj = JsonLoader.fromResource(path + "valid-example.json");
        final JsonNode validObjNoLight = JsonLoader.fromResource(path + "invalid-example-no-light.json");
        final JsonNode validObjEmptyLight = JsonLoader.fromResource(path + "valid-example-empty-light.json");
        final JsonNode validObjAdditionalProperty = JsonLoader.fromResource(path + "valid-example-additional-property.json");

        assertTrue(processor.isCompliant(validObj.toString()));
        assertTrue(processor.isCompliant(validObjNoLight.toString()));
        assertTrue(processor.isCompliant(validObjEmptyLight.toString()));
        assertTrue(processor.isCompliant(validObjAdditionalProperty.toString()));
    }

    @Test
    void isCompliant_False_MultipleScenarios() throws IOException {
        final JsonNode invalidObjEmptyAcceleration = JsonLoader.fromResource(path + "invalid-example-empty-acceleration.json");
        final JsonNode invalidObjLightWrongNameType = JsonLoader.fromResource(path + "invalid-example-light-wrong-type-of-name.json");

        assertFalse(processor.isCompliant(invalidObjEmptyAcceleration.toString()));
        assertFalse(processor.isCompliant(invalidObjLightWrongNameType.toString()));
    }


}
