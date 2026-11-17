package dev.gnad.SpringModulith.common.schedule.internal.repository;

import dev.gnad.SpringModulith.common.schedule.domain.Enum.ScheduleStatus;
import dev.gnad.SpringModulith.common.schedule.domain.entity.Schedule;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@Table(indexes = @Index(columnList = "status, remindAt"))
public interface ScheduleRepository extends JpaRepository<Schedule, Integer> {

    @Query("""
        SELECT s
        FROM Schedule s
        JOIN FETCH s.toDoList t
        JOIN FETCH t.user u
        WHERE s.remindAt >= :startOfDay
          AND s.remindAt < :startOfNextDay
          AND s.status = :status
        """)
    List<Schedule> findDueSchedules(LocalDateTime startOfDay,
                                    LocalDateTime startOfNextDay,
                                    ScheduleStatus status);

    Optional<Schedule> findByToDoList_id(Integer id);   // renamed from findByToDoList_id
}