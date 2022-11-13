package com.quatex.evaproxy;

import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
public class Store<K, V> {

    private final Map<K, V> store = new HashMap<>();

    public void store(K key, V value) {
        synchronized (store) {
            store.put(key, value);
        }
    }

    public Mono<V> getValueObj(K key) {
        return Mono.justOrEmpty(store.get(key));
    }

    public Mono<Map<K, Object>> getStore() {
        return Mono.just(Collections.unmodifiableMap(store));
    }
}
