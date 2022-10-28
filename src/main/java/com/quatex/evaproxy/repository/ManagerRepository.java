package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.Store;
import com.quatex.evaproxy.entity.ManageEntity;
import com.quatex.evaproxy.entity.SettingEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

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

    public SettingEntity getSetting() {
        return store.getValueObj(SETTING_KEY);
    }

    public void updateLink(ManageEntity<String> newValue) {
        synchronized (store) {
            SettingEntity setting = store.getValueObj(SETTING_KEY);
            setting.setLink(remapping(newValue).apply(setting.getLink()));
        }
    }

    public void updateEnabled(ManageEntity<Integer> newValue) {
        synchronized (store) {
            SettingEntity setting = store.getValueObj(SETTING_KEY);
            setting.setEnabled(remapping(newValue).apply(setting.getEnabled()));
        }
    }

    public void updateVersion(Integer version) {
        synchronized (store) {
            SettingEntity setting = store.getValueObj(SETTING_KEY);
            setting.setVersion(version);
        }
    }

    public String updateLinkCryptoPay(String link) {
        synchronized (store) {
            SettingEntity setting = store.getValueObj(SETTING_KEY);
            setting.setLinkCryptoPay(link);
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
