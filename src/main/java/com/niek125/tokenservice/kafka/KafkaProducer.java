package com.niek125.tokenservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.niek125.tokenservice.events.DataEditorEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public void dispatch(String topic, DataEditorEvent event) throws JsonProcessingException {
        DocumentContext json = JsonPath.parse("{}");
        json = json.put("$", "class", event.getClass().getSimpleName());
        json = json.put("$", "event", mapper.writeValueAsString(event));
        kafkaTemplate.send(topic, json.jsonString());
    }
}
