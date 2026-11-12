package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class UserSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.USERS;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<User> serializer =
                new JacksonJsonRedisSerializer<>(
                        User.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
