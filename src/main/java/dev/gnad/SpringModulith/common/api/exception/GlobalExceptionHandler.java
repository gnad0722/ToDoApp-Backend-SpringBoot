package dev.gnad.SpringModulith.common.api.exception;


import dev.gnad.SpringModulith.common.api.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.core.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Converts every exception raised in controllers and services into the
 * {@code { success: false, data, message }} envelope with the correct HTTP status.
 *
 * Errors raised inside the Spring Security filter chain (before a controller is
 * reached) are handled by RestAuthenticationEntryPoint and RestAccessDeniedHandler.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ---------- Business errors (400 / 401 / 403 / 404 / 409) ----------

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException ex) {
        return build(ex.getStatus(), ex.getMessage());
    }

    // ---------- 400: request body validation ----------

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new LinkedHashMap<>();
        String firstMessage = null;

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            String message = fieldError.getDefaultMessage() != null
                    ? fieldError.getDefaultMessage()
                    : ErrorMessages.INVALID_FIELD_VALUE;
            errors.putIfAbsent(fieldError.getField(), message);
            if (firstMessage == null) {
                firstMessage = message;
            }
        }

        for (ObjectError globalError : ex.getBindingResult().getGlobalErrors()) {
            String message = globalError.getDefaultMessage() != null
                    ? globalError.getDefaultMessage()
                    : ErrorMessages.INVALID_FIELD_VALUE;
            errors.putIfAbsent(globalError.getObjectName(), message);
            if (firstMessage == null) {
                firstMessage = message;
            }
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(
                        errors,
                        firstMessage != null ? firstMessage : ErrorMessages.INVALID_REQUEST
                ));
    }

    // ---------- 400: malformed JSON / wrong data type ----------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotReadable(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, ErrorMessages.INVALID_REQUEST_FORMAT);
    }

    // ---------- 400: invalid query / path parameters ----------

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleMissingParameter(MissingServletRequestParameterException ex) {
        return build(HttpStatus.BAD_REQUEST, "Missing required parameter: " + ex.getParameterName());
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidSort(PropertyReferenceException ex) {
        return build(HttpStatus.BAD_REQUEST, "Invalid sort field: " + ex.getPropertyName());
    }

    // ---------- 401: wrong credentials at login ----------

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthentication(AuthenticationException ex) {
        return build(HttpStatus.UNAUTHORIZED, ErrorMessages.BAD_CREDENTIALS);
    }

    // ---------- 403: access denied inside a controller / method security ----------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        return build(HttpStatus.FORBIDDEN, ErrorMessages.FORBIDDEN);
    }

    // ---------- 409: unique constraint violated in the database ----------

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrity(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, ErrorMessages.DATA_CONFLICT);
    }

    // ---------- Fallback ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnexpected(Exception ex) {

        // Exceptions thrown inside repositories may arrive wrapped
        // (e.g. InvalidDataAccessApiUsageException), so look at the cause chain.
        Throwable cause = ex.getCause();
        while (cause != null && cause != cause.getCause()) {
            if (cause instanceof ApiException apiException) {
                return handleApiException(apiException);
            }
            if (cause instanceof PropertyReferenceException propertyException) {
                return handleInvalidSort(propertyException);
            }
            cause = cause.getCause();
        }

        // Standard Spring MVC exceptions (404 no handler, 405, 415, ...) carry their own status.
        if (ex instanceof ErrorResponse errorResponse) {
            HttpStatusCode status = errorResponse.getStatusCode();
            if (status.is4xxClientError()) {
                return build(status, defaultMessageFor(status));
            }
        }

        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, ErrorMessages.INTERNAL_ERROR);
    }

    // ---------- Helpers ----------

    private ResponseEntity<ApiResponse<Void>> build(HttpStatusCode status, String message) {
        return ResponseEntity
                .status(status)
                .body(ApiResponse.error(message));
    }

    private String defaultMessageFor(HttpStatusCode status) {
        return switch (status.value()) {
            case 401 -> ErrorMessages.UNAUTHORIZED;
            case 403 -> ErrorMessages.FORBIDDEN;
            case 404 -> ErrorMessages.RESOURCE_NOT_FOUND;
            case 405 -> ErrorMessages.METHOD_NOT_ALLOWED;
            case 415 -> ErrorMessages.UNSUPPORTED_MEDIA_TYPE;
            default -> ErrorMessages.INVALID_REQUEST;
        };
    }
}
