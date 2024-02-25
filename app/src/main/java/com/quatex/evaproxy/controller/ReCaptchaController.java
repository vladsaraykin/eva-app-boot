package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.RecaptchaDto;
import com.quatex.evaproxy.service.ReCaptchaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@AllArgsConstructor
@RequestMapping("recaptcha")
public class ReCaptchaController {

    private final ReCaptchaService reCaptchaService;

    @Operation(summary = "token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get id query for solve captcha",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = String.class)) }) })
    @GetMapping("/register")
    public Mono<String> getToken() {
        return reCaptchaService.registerToken();
    }
    @Operation(summary = "token")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Get token of captcha",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = RecaptchaDto.class)) }) })
    @GetMapping("/token")
    public Mono<RecaptchaDto> getToken(@RequestParam String id) {
        return reCaptchaService.getToken(id);
    }
}
