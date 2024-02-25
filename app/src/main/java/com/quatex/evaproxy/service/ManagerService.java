package com.quatex.evaproxy.service;

import com.quatex.evaproxy.entity.SettingEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ManagerService {


    Mono<SettingEntity> save(SettingEntity settingEntity);

    Flux<SettingEntity> findAll();
}
