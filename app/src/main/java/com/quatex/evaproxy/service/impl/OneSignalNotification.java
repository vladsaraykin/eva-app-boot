package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.dto.onesignal.OneSignalPushDto;
import com.quatex.evaproxy.service.NotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
@ConditionalOnExpression(value = "!'${onesignal.apiKey}'.equals('')")
public class OneSignalNotification implements NotificationService {

    private final WebClient webClient;
    private final String apiKey;
    private final String appId;
    public OneSignalNotification(WebClient webClient,
                                 @Value("${onesignal.apiKey}") String apiKey,
                                 @Value("${onesignal.appId}") String appId) {
        this.webClient = webClient;
        this.apiKey = apiKey;
        this.appId = appId;
    }

    @Override
    public Mono<String> push(String headerMessage, String message) {
        OneSignalPushDto body = OneSignalPushDto.builder()
                .includedSegments(List.of("Active Users", "Inactive Users"))
                .headings(Map.of("en", headerMessage))
                .contents(Map.of("en", message))
                .isIos(true)
                .appId(appId)
                .build();
        return webClient.post()
                .uri("https://onesignal.com/api/v1/notifications")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.setBasicAuth(apiKey))
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class);
    }
}
