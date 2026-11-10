package dev.gnad.SpringModulith.common.cache;

import dev.gnad.SpringModulith.common.cache.configuration.CachingConfigurationFactory;
import dev.gnad.SpringModulith.common.cache.constants.CacheName;
import dev.gnad.SpringModulith.common.cache.serializaion.SerializationStrategyFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;
import java.util.Map;

@Configuration
public class CacheManager {

    private final SerializationStrategyFactory strategyFactory;

    private final CachingConfigurationFactory configurationFactory;

    private final RedisConnectionFactory redisConnectionFactory;

    public CacheManager(SerializationStrategyFactory strategyFactory,
                        CachingConfigurationFactory configurationFactory,
                        RedisConnectionFactory redisConnectionFactory) {
        this.strategyFactory = strategyFactory;
        this.configurationFactory = configurationFactory;
        this.redisConnectionFactory = redisConnectionFactory;
    }

    @Bean
    public RedisCacheManager redisCacheManager(){
      RedisCacheConfiguration defaultConfiguration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofMinutes(10));

        Map<String, RedisCacheConfiguration> configurations =
                Map.of(
                        CacheName.USERS,
                        this.configurationFactory.getConfiguration(
                                CacheName.USERS,
                                this.strategyFactory.getStrategy(CacheName.USERS),
                                defaultConfiguration
                        )
                );

        return RedisCacheManager
                .builder(redisConnectionFactory)
                .cacheDefaults(defaultConfiguration)
                .withInitialCacheConfigurations(configurations)
                .build();
    }

}
