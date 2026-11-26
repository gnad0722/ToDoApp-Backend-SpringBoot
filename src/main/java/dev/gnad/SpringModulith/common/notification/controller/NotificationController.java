package dev.gnad.SpringModulith.common.notification.controller;

import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import dev.gnad.SpringModulith.common.notification.domain.dto.NotificationResponseDto;
import dev.gnad.SpringModulith.common.notification.service.NotificationService;
import dev.gnad.SpringModulith.common.provider.CurrentUserProvider;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notification")
@Tag(
        name = "Notification Management",
        description = "APIs for reading in-app notifications and managing their read state"
)
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/recent")
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getRecent(
            @AuthenticationPrincipal Jwt jwt) {
        List<NotificationResponseDto> response =
                this.notificationService.getRecent(CurrentUserProvider.getUserId(jwt));

        return ResponseEntity.ok(
                ApiResponse.success(response, "Recent notifications retrieved successfully"));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> countUnread(@AuthenticationPrincipal Jwt jwt) {
        long response = this.notificationService.countUnread(CurrentUserProvider.getUserId(jwt));

        return ResponseEntity.ok(
                ApiResponse.success(response, "Unread notification count retrieved successfully"));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markRead(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        NotificationResponseDto response =
                this.notificationService.markRead(id, CurrentUserProvider.getUserId(jwt));

        return ResponseEntity.ok(
                ApiResponse.success(response, "Notification marked as read"));
    }

    @PatchMapping("/{id}/unread")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> markUnread(
            @PathVariable("id") Integer id,
            @AuthenticationPrincipal Jwt jwt) {
        NotificationResponseDto response =
                this.notificationService.markUnread(id, CurrentUserProvider.getUserId(jwt));

        return ResponseEntity.ok(
                ApiResponse.success(response, "Notification marked as unread"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Integer>> markAllRead(@AuthenticationPrincipal Jwt jwt) {
        int updated = this.notificationService.markAllRead(CurrentUserProvider.getUserId(jwt));

        return ResponseEntity.ok(
                ApiResponse.success(updated, "All notifications marked as read"));
    }
}
