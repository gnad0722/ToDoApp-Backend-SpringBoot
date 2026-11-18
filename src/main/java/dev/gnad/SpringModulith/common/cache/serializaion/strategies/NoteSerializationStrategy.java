package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.Note.domain.dto.NoteResponseDto;
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
        JacksonJsonRedisSerializer<NoteResponseDto> serializer =
                new JacksonJsonRedisSerializer<>(
                        NoteResponseDto.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
