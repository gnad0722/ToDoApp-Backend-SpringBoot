package dev.gnad.SpringModulith.common.api.exception;

/**
 * User-facing error messages shared by the exception handler and the security handlers.
 */
public final class ErrorMessages {

    private ErrorMessages() {}

    public static final String INVALID_REQUEST_FORMAT = "Invalid request format";
    public static final String INVALID_REQUEST = "Invalid request";
    public static final String INVALID_FIELD_VALUE = "Invalid value";
    public static final String BAD_CREDENTIALS = "Email or password is incorrect";
    public static final String UNAUTHORIZED = "Unauthorized";
    public static final String FORBIDDEN = "You do not have permission to access this resource";
    public static final String RESOURCE_NOT_FOUND = "Resource not found";
    public static final String METHOD_NOT_ALLOWED = "Method not allowed";
    public static final String UNSUPPORTED_MEDIA_TYPE = "Unsupported media type";
    public static final String DATA_CONFLICT = "This resource conflicts with existing data";
    public static final String INTERNAL_ERROR = "Something went wrong, please try again later";
}
