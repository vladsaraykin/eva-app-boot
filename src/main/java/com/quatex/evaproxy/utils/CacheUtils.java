package com.quatex.evaproxy.utils;

import com.github.benmanes.caffeine.cache.AsyncCache;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

public class CacheUtils {

    public static <K, V> Mono<V> getCacheValue(AsyncCache<K, V> cache, K key, Mono<V> publisher) {
       return Mono.defer(
                () -> Mono.fromFuture(
                        cache.get(
                                key,
                                (searchKey, executor) -> {
                                    CompletableFuture<V> future = publisher.toFuture();
                                    return future.whenComplete(
                                            (r, t) -> {
                                                if (t != null) {
                                                    cache.synchronous().invalidate(key);
                                                }
                                            });
                                })));
    }
}
