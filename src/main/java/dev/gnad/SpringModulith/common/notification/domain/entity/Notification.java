package dev.gnad.SpringModulith.common.notification.domain.entity;

import dev.gnad.SpringModulith.common.domain.BaseEntity;
import dev.gnad.SpringModulith.common.notification.domain.MessageType;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * An in-app notification message shown to a user (not an email).
 * Every message pushed through FCM is also stored here so the client can
 * list it later and track whether it has been read.
 */
@Entity
@Table(
        name = "notification",
        indexes = @Index(name = "idx_notification_user_created", columnList = "userId, createdAt")
)
public class Notification extends BaseEntity {

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 1000)
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type;

    @Column(nullable = false)
    private boolean isRead = false;

    private LocalDateTime readAt;

    public Notification() {
    }

    public Notification(Integer userId, MessageType type, String title, String body) {
        this.userId = userId;
        this.type = type;
        this.title = title;
        this.body = body;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public MessageType getType() {
        return type;
    }

    public boolean isRead() {
        return isRead;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void markRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
        this.setLastModifiedAt(this.readAt);
    }

    public void markUnread() {
        this.isRead = false;
        this.readAt = null;
        this.setLastModifiedAt(LocalDateTime.now());
    }
}
