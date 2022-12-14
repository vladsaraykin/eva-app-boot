package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.service.EconomicCalendarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@AllArgsConstructor
@RestController
@RequestMapping("test")
public class EconomicCalendarController {

    private final EconomicCalendarService economicCalendarService;

    @Operation(summary = "Get economic events from outer service")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Success",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = EconomicEventDto.class)),
                    })
    })
    @GetMapping("/list-remote")
    public Flux<EconomicEventDto> getListRemote() {
        return economicCalendarService.getEventsFromRemoteService(LocalDate.of(2022, 12, 8), LocalDate.of(2022, 12, 9));
    }
}
