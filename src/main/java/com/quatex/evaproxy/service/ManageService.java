package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.SettingDto;
import com.quatex.evaproxy.entity.ManagerSetting;
import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.entity.VersionStructure;
import com.quatex.evaproxy.repository.ManagerSettingRepository;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class ManageService {

    private final ManagerSettingRepository managerRepository;

    private final ConcurrentHashMap<Integer, ManagerSetting> cache = new ConcurrentHashMap<>();

    public ManageService(ManagerSettingRepository managerRepository) {
        this.managerRepository = managerRepository;
    }

    public Flux<ManagerSetting> getSetting() {
        if (cache.isEmpty()) {
            return managerRepository
                    .findAll()
                    .doOnNext(s -> cache.put(s.getVersion(), s));
        }
        return Flux.fromIterable(cache.values());
    }

    public Mono<ManagerSetting> update(ManagerSetting settingEntity) {
        return managerRepository.save(settingEntity).doOnSuccess(e -> cache.put(e.getVersion(), e));
    }

    public Mono<SettingDto> getSettings(Integer version) {
        return managerRepository.findById(version)
                .map(entity -> SettingDto.builder()
                        .enabled(entity.getEnabled())
                        .link(entity.getLink())
                        .build());
    }

    public Mono<String> getLink(Integer version) {
        ManagerSetting settingEntity = cache.get(version);
        if (settingEntity == null) {
            return managerRepository.findById(version)
                    .doOnSuccess(e -> cache.put(version, e))
                    .map(ManagerSetting::getLink);
        }
        return Mono.just(settingEntity.getLink());
    }

    public Mono<Integer> getEnabled(Integer version) {
        ManagerSetting settingEntity = cache.get(version);
        if (settingEntity == null) {
            return managerRepository.findById(version)
                    .doOnSuccess(e -> cache.put(version, e))
                    .map(ManagerSetting::getEnabled)
                    .switchIfEmpty(Mono.error(new NotImplementedException("This version is unsupported: " + version)));
        }
        return Mono.just(settingEntity.getEnabled());

    }
}
