package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.Project.domain.dto.ProjectResponseDto;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class ProjectSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.Project;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<ProjectResponseDto> serializer =
                new JacksonJsonRedisSerializer<>(
                        ProjectResponseDto.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
