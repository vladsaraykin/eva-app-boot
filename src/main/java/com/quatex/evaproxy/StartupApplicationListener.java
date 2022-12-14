package com.quatex.evaproxy;

import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.repository.ManagerRepository;
import com.quatex.evaproxy.scheduled.EconomicCalendarScheduled;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final ManagerRepository managerRepository;
    private final EconomicCalendarScheduled economicCalendarScheduled;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        managerRepository.findAll()
                .switchIfEmpty(managerRepository.save(
                        SettingEntity.builder()
                                .id(ManagerRepository.ID)
                                .version(1)
                                .build()
                )).subscribe();

        economicCalendarScheduled.synchronizeEconomicEvents();
    }
}
