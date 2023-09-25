package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.dto.RecaptchaDto;
import com.quatex.evaproxy.service.ReCaptchaService;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "recaptcha", name = "enabled", havingValue = "false")
public class MockReCaptchaService implements ReCaptchaService {
    @Override
    public Mono<RecaptchaDto> getToken(String id) {
        throw new NotImplementedException();
    }

    @Override
    public Mono<String> registerToken() {
        throw new NotImplementedException();
    }
}
