package com.quatex.evaproxy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class KeitaroService {

    Logger log = LoggerFactory.getLogger(KeitaroService.class);

    private final WebClient webClient;
    private final String keitaroUrl;

    public KeitaroService(WebClient webClient,
                          @Value("${keitaroUrl}") String keitaroUrl) {
        log.info("KeitaroUrl: {}", keitaroUrl);
        this.webClient = webClient;
        this.keitaroUrl = keitaroUrl;
    }

    public Mono<Integer> getStatus(String ip, String name, String systemVersion, String model) {
        return webClient.get().uri(keitaroUrl + "/option.php?", uriBuilder ->
                        uriBuilder.queryParam("ip", ip)
                                .queryParam("name", name)
                                .queryParam("systemName", name)
                                .queryParam("systemVersion", systemVersion)
                                .queryParam("model", model).build()
                ).exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(Integer.class);
                    }

                    return response.createException().flatMap(Mono::error);
                })
                .retry(3)
                .doOnSuccess(result -> log.info("Keitaro success response: {}", result))
                .doOnError(ex -> log.error("Keitaro response", ex))
                .onErrorReturn(-1);
    }
}
