package com.example.activityconsumer;

import com.example.activityconsumer.model.ActivityEvent;
import com.example.activityconsumer.service.ActivityProducer;

import tools.jackson.core.JacksonException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final ActivityProducer activityProducer;

    public ActivityController(ActivityProducer activityProducer) {
        this.activityProducer = activityProducer;
    }

    @PostMapping("/activity")
    public ResponseEntity<String> trackActivity(
            @RequestBody ActivityEvent event)
            throws JacksonException {

        activityProducer.publishEvent(event);

        return ResponseEntity.ok(
                "Activity event sent to Kafka successfully"
        );
    }
}
