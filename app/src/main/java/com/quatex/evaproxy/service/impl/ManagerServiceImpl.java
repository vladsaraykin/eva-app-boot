package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.repository.ManagerRepository;
import com.quatex.evaproxy.service.ManagerService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;

@AllArgsConstructor
@Service
public class ManagerServiceImpl implements ManagerService {

    private final ManagerRepository managerRepository;

    @Override
    public Mono<SettingEntity> save(SettingEntity settingEntity) {
        return managerRepository.save(settingEntity);
    }

    @Override
    public Flux<SettingEntity> findAll() {
        return managerRepository.findAll();
    }
}
