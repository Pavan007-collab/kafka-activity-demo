package com.example.activityconsumer.service;

import com.example.activityconsumer.model.ActivityEvent;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ActivityProducer {

    private static final String TOPIC = "user-activity-events";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ActivityProducer(
            KafkaTemplate<String, String> kafkaTemplate,
            ObjectMapper objectMapper) {

        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishEvent(ActivityEvent event)
            throws JacksonException {

        String message = objectMapper.writeValueAsString(event);

        kafkaTemplate.send(TOPIC, message);

        System.out.println("Activity event published: " + message);
    }
}
