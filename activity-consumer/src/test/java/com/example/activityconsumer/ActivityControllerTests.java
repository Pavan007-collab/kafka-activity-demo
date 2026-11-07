package com.example.activityconsumer;

import com.example.activityconsumer.model.ActivityEvent;
import com.example.activityconsumer.service.ActivityProducer;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import tools.jackson.databind.ObjectMapper;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ActivityController.class)
class ActivityControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ActivityProducer activityProducer;

    @Test
    void shouldPublishActivityEventSuccessfully() throws Exception {

        ActivityEvent event = new ActivityEvent();
        event.setEventType("BUTTON_CLICKED");
        event.setButtonId("test_button");
        event.setUserId("TEST_USER");
        event.setModule("DASHBOARD");

        mockMvc.perform(post("/api/activity")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andExpect(status().isOk())
                .andExpect(content().string(
                        "Activity event sent to Kafka successfully"));

        verify(activityProducer).publishEvent(
                org.mockito.ArgumentMatchers.any(ActivityEvent.class));
    }
}
