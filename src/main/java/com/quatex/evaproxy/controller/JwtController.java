package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.LoginDto;
import com.quatex.evaproxy.dto.RegistrationDto;
import com.quatex.evaproxy.service.JwtProxyService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@AllArgsConstructor
public class JwtController {

    private final JwtProxyService jwtProxyService;
    @PostMapping("/registrationByPassword")
    public Mono<Map<String, Object>> registrationByPassword(@RequestBody RegistrationDto registrationDto) {
        return jwtProxyService.registrationByPassword(registrationDto);
    }

    @PostMapping("/loginByPassword")
    public Mono<Map<String, Object>> registrationByPassword(@RequestBody LoginDto loginDto) {
        return jwtProxyService.loginByPassword(loginDto);
    }
}
