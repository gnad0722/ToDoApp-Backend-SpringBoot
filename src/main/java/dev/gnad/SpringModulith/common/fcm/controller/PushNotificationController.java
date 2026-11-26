package dev.gnad.SpringModulith.common.fcm.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.fcm.domain.dto.TokenRequest;
import dev.gnad.SpringModulith.common.fcm.service.PushNotificationChannel;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class PushNotificationController {

    private final PushNotificationChannel pushNotificationChannel;

    public PushNotificationController(PushNotificationChannel pushNotificationChannel) {
        this.pushNotificationChannel = pushNotificationChannel;
    }

    @PostMapping("/push-token")
    public ResponseEntity<ApiResponse<Void>> registerToken(@RequestBody TokenRequest request, @AuthenticationPrincipal Jwt jwt) {

        this.pushNotificationChannel.registerToken(
                CurrentUserProvider.getUserId(jwt),
                request.token());

        return ResponseEntity.ok(
                ApiResponse.success(
                        null,
                        "Register Token Device Successfully!"
                )
        );
    }
}
