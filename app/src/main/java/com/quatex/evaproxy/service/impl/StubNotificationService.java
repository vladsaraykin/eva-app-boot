package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.service.NotificationService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnMissingBean(OneSignalNotification.class)
public class StubNotificationService implements NotificationService {
    @Override
    public Mono<String> push(String headerMessage, String message) {
        return Mono.just("not implement");
    }
}
