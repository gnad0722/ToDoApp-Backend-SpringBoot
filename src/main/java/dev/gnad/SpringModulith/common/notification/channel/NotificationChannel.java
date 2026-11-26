package dev.gnad.SpringModulith.common.notification.channel;

public interface NotificationChannel {
    void deliveryMessage(String recipient, String body, String title);
}
