package com.quatex.evaproxy;

import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.repository.ManagerRepository;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ManagerRepository managerRepository;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        managerRepository.findAll()
                .switchIfEmpty(managerRepository.save(
                        SettingEntity.builder()
                                .id(ManagerRepository.ID)
                                .version(1)
                                .build()
                )).subscribe();
    }
}
