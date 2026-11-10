package dev.gnad.SpringModulith.common.cache.serializaion;

import org.springframework.data.redis.serializer.RedisSerializationContext;

public interface SerializationStrategy {

    String getCacheName();
    RedisSerializationContext.SerializationPair<?> createSerializationPair();

}
