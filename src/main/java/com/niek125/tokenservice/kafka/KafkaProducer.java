package com.niek125.tokenservice.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niek125.tokenservice.models.KafkaMessage;
import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;

@AllArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper;

    public void dispatch(KafkaMessage message) {
        try {
            kafkaTemplate.send(message.getKafkaHeader().getPayload(), mapper.writeValueAsString(message.getKafkaHeader()) + "\n" + message.getPayload());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }
}
