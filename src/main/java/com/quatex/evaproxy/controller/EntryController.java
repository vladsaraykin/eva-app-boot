package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.EntryDataDto;
import com.quatex.evaproxy.entity.PromoEntity;
import com.quatex.evaproxy.service.ManageService;
import com.quatex.evaproxy.service.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EntryController {

    private final PromoCodeService promoCodeService;
    private final ManageService manageService;

    public EntryController(PromoCodeService promoCodeService, ManageService manageService) {
        this.promoCodeService = promoCodeService;
        this.manageService = manageService;
    }

    @Operation(summary = "Get entry data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = PromoEntity.class)),
                    })
    })
    @GetMapping("/entryData")
    public ResponseEntity<EntryDataDto> getEntryData(@RequestParam(defaultValue = "1") Integer version) {
        return ResponseEntity.ok(new EntryDataDto(
                promoCodeService.getAll(),
                manageService.getLink(version),
                manageService.getLinkCryptoPay(),
                manageService.getEnabled(version))
        );
    }
}
