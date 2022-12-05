package com.quatex.evaproxy.controller;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quatex.evaproxy.dto.EntryDataDto;
import com.quatex.evaproxy.service.ManageService;
import com.quatex.evaproxy.service.PromoCodeService;
import com.quatex.evaproxy.utils.CacheUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@RestController
public class EntryController {

    private final PromoCodeService promoCodeService;
    private final ManageService manageService;
    private final AsyncCache<Integer, EntryDataDto> cache;

    public EntryController(PromoCodeService promoCodeService, ManageService manageService) {
        this.promoCodeService = promoCodeService;
        this.manageService = manageService;
        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(30, TimeUnit.SECONDS)
                .buildAsync();
    }

    @Operation(summary = "Get entry data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EntryDataDto.class)),
                    })
    })
    @GetMapping("/entryData")
    public Mono<EntryDataDto> getEntryData(@RequestParam(defaultValue = "1") Integer version) {
        return CacheUtils.getCacheValue(
                cache,
                version,
                Mono.zip(
                        promoCodeService.getAll().collectList(),
                        manageService.getLink(version).defaultIfEmpty(""),
                        manageService.getLinkCryptoPay().defaultIfEmpty("")
                ).map(data -> new EntryDataDto(
                        data.getT1(),
                        data.getT2(),
                        data.getT3())));
    }
}
