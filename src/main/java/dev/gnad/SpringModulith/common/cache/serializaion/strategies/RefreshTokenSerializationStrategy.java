package dev.gnad.SpringModulith.common.cache.serializaion.strategies;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenSerializationStrategy implements SerializationStrategy {


    @Override
    public String getCacheName() {
        return CacheName.RefreshToken;
    }

    @Override
    public RedisSerializationContext.SerializationPair<?> createSerializationPair() {
        JacksonJsonRedisSerializer<RefreshToken> serializer =
                new JacksonJsonRedisSerializer<>(
                        RefreshToken.class
                );
        return RedisSerializationContext.SerializationPair
                .fromSerializer(serializer);
    }
}
