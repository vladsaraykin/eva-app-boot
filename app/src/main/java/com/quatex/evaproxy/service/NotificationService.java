package com.quatex.evaproxy.service;

import reactor.core.publisher.Mono;

public interface NotificationService {

    Mono<String> push(String headerMessage, String message);
}
