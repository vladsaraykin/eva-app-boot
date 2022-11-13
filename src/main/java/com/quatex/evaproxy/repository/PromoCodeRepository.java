package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.entity.PromoEntity;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.*;

@Repository
public class PromoCodeRepository {

    private final Store<String, Map<String,PromoEntity>> store;
    public static final String PROMO = "PROMO";

    public PromoCodeRepository(Store<String, Map<String,PromoEntity>> store) {
        this.store = store;
        store.store(PROMO, new HashMap<>());
    }

    public Flux<PromoEntity> getAll() {
        return getStore().flatMapMany(db -> Flux.fromIterable(db.values()));
    }

    public Mono<PromoEntity> getByCode(String code) {
        return getStore().handle((store, synchronousSink) -> {
            final PromoEntity promoEntity = store.get(code);
            if (promoEntity != null) {
                synchronousSink.next(promoEntity);
            }
        });
    }

    public void create(PromoEntity promoEntity) {
        getStore().doOnNext(store -> store.put(promoEntity.getCode(), promoEntity)).subscribe();
    }

    public void update(PromoEntity promoEntity) {
        getStore().doOnNext(store -> store.put(promoEntity.getCode(), promoEntity)).subscribe();
    }

    private Mono<Map<String, PromoEntity>> getStore() {
        return store.getValueObj(PROMO);
    }

    public void delete(String code) {
        getStore().doOnNext(store -> store.remove(code)).subscribe();
    }
}
