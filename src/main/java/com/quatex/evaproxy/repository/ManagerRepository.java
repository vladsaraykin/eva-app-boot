package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.entity.ManageEntity;
import com.quatex.evaproxy.entity.SettingEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Repository
public class ManagerRepository {

    public static final String SETTING_KEY = "SETTINGS";
    private final Store<String, SettingEntity> store;

    public ManagerRepository(Store<String, SettingEntity> store,
                             @Value("${defaultLink:}") String newLink) {
        this.store = store;
        SettingEntity settingEntity = new SettingEntity();
        settingEntity.setVersion(1);
        settingEntity.setEnabled(new ManageEntity<>(0, 0));
        settingEntity.setLink(new ManageEntity<>("https://www.google.com/", newLink));
        store.store(SETTING_KEY, settingEntity);
    }

    public Mono<SettingEntity> getSetting() {
        return store.getValueObj(SETTING_KEY);
    }

    public void updateLink(ManageEntity<String> newValue) {
        synchronized (store) {
            store.getValueObj(SETTING_KEY).doOnNext(setting -> setting.setLink(remapping(newValue).apply(setting.getLink()))).subscribe();
        }
    }

    public void updateEnabled(ManageEntity<Integer> newValue) {
        synchronized (store) {
            store.getValueObj(SETTING_KEY).doOnNext(setting -> remapping(newValue).apply(setting.getEnabled())).subscribe();
        }
    }

    public void updateVersion(Integer version) {
        synchronized (store) {
            store.getValueObj(SETTING_KEY).doOnNext(setting -> setting.setVersion(version)).subscribe();
        }
    }

    public String updateLinkCryptoPay(String link) {
        synchronized (store) {
            store.getValueObj(SETTING_KEY).doOnNext(setting -> setting.setLinkCryptoPay(link)).subscribe();
        }
        return link;
    }

    private <T> Function<ManageEntity<T>, ManageEntity<T>> remapping(ManageEntity<T> newValue) {
        return oldValue -> {
            if (oldValue == null) {
                return new ManageEntity<>(newValue.getCurrentValue(), newValue.getNewValue());
            }
            if (newValue.getNewValue() != null) {
                oldValue.setNewValue(newValue.getNewValue());
            }
            if (newValue.getCurrentValue() != null) {
                oldValue.setCurrentValue(newValue.getCurrentValue());
            }
            return oldValue;
        };
    }
}
