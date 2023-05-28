package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.entity.VersionStructure;
import com.quatex.evaproxy.repository.ManagerRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManageService {

    private final ManagerRepository managerRepository;
    private final ConcurrentHashMap<Integer, SettingEntity> cache = new ConcurrentHashMap<>();

    public ManageService(ManagerRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public Mono<SettingEntity> getSetting() {
        SettingEntity settingEntity = cache.get(ManagerRepository.ID);
        if (settingEntity == null) {
            return managerRepository.findById(ManagerRepository.ID)
                    .doOnSuccess(e -> cache.put(e.getId(), e));
        }
        return Mono.just(settingEntity);
    }

    public Mono<SettingEntity> update(SettingEntity settingEntity) {
        settingEntity.setId(ManagerRepository.ID);
        return managerRepository.save(settingEntity).doOnSuccess(e -> cache.put(e.getId(), e));
    }

    public Mono<String> getLink(Integer version) {
        SettingEntity settingEntity = cache.get(ManagerRepository.ID);
        if (settingEntity == null) {
            return managerRepository.findById(ManagerRepository.ID)
                    .doOnSuccess(e -> cache.put(e.getId(), e))
                    .handle((setting, sink) -> {
                        final String value = getValue(setting.getLink(), newVersion(setting.getVersion(), version));
                        if (value != null) {
                            sink.next(value);
                        }
                    });
        }
        String value = getValue(settingEntity.getLink(), newVersion(settingEntity.getVersion(), version));
        return value != null ? Mono.just(value) : Mono.empty();
    }

    public Mono<Integer> getEnabled(Integer version) {
        SettingEntity settingEntity = cache.get(ManagerRepository.ID);
        if (settingEntity == null) {
            return managerRepository.findById(ManagerRepository.ID)
                    .doOnSuccess(e -> cache.put(e.getId(), e))
                    .handle((setting, sink) -> {
                        final Integer value = getValue(setting.getEnabled(), newVersion(setting.getVersion(), version));
                        if (value != null) {
                            sink.next(value);
                        }
                    });
        }
        Integer value = getValue(settingEntity.getEnabled(), newVersion(settingEntity.getVersion(), version));
        return value != null ? Mono.just(value) : Mono.empty();

    }

    public Mono<String> getLinkCryptoPay() {
        SettingEntity settingEntity = cache.get(ManagerRepository.ID);
        if (settingEntity == null) {
            return managerRepository.findById(ManagerRepository.ID)
                    .doOnSuccess(e -> cache.put(e.getId(), e))
                    .handle((setting, sink) -> {
                        if (setting.getLinkCryptoPay() != null) {
                            sink.next(setting.getLinkCryptoPay());
                        }
                    });
        }
        return settingEntity.getLinkCryptoPay() != null ? Mono.just(settingEntity.getLinkCryptoPay()) : Mono.empty();
    }

    private <T> T getValue(VersionStructure<T> value, boolean newVersion) {
        if (value == null) {
            return null;
        }
        return newVersion ? value.getNewValue() : value.getCurrentValue();
    }

    private boolean newVersion(@NonNull Integer currentVersion, Integer version) {
        return version > currentVersion;
    }
}
