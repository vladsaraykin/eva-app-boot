package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.entity.VersionStructure;
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

    public Mono<SettingEntity> getSetting() {
        return managerRepository.findById(ManagerRepository.ID);
    }

    public Mono<SettingEntity> update(SettingEntity settingEntity) {
        settingEntity.setId(ManagerRepository.ID);
        return managerRepository.save(settingEntity);
    }

    public Mono<String> getLink(Integer version) {
        return managerRepository.findById(ManagerRepository.ID)
                .handle((setting, sink) -> {
                    final String value = getValue(setting.getLink(), newVersion(setting.getVersion(), version));
                    if (value != null) {
                        sink.next(value);
                    }
                });
    }

    public Mono<Integer> getEnabled(Integer version) {
        return managerRepository.findById(ManagerRepository.ID)
                .handle((setting, sink) -> {
                    final Integer value = getValue(setting.getEnabled(), newVersion(setting.getVersion(), version));
                    if (value != null) {
                        sink.next(value);
                    }
                });
    }

    public Mono<String> getLinkCryptoPay() {
        return managerRepository.findById(ManagerRepository.ID)
                .handle((setting, sink) -> {
                    if (setting.getLinkCryptoPay() != null) {
                        sink.next(setting.getLinkCryptoPay());
                    }
                });
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
