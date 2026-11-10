package dev.gnad.SpringModulith.common.cache.configuration;

import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.time.Duration;

public class UserCachingConfiguration implements CachingConfiguration {


    @Override
    public String getCacheName() {
        return CacheName.USERS;
    }

    @Override
    public RedisCacheConfiguration createCacheConfiguration(
            RedisCacheConfiguration defaultConfig,
            SerializationStrategy strategy) {

        Duration ttl = Duration.ofMinutes(30);
        return defaultConfig
                .entryTtl(ttl)
                .serializeValuesWith(
                        strategy.createSerializationPair()
                );
    }


}
