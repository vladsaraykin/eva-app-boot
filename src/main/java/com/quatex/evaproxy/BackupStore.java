package com.quatex.evaproxy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quatex.evaproxy.controller.ManageController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

@Component
public class BackupStore {

    private final Logger log = LoggerFactory.getLogger(BackupStore.class);

    public static final String PATH = "backup";
    public static final String FILE_NAME = "backup.json";

    private final Store<Object, Object> store;
    private final ObjectMapper mapper;

    public BackupStore(Store<Object, Object> store, ObjectMapper mapper) {
        this.store = store;
        this.mapper = mapper;
    }


    @Scheduled(timeUnit = TimeUnit.SECONDS, initialDelayString = "60", fixedDelayString = "${backupDelay:900}")
    public void backup() throws IOException {
        log.info("Backup started");

        Files.createDirectories(Paths.get(PATH));

        Files.write(Paths.get(PATH, FILE_NAME), mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(store.getStore()));

    }
}
