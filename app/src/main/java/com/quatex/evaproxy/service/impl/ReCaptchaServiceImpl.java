package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.dto.RecaptchaDto;
import com.quatex.evaproxy.service.ReCaptchaService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@ConditionalOnProperty(prefix = "recaptcha", name = "enabled", havingValue = "true")
public class ReCaptchaServiceImpl implements ReCaptchaService {
    private final String apiKey;
    private final String siteKey;
    private final String siteUrl;
    private final WebClient webClient;
    public ReCaptchaServiceImpl(@Value("${recaptcha.apiKey}") String apiKey,
                                @Value("${recaptcha.url}") String siteUrl,
                                @Value("${recaptcha.siteKey}") String siteKey,
                                WebClient webClient) {
        if (StringUtils.isBlank(apiKey) ||
                StringUtils.isBlank(siteKey) ||
                StringUtils.isBlank(siteKey)) {
            throw new BeanCreationException("Params recaptcha is incorrect");
        }
        this.apiKey = apiKey;
        this.siteKey = siteKey;
        this.siteUrl = siteUrl;
        this.webClient = webClient;
    }

    @Override
    public Mono<RecaptchaDto> getToken(String id) {
        return webClient.get()
                .uri("https://2captcha.com/res.php", uriBuilder ->
                        uriBuilder.queryParam("key", apiKey)
                                .queryParam("action", "get")
                                .queryParam("id", id)
                                .build())
                .retrieve()
                .bodyToMono(String.class)
                .<RecaptchaDto>handle((response, sink) -> {
                    if (response.equals("CAPCHA_NOT_READY")) {
                        sink.next(RecaptchaDto.builder().error(response).build());
                        return;
                    }

                    if (!response.startsWith("OK|")) {
                        sink.next(RecaptchaDto.builder().error("Cannot recognise api response (" + response + ")").build());
                        return;
                    }
                    sink.next(RecaptchaDto.builder().token(response.substring(3)).build());
                })
                .retry(3);
    }

    @Override
    public Mono<String> registerToken() {
        return webClient.get()
                .uri("https://2captcha.com/in.php", uriBuilder ->
                    uriBuilder.queryParam("key", apiKey)
                            .queryParam("method", "userrecaptcha")
                            .queryParam("googlekey", siteKey)
                            .queryParam("pageurl", siteUrl)
                            .build()
                ).retrieve()
                .bodyToMono(String.class)
                .<String>handle((response, sink) -> {
                    if (!response.startsWith("OK|")) {
                        sink.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Cannot recognise api response (" + response + ")"));
                    } else {
                        sink.next(response.substring(3));
                    }
                })
                .retry(3);
    }
}
