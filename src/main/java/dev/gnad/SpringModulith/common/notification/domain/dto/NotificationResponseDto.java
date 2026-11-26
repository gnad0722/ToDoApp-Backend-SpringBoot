package dev.gnad.SpringModulith.common.notification.domain.dto;

import dev.gnad.SpringModulith.common.notification.domain.MessageType;
import dev.gnad.SpringModulith.common.notification.domain.entity.Notification;

import java.time.LocalDateTime;

public record NotificationResponseDto(
        Integer id,
        MessageType type,
        String title,
        String body,
        boolean isRead,
        LocalDateTime readAt,
        LocalDateTime createdAt
) {
    public static NotificationResponseDto from(Notification notification) {
        return new NotificationResponseDto(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getBody(),
                notification.isRead(),
                notification.getReadAt(),
                notification.getCreatedAt()
        );
    }
}
