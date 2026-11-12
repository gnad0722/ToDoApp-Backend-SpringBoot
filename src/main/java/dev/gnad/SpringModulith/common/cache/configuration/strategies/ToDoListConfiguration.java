package dev.gnad.SpringModulith.common.cache.configuration.strategies;

import dev.gnad.SpringModulith.common.cache.configuration.CachingConfiguration;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.stereotype.Component;

import java.time.Duration;
@Component
public class ToDoListConfiguration implements CachingConfiguration {

    @Value("${spring.cache.ttl-reference-data}")
    private long ttl;

    @Override
    public String getCacheName() {
        return CacheName.ToDoList;
    }

    @Override
    public RedisCacheConfiguration createCacheConfiguration(
            RedisCacheConfiguration defaultConfig,
            SerializationStrategy strategy) {
        Duration ttl = Duration.ofMinutes(this.ttl);

        return defaultConfig
                .entryTtl(ttl)
                .serializeValuesWith(
                        strategy.createSerializationPair()
                );
    }
}
