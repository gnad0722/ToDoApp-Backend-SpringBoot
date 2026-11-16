package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.ResetPasswordToken;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class ResetPasswordTokenSerializationStrategy implements SerializationStrategy {
    @Override
    public String getCacheName() {
        return CacheName.ResetToken;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<ResetPasswordToken> serializer =
                new JacksonJsonRedisSerializer<>(
                        ResetPasswordToken.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
