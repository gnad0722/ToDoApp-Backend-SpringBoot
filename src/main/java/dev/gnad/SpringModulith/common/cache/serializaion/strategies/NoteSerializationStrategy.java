package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.Note.domain.entity.Note;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class NoteSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.Note;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<Note> serializer =
                new JacksonJsonRedisSerializer<>(
                        Note.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
