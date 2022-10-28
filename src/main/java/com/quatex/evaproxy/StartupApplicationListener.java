package com.quatex.evaproxy;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.quatex.evaproxy.entity.PromoEntity;
import com.quatex.evaproxy.entity.SettingEntity;
import com.quatex.evaproxy.repository.ManagerRepository;
import com.quatex.evaproxy.repository.PromoCodeRepository;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

@Component
public class StartupApplicationListener implements ApplicationListener<ContextRefreshedEvent> {

    private final PromoCodeRepository promoCodeRepository;
    private final ManagerRepository managerRepository;
    private final ObjectMapper objectMapper;
    private final Store<Object, Object> store;

    public StartupApplicationListener(PromoCodeRepository promoCodeRepository,
                                      ManagerRepository managerRepository,
                                      ObjectMapper objectMapper, Store<Object, Object> store) {
        this.promoCodeRepository = promoCodeRepository;
        this.managerRepository = managerRepository;
        this.objectMapper = objectMapper;
        this.store = store;
    }
    
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        try {
            String content = Files.readString(Paths.get(BackupStore.PATH, BackupStore.FILE_NAME));
            Map<String, Object> backupStore = objectMapper.readValue(content, new TypeReference<>() {});
            for (Map.Entry<String, Object> entry : backupStore.entrySet()) {
                if (entry.getKey().equals(PromoCodeRepository.PROMO)) {
                    Map<String, PromoEntity> codes = objectMapper.convertValue(entry.getValue(), new TypeReference<>() {});
                    store.store(PromoCodeRepository.PROMO, codes);
                    continue;
                }
                if (entry.getKey().equals(ManagerRepository.SETTING_KEY)) {
                    SettingEntity setting = objectMapper.convertValue(entry.getValue(), SettingEntity.class);
                    store.store(ManagerRepository.SETTING_KEY, setting);
                    continue;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
