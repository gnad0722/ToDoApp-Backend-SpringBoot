package dev.gnad.SpringModulith.common.api.response;

public record ApiResponse<T>(
        boolean success,
        T data,
        String message
) {
    public static <T> ApiResponse<T> success(T data){
        return new ApiResponse<>(
                true,
                data,
                null
        );
    }

    public static <T> ApiResponse<T> success(T data,String message){
        return new ApiResponse<>(
                true,
                data,
                message
        );
    }

    public static <T> ApiResponse<T> error(String message){
        return new ApiResponse<>(
                false,
                null,
                message
        );
    }

    public static <T> ApiResponse<T> error(T data, String message){
        return new ApiResponse<>(
                false,
                data,
                message
        );
    }
}
