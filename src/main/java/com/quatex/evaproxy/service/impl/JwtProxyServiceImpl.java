package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.dto.LoginDto;
import com.quatex.evaproxy.dto.RegistrationDto;
import com.quatex.evaproxy.service.JwtProxyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class JwtProxyServiceImpl implements JwtProxyService {

    private final WebClient webClient;
    private final String GET_JWT_TOKEN_URL;
    private final String REGISTRATION_USER_URL;
    private final String LOGIN_USER_URL;

    public JwtProxyServiceImpl(WebClient webClient,
                               @Value("${jwt.registrationUrl:none}") String registrationUserUrl,
                               @Value("${jwt.getJwtTokenUrl:none}") String getJwtTokenUrl,
                               @Value("${jwt.loginUrl:none}") String loginUserUrl) {
        this.webClient = webClient;
        REGISTRATION_USER_URL = registrationUserUrl;
        GET_JWT_TOKEN_URL = getJwtTokenUrl;
        LOGIN_USER_URL = loginUserUrl;
    }

    @Override
    public Mono<Map<String, Object>> registrationByPassword(RegistrationDto registrationDto) {
        return webClient.get()
                .uri(GET_JWT_TOKEN_URL)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class)
                .flatMap(jwt -> webClient.post()
                        .uri(REGISTRATION_USER_URL, builder -> builder.queryParam("jwt_traffic", jwt).build())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .headers(h -> h.set("X-Request-Project", "otp"))
                        .bodyValue(registrationDto)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<>() {}));
    }

    @Override
    public Mono<Map<String, Object>> loginByPassword(LoginDto loginDto) {
        return webClient.post()
                .uri(LOGIN_USER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .headers(h -> h.set("X-Request-Project", "otp"))
                .bodyValue(loginDto)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<>() {});
    }
}
