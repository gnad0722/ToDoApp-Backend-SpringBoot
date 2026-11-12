package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.ToDoList.domain.entity.ToDoList;
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
        JacksonJsonRedisSerializer<ToDoList> serializer =
                new JacksonJsonRedisSerializer<>(
                        ToDoList.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
