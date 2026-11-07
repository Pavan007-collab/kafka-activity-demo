
package com.example.activityconsumer;

import com.example.activityconsumer.entity.ActivityEventEntity;
import com.example.activityconsumer.repository.ActivityEventRepository;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final ActivityEventRepository repository;

    public AnalyticsController(ActivityEventRepository repository) {
        this.repository = repository;
    }

    // 1. Overall activity summary
    @GetMapping("/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        result.put("totalEvents", repository.count());
        result.put(
            "eventsByType",
            convertCounts(repository.countEventsByType(), "eventType")
        );

        return result;
    }

    // 2. Activity count per user
    @GetMapping("/users")
    public List<Map<String, Object>> getUserAnalytics() {
        return convertCounts(repository.countEventsByUser(), "userId");
    }

    // 3. Activity count per application module
    @GetMapping("/modules")
    public List<Map<String, Object>> getModuleAnalytics() {
        return convertCounts(repository.countEventsByModule(), "module");
    }

    // 4. Latest 50 activity records
    @GetMapping("/events")
    public List<ActivityEventEntity> getRecentEvents() {
        return repository.findTop50ByOrderByCreatedAtDesc();
    }

    // 5. Excel and PDF download statistics
    @GetMapping("/downloads")
    public Map<String, Object> getDownloadAnalytics() {
        long excelDownloads =
            repository.countByEventType("EXCEL_DOWNLOADED");

        long pdfDownloads =
            repository.countByEventType("PDF_DOWNLOADED");

        Map<String, Object> result = new LinkedHashMap<>();

        result.put("excelDownloads", excelDownloads);
        result.put("pdfDownloads", pdfDownloads);
        result.put("totalDownloads", excelDownloads + pdfDownloads);

        return result;
    }

    // Converts repository results into JSON-friendly maps
    private List<Map<String, Object>> convertCounts(
            List<Object[]> rows, String key) {

        List<Map<String, Object>> result = new ArrayList<>();

        for (Object[] row : rows) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put(key, row[0]);
            item.put("total", row[1]);
            result.add(item);
        }

        return result;
    }
}
