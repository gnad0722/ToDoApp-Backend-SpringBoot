package dev.gnad.SpringModulith.common.api.response;

public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages
) {
}
