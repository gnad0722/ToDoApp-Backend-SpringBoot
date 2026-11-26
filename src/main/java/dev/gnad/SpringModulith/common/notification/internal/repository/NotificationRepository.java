package dev.gnad.SpringModulith.common.notification.internal.repository;

import dev.gnad.SpringModulith.common.notification.domain.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    /** The five most recent notifications of a user, newest first. */
    List<Notification> findTop5ByUserIdOrderByCreatedAtDescIdDesc(Integer userId);

    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);

    long countByUserIdAndIsReadFalse(Integer userId);

    @Modifying
    @Query("""
        UPDATE Notification n
        SET n.isRead = true, n.readAt = :now, n.lastModifiedAt = :now
        WHERE n.userId = :userId AND n.isRead = false
        """)
    int markAllRead(@Param("userId") Integer userId, @Param("now") LocalDateTime now);
}
