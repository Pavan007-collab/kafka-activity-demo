package com.example.activityconsumer;

import com.example.activityconsumer.entity.ActivityEventEntity;
import com.example.activityconsumer.repository.ActivityEventRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AnalyticsController.class)
class AnalyticsControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ActivityEventRepository repository;

    @Test
    void shouldReturnAnalyticsSummary() throws Exception {

        when(repository.count()).thenReturn(10L);

        when(repository.countEventsByType()).thenReturn(
                List.<Object[]>of(
                        new Object[]{"BUTTON_CLICKED", 6L},
                        new Object[]{"EXCEL_DOWNLOADED", 4L}
                )
        );

        mockMvc.perform(get("/api/analytics/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(10))
                .andExpect(jsonPath("$.eventsByType[0].eventType")
                        .value("BUTTON_CLICKED"))
                .andExpect(jsonPath("$.eventsByType[0].total").value(6));
    }

    @Test
    void shouldReturnUserAnalytics() throws Exception {

        when(repository.countEventsByUser()).thenReturn(
                List.<Object[]>of(
                        new Object[]{"USER104", 5L}
                )
        );

        mockMvc.perform(get("/api/analytics/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value("USER104"))
                .andExpect(jsonPath("$[0].total").value(5));
    }

    @Test
    void shouldReturnModuleAnalytics() throws Exception {

        when(repository.countEventsByModule()).thenReturn(
                List.<Object[]>of(
                        new Object[]{"DASHBOARD", 7L}
                )
        );

        mockMvc.perform(get("/api/analytics/modules"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].module").value("DASHBOARD"))
                .andExpect(jsonPath("$[0].total").value(7));
    }

    @Test
    void shouldReturnRecentEvents() throws Exception {

        when(repository.findTop50ByOrderByCreatedAtDesc())
                .thenReturn(List.of());

        mockMvc.perform(get("/api/analytics/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    void shouldReturnDownloadAnalytics() throws Exception {

        when(repository.countByEventType("EXCEL_DOWNLOADED"))
                .thenReturn(4L);

        when(repository.countByEventType("PDF_DOWNLOADED"))
                .thenReturn(3L);

        mockMvc.perform(get("/api/analytics/downloads"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.excelDownloads").value(4))
                .andExpect(jsonPath("$.pdfDownloads").value(3))
                .andExpect(jsonPath("$.totalDownloads").value(7));
    }
}
