package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.ManageEntity;
import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.repository.ManagerRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ManageService {

    private final ManagerRepository managerRepository;


    public ManageService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public String updateLink(boolean newVersion, String value) {
        ManageEntity<String> newValue = valueFrom(newVersion, value);
        managerRepository.updateLink(newValue);
        return value;
    }

    public Mono<String> getLink(Integer version) {
        return managerRepository.getSetting().map(setting -> getValue(setting.getLink(), newVersion(setting.getVersion(), version)));
    }

    public Integer updateEnabled(boolean newVersion, Integer value) {
        ManageEntity<Integer> newValue = valueFrom(newVersion, value);
        managerRepository.updateEnabled(newValue);
        return value;
    }

    public Mono<Integer> getEnabled(Integer version) {
        return managerRepository.getSetting().map(setting -> getValue(setting.getEnabled(), newVersion(setting.getVersion(), version)));
    }

    public Mono<Integer> updateVersion(Integer version) {
        managerRepository.updateVersion(version);
        return Mono.just(version);
    }

    public String updateLinkCryptoPay(String link) {
        return managerRepository.updateLinkCryptoPay(link);
    }

    public Mono<String> getLinkCryptoPay() {
        return managerRepository.getSetting().map(SettingEntity::getLinkCryptoPay);
    }

    private <T> ManageEntity<T> valueFrom(boolean newVersion, T value) {
        return newVersion ? ManageEntity.fromNewValue(value) : ManageEntity.fromCurrentValue(value);
    }

    private <T> T getValue(ManageEntity<T> value, boolean newVersion) {
        if (value == null) {
            return null;
        }
        return newVersion ? value.getNewValue() : value.getCurrentValue();
    }

    private boolean newVersion(@NonNull Integer currentVersion, Integer version) {
        return version > currentVersion;
    }
}
