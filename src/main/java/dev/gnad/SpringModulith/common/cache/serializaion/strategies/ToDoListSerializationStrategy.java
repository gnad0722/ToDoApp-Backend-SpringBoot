package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.ToDoList.domain.dto.ToDoResponseDto;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class ToDoListSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.ToDoList;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<ToDoResponseDto> serializer =
                new JacksonJsonRedisSerializer<>(
                        ToDoResponseDto.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
