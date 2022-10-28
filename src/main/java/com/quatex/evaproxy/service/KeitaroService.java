package com.quatex.evaproxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class KeitaroService {

    Logger log = LoggerFactory.getLogger(KeitaroService.class);

    private final RestTemplate restTemplate;
    private final String keitaroUrl;

    public KeitaroService(RestTemplate restTemplate,
                          @Value("${keitaroUrl}") String keitaroUrl) {
        log.info("KeitaroUrl: {}", keitaroUrl);
        this.restTemplate = restTemplate;
        this.keitaroUrl = keitaroUrl;
    }

    public int getStatus(String ip, String name, String systemVersion, String model) {
        ResponseEntity<String> forEntity = restTemplate.getForEntity(keitaroUrl + "/option.php?" +
                "ip={ip}" +
                "&name={name}" +
                "&systemName={name}" +
                "&systemVersion={systemVersion}" +
                "&model={model}", String.class, ip, name, name, systemVersion, model);
        if (forEntity.getStatusCode() != HttpStatus.OK) {
            log.error("Binomo response {}", forEntity.getBody());
            return -1;
        }
        log.info("Binomo success response: {}", forEntity.getBody());
        return Integer.parseInt(forEntity.getBody());
    }
}
