package com.example.activityconsumer;

import com.example.activityconsumer.model.ActivityEvent;
import com.example.activityconsumer.service.ActivityProducer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.kafka.core.KafkaTemplate;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class ActivityProducerTests {

    @Test
    void shouldSerializeAndPublishActivityEvent() throws Exception {

        @SuppressWarnings("unchecked")
        KafkaTemplate<String, String> kafkaTemplate =
                mock(KafkaTemplate.class);

        ObjectMapper objectMapper = new ObjectMapper();

        ActivityProducer producer =
                new ActivityProducer(kafkaTemplate, objectMapper);

        ActivityEvent event = new ActivityEvent();
        event.setEventType("BUTTON_CLICKED");
        event.setButtonId("test_button");
        event.setUserId("TEST_USER");
        event.setModule("DASHBOARD");

        producer.publishEvent(event);

        ArgumentCaptor<String> messageCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(kafkaTemplate).send(
                eq("user-activity-events"),
                messageCaptor.capture()
        );

        ActivityEvent actualEvent = objectMapper.readValue(
                messageCaptor.getValue(),
                ActivityEvent.class
        );

        assertEquals("BUTTON_CLICKED", actualEvent.getEventType());
        assertEquals("test_button", actualEvent.getButtonId());
        assertEquals("TEST_USER", actualEvent.getUserId());
        assertEquals("DASHBOARD", actualEvent.getModule());
    }
}
