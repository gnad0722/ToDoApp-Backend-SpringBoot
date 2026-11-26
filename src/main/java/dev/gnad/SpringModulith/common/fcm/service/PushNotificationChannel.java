package dev.gnad.SpringModulith.common.fcm.service;

import com.google.firebase.messaging.*;
import dev.gnad.SpringModulith.common.fcm.domain.entity.DeviceToken;
import dev.gnad.SpringModulith.common.fcm.internal.repository.DeviceTokenRepository;
import dev.gnad.SpringModulith.common.notification.channel.NotificationChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PushNotificationChannel implements NotificationChannel {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationChannel.class);

    private final FirebaseMessaging firebaseMessaging;
    private final DeviceTokenRepository tokens;

    PushNotificationChannel(FirebaseMessaging firebaseMessaging, DeviceTokenRepository tokens) {
        this.firebaseMessaging = firebaseMessaging;
        this.tokens = tokens;
    }

    /**
     * Store a token sent by the client app. A device keeps the same token when a
     * different user logs in on it, so an existing token is moved to the new user;
     * otherwise the previous user's notifications would keep arriving on this device.
     */
    public void registerToken(Integer userId, String token) {
        tokens.findByToken(token).ifPresentOrElse(
                device -> {
                    if (!device.getUserId().equals(userId)) {
                        device.setUserId(userId);
                        tokens.save(device);
                    }
                },
                () -> tokens.save(new DeviceToken(userId, token))
        );
    }

    /** Send the same notification to every device of a user. */
    public void sendToUser(Integer userId,String body , String title) {
        for (DeviceToken device : tokens.findByUserId(userId)) {
            deliveryMessage(device.getToken(), body,title);
        }
    }

    /** Send one notification to one device. */
    @Override
    public void deliveryMessage(String token, String body, String title) {
        Message message = Message.builder()
                .setToken(token)                        // WHO: the device address
                .setNotification(Notification.builder() // WHAT the OS shows automatically
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .putData("screen", "todo-list")         // extra data your app code can read (strings only)
                .build();

        try {
            // Returns an id like "projects/<project>/messages/<id>" when FCM accepts the message.
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            MessagingErrorCode code = e.getMessagingErrorCode();
            if (code == MessagingErrorCode.UNREGISTERED || code == MessagingErrorCode.INVALID_ARGUMENT) {
                // The app was uninstalled, the token expired, or it was never a valid token:
                // this address will never work, so stop sending to it.
                tokens.deleteByToken(token);
            } else {
                log.warn("FCM send failed ({}): {}", code, e.getMessage());
            }
        }
    }

}
