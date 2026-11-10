package dev.gnad.SpringModulith.common.cache.configuration;

import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategy;
import org.hibernate.boot.model.source.spi.Caching;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CachingConfigurationFactory {

    private final Map<String, CachingConfiguration>  cachingConfigurationMap;

    public CachingConfigurationFactory(
            List<CachingConfiguration> configurationList
    ) {
        this.cachingConfigurationMap = configurationList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        CachingConfiguration::getCacheName,
                        Function.identity()
                ));
    }

    public RedisCacheConfiguration getConfiguration(String cacheName,
                                                    SerializationStrategy strategy,
                                                    RedisCacheConfiguration defaultConfiguration
    ){

        CachingConfiguration configuration = this.cachingConfigurationMap.get(cacheName);

        if (configuration == null) {
            throw new IllegalArgumentException(
                    "No configuration registered for cache: "
                            + cacheName
            );
        }
        return configuration.createCacheConfiguration(defaultConfiguration, strategy);
    }
}
