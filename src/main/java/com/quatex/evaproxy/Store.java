package com.quatex.evaproxy;

import org.springframework.stereotype.Repository;

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

    public V getValueObj(K key) {
        return store.get(key);
    }

    public Map<K, Object> getStore() {
        return Collections.unmodifiableMap(store);
    }
}
