
package com.example.activityconsumer.repository;

import com.example.activityconsumer.entity.ActivityEventEntity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ActivityEventRepository
        extends JpaRepository<ActivityEventEntity, Long> {

    // Count events grouped by event type
    @Query("""
        SELECT e.eventType, COUNT(e)
        FROM ActivityEventEntity e
        GROUP BY e.eventType
        """)
    List<Object[]> countEventsByType();

    // Count events grouped by user
    @Query("""
        SELECT e.userId, COUNT(e)
        FROM ActivityEventEntity e
        GROUP BY e.userId
        """)
    List<Object[]> countEventsByUser();

    // Count events grouped by application module
    @Query("""
        SELECT e.module, COUNT(e)
        FROM ActivityEventEntity e
        GROUP BY e.module
        """)
    List<Object[]> countEventsByModule();

    // Retrieve the latest 50 activity records
    List<ActivityEventEntity> findTop50ByOrderByCreatedAtDesc();

    // Count a particular event type
    long countByEventType(String eventType);
}
