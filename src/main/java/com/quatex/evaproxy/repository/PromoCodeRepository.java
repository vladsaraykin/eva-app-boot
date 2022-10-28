package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.entity.PromoEntity;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class PromoCodeRepository {

    private final Store<String, Map<String,PromoEntity>> store;
    public static final String PROMO = "PROMO";

    public PromoCodeRepository(Store<String, Map<String,PromoEntity>> store) {
        this.store = store;
        store.store(PROMO, new HashMap<>());
    }

    public List<PromoEntity> getAll() {
        return Optional.ofNullable(getStore()).map(Map::values)
                .map(List::copyOf)
                .orElse(Collections.emptyList());
    }

    public PromoEntity getByCode(String code) {
        return getStore().get(code);
    }

    public void create(PromoEntity promoEntity) {
        getStore().put(promoEntity.getCode(), promoEntity);
    }

    public void update(PromoEntity promoEntity) {
        getStore().put(promoEntity.getCode(), promoEntity);
    }

    private Map<String,PromoEntity> getStore() {
        return store.getValueObj(PROMO);
    }

    public void delete(String code) {
        getStore().remove(code);
    }
}
