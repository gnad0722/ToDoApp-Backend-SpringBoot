package dev.gnad.SpringModulith.common.api.exception;

import org.springframework.http.HttpStatus;

/**
 * Base class for business errors that map to a specific HTTP status.
 * The message is returned to the client as-is, so it must be short and user-facing.
 */
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
