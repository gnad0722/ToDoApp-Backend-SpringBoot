package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.Task.domain.dto.TaskDto;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class TaskSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.Task;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<TaskDto> serializer =
                new JacksonJsonRedisSerializer<>(
                        TaskDto.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
