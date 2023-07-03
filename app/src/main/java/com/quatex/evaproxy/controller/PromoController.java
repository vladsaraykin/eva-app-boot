package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.entity.PromoCodeEntity;
import com.quatex.evaproxy.service.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("promocode")
public class PromoController {

    private final PromoCodeService promoCodesService;

    public PromoController(PromoCodeService promoCodesService) {
        this.promoCodesService = promoCodesService;
    }

    @Operation(summary = "Get promo codes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the promo codes",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromoCodeEntity.class)) }) })
    @GetMapping("/list")
    public Flux<PromoCodeEntity> getAll() {
        return promoCodesService.getAll();
    }

    @Operation(summary = "Get promo code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the promo code",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromoCodeEntity.class)),
                    }),
            @ApiResponse(responseCode = "404", description = "Not found promo code")})
    @GetMapping("/{code}")
    public Mono<PromoCodeEntity> findByCode(@PathVariable(name = "code") String code) {
        return promoCodesService.getByCode(code);
    }

    @Operation(summary = "Create promo code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Success",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromoCodeEntity.class)),
                    }),
            @ApiResponse(responseCode = "409", description = "Already exist"),
            @ApiResponse(responseCode = "400", description = "Code not be null")})
    @PostMapping("/create")
    public Mono<PromoCodeEntity> createCode(@RequestBody PromoCodeEntity promoCodeEntity) {
        return promoCodesService.create(promoCodeEntity);
    }

    @Operation(summary = "Update promo code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromoCodeEntity.class)),
                    }),
            @ApiResponse(responseCode = "404", description = "Code not be null")})
    @PostMapping("/update")
    public Mono<PromoCodeEntity> updateCode(@RequestBody PromoCodeEntity promoCodeEntity) {
        return promoCodesService.update(promoCodeEntity);
    }

    @Operation(summary = "Delete promo code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success")})
    @DeleteMapping("/{uuid}")
    public Mono<Void> deleteCode(@PathVariable("uuid") UUID uuid) {
        return promoCodesService.delete(uuid);
    }
}
