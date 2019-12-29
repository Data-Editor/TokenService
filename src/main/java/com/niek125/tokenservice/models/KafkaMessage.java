package com.niek125.tokenservice.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KafkaMessage {
    private final KafkaHeader kafkaHeader;
    private final String payload;
}
