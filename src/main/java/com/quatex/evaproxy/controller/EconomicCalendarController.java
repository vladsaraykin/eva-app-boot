package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.service.EconomicCalendarService;
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

    @GetMapping("/list-remote")
    public Flux<EconomicEventDto> getListRemote() {
        return economicCalendarService.getEventsFromRemoteService(LocalDate.of(2022, 12, 8), LocalDate.of(2022, 12, 9));
    }
}
