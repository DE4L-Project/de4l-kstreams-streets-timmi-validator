package io.de4l.kstreams.timmi.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import lombok.extern.log4j.Log4j2;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.function.Function;

@Component
@Log4j2
public class KStreamProcessor {
    private final ObjectMapper objectMapper;
    final JsonSchemaFactory factory;
    JsonNode schemaNode;
    JsonSchema schema;

    @Autowired
    public KStreamProcessor(ObjectMapper objectMapper) throws IOException, ProcessingException {
        this.objectMapper = objectMapper;
        this.schemaNode = JsonLoader.fromResource("/timmi-schema.json");
        this.factory = JsonSchemaFactory.byDefault();
        this.schema = factory.getJsonSchema(schemaNode);
    }

    @Bean
    public Function<KStream<String, String>, KStream<String, String>[]> process() {
        Predicate<String, String> isCompliant = (k, v) -> isCompliant(v);
        Predicate<String, String> isNotCompliant = (k, v) -> !isCompliant(v);

        return input ->
                input.filter(this::filterDocuments)
                .branch(isCompliant, isNotCompliant);
    }

    public boolean filterDocuments(String key, String json) {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
            if (!jsonNode.isObject()) {
                log.info("Input data is not an object. Rejecting document.");
                return false;
            }
        } catch (JsonProcessingException e) {
            log.info("JsonProcessingException inside filter function! Rejecting document.");
            log.info(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean isCompliant(String json) {
        try {
            var jsonNode = objectMapper.readTree(json);
            return schema.validate(jsonNode).isSuccess();
        } catch (IOException | ProcessingException ioException) {
            ioException.printStackTrace();
            return false;
        }
    }
}
