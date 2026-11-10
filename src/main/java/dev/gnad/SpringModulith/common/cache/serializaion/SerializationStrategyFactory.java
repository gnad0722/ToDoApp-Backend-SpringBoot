package dev.gnad.SpringModulith.common.cache.serializaion;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public final class SerializationStrategyFactory {

    private final Map<String, SerializationStrategy> strategyMap;


    public SerializationStrategyFactory(List<SerializationStrategy> strategyList) {
        this.strategyMap = strategyList.stream()
                .collect(Collectors.toUnmodifiableMap(
                        SerializationStrategy::getCacheName,
                        Function.identity()
                ));
    }

    public SerializationStrategy getStrategy(String cacheName){
        SerializationStrategy strategy = strategyMap.get(cacheName);

        if (strategy == null) {
            throw new IllegalArgumentException(
                    "No serialization strategy registered for cache: "
                            + cacheName
            );
        }
        return strategy;
    }
}
