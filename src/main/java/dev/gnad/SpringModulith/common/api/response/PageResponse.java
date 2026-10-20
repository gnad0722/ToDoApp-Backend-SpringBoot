package dev.gnad.SpringModulith.common.api.response;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(
        List<T> content,
        PageMeta meta
) {

    public static <T,R> PageResponse<R> from(
            Page<T> page,
            Function<T,R> mapper
    ){
        List<R> content = page.getContent()
                .stream()
                .map(mapper)
                .toList();
        PageMeta meta = new PageMeta(
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages()
        );
        return new PageResponse<>(
                content,
                meta);
    }
}
