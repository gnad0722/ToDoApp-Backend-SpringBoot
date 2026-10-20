package dev.gnad.SpringModulith.common.api.response;

import org.springframework.http.HttpStatus;

import java.time.Instant;

public record ErrorResponse(
        HttpStatus status,
        String message,
        String path,
        Instant timestamp
) {
}
