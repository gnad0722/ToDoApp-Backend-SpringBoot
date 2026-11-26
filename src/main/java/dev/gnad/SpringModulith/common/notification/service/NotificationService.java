package dev.gnad.SpringModulith.common.notification.service;

import dev.gnad.SpringModulith.common.api.exception.NotFoundException;
import dev.gnad.SpringModulith.common.email.EmailChannel;
import dev.gnad.SpringModulith.common.fcm.service.PushNotificationChannel;
import dev.gnad.SpringModulith.common.notification.domain.MessageContent;
import dev.gnad.SpringModulith.common.notification.domain.MessageType;
import dev.gnad.SpringModulith.common.notification.domain.dto.NotificationResponseDto;
import dev.gnad.SpringModulith.common.notification.domain.entity.Notification;
import dev.gnad.SpringModulith.common.notification.internal.repository.NotificationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final EmailChannel emailChannel;
    private final PushNotificationChannel pushNotificationChannel;
    private final NotificationRepository notificationRepository;

    public NotificationService(
            EmailChannel emailChannel,
            PushNotificationChannel pushNotificationChannel,
            NotificationRepository notificationRepository
    ) {
        this.emailChannel = emailChannel;
        this.pushNotificationChannel = pushNotificationChannel;
        this.notificationRepository = notificationRepository;
    }

    public void sendEmailMessage(String recipientEmail, String htmlContent, String subject){
        this.emailChannel.deliveryMessage(recipientEmail, htmlContent,  subject);
    }

    public void sendFcmMessage(Integer userId, String body, String title){
        this.pushNotificationChannel.sendToUser(userId,body,title);
    }

    /**
     * Stores an in-app notification for the user and pushes it to their devices.
     * A push failure is logged and never breaks the operation that triggered
     * the notification; the stored message is still available in-app.
     */
    public NotificationResponseDto notify(Integer userId, MessageType type, String resourceName) {
        MessageContent content = NotficationMessageFactory.create(type, resourceName);

        Notification notification = this.notificationRepository.save(
                new Notification(userId, type, content.title(), content.body()));

        try {
            this.sendFcmMessage(userId, content.body(), content.title());
        } catch (Exception e) {
            log.warn("Push notification failed for user {} ({})", userId, type, e);
        }

        return NotificationResponseDto.from(notification);
    }

    /** The five most recent notifications of the user, newest first. */
    public List<NotificationResponseDto> getRecent(Integer userId) {
        return this.notificationRepository
                .findTop5ByUserIdOrderByCreatedAtDescIdDesc(userId)
                .stream()
                .map(NotificationResponseDto::from)
                .toList();
    }

    public long countUnread(Integer userId) {
        return this.notificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponseDto markRead(Integer notificationId, Integer userId) {
        Notification notification = this.getOwned(notificationId, userId);
        if (!notification.isRead()) {
            notification.markRead();
        }
        return NotificationResponseDto.from(notification);
    }

    @Transactional
    public NotificationResponseDto markUnread(Integer notificationId, Integer userId) {
        Notification notification = this.getOwned(notificationId, userId);
        if (notification.isRead()) {
            notification.markUnread();
        }
        return NotificationResponseDto.from(notification);
    }

    @Transactional
    public int markAllRead(Integer userId) {
        return this.notificationRepository.markAllRead(userId, LocalDateTime.now());
    }

    /** Looks the notification up by id and owner, so users cannot touch each other's messages. */
    private Notification getOwned(Integer notificationId, Integer userId) {
        return this.notificationRepository.findByIdAndUserId(notificationId, userId)
                .orElseThrow(() -> new NotFoundException("Notification not found"));
    }
}
