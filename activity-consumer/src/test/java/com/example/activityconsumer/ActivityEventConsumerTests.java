package com.example.activityconsumer;

import com.example.activityconsumer.entity.ActivityEventEntity;
import com.example.activityconsumer.repository.ActivityEventRepository;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ActivityEventConsumerTests {

    private final ActivityEventRepository repository =
            mock(ActivityEventRepository.class);

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private final ActivityEventConsumer consumer =
            new ActivityEventConsumer(repository, objectMapper);

    @Test
    void shouldConsumeAndSaveValidActivityEvent() {

        String message = """
                {
                    "eventType": "BUTTON_CLICKED",
                    "buttonId": "test_button",
                    "userId": "TEST_USER",
                    "module": "DASHBOARD",
                    "fileName": null
                }
                """;

        consumer.consume(message);

        ArgumentCaptor<ActivityEventEntity> captor =
                ArgumentCaptor.forClass(ActivityEventEntity.class);

        verify(repository).save(captor.capture());

        ActivityEventEntity savedEvent = captor.getValue();

        assertEquals("BUTTON_CLICKED", savedEvent.getEventType());
        assertEquals("test_button", savedEvent.getButtonId());
        assertEquals("TEST_USER", savedEvent.getUserId());
        assertEquals("DASHBOARD", savedEvent.getModule());
    }

    @Test
    void shouldSkipEventWithoutEventType() {

        String message = """
                {
                    "buttonId": "test_button",
                    "userId": "TEST_USER"
                }
                """;

        consumer.consume(message);

        verify(repository, never()).save(any(ActivityEventEntity.class));
    }

    @Test
    void shouldSkipMalformedJsonMessage() {

        String message = "{ invalid json }";

        consumer.consume(message);

        verify(repository, never()).save(any(ActivityEventEntity.class));
    }
}
