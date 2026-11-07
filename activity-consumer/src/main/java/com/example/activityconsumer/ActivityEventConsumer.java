
package com.example.activityconsumer;

import com.example.activityconsumer.entity.ActivityEventEntity;
import com.example.activityconsumer.model.ActivityEvent;
import com.example.activityconsumer.repository.ActivityEventRepository;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import tools.jackson.databind.ObjectMapper;

@Component
public class ActivityEventConsumer {

    private final ActivityEventRepository repository;
    private final ObjectMapper objectMapper;

    public ActivityEventConsumer(
            ActivityEventRepository repository,
            ObjectMapper objectMapper) {

        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(
        topics = "user-activity-events",
        groupId = "activity-tracking-db-group"
    )
    public void consume(String message) {

        try {
            // Convert Kafka JSON into a Java object
            ActivityEvent event =
                    objectMapper.readValue(message, ActivityEvent.class);

            // Skip events without an event type
            if (event.getEventType() == null ||
                event.getEventType().isBlank()) {

                System.err.println(
                    "Skipping event without eventType: " + message
                );
                return;
            }

            // Convert Java object into a database entity
            ActivityEventEntity entity = new ActivityEventEntity();

            entity.setEventType(event.getEventType());
            entity.setButtonId(event.getButtonId());
            entity.setUserId(event.getUserId());
            entity.setModule(event.getModule());
            entity.setFileName(event.getFileName());

            // Save event in PostgreSQL
            ActivityEventEntity saved = repository.save(entity);

            System.out.println(
                "Activity event saved to PostgreSQL. ID: "
                + saved.getId()
            );

        } catch (Exception ex) {

            // Skip malformed historical Kafka messages safely
            System.err.println(
                "Skipping invalid activity message: " + ex.getMessage()
            );
        }
    }
}
