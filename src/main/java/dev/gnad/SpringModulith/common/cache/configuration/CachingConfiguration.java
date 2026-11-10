package dev.gnad.SpringModulith.common.cache.configuration;

import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

public interface CachingConfiguration {

    String getCacheName();

    RedisCacheConfiguration createCacheConfiguration(
            RedisCacheConfiguration defaultConfig,
            SerializationStrategy strategy);
}
