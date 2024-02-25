package com.quatex.evaproxy.service.impl;

import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.service.EconomicCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

@Slf4j
@Service
public class FinancialModelingPrep implements EconomicCalendarService {

    private final WebClient webClient;
    private final String apiKey;

    public FinancialModelingPrep(WebClient webClient,
                                 @Value("${financialmodelingprep.apikey}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    @Override
    public Flux<EconomicEventDto> getEventsFromRemoteService(LocalDate startDate, LocalDate endDate) {
        return webClient.get()
                .uri("https://financialmodelingprep.com/api/v3/economic_calendar", uriBuilder -> uriBuilder
                        .queryParam("from", startDate)
                        .queryParam("to", endDate)
                        .queryParam("apikey", apiKey)
                        .build()
                ).retrieve()
                .bodyToFlux(EconomicEventDto.class)
                .retry(3);
    }
}
