package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.LoginDto;
import com.quatex.evaproxy.dto.RegistrationDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface JwtProxyService {
    Mono<ResponseEntity<Map<String, Object>>> registrationByPassword(RegistrationDto registrationDto);

    Mono<Map<String, Object>> loginByPassword(LoginDto loginDto);
}
