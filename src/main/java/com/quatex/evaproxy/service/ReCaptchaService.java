package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.RecaptchaDto;
import reactor.core.publisher.Mono;

public interface ReCaptchaService {

    Mono<RecaptchaDto> getToken(String id);
    Mono<String> registerToken();
}
