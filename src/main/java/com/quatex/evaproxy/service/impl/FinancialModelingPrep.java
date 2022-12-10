package com.quatex.evaproxy.service.impl;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.entity.EconomicEventEntity;
import com.quatex.evaproxy.repository.EconomicEventRepository;
import com.quatex.evaproxy.service.EconomicCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@Slf4j
@Service
public class FinancialModelingPrep implements EconomicCalendarService {

    private final WebClient webClient;
    private final EconomicEventRepository eventRepository;
    private final String apiKey;

    public FinancialModelingPrep(WebClient webClient,
                                 EconomicEventRepository eventRepository,
                                 @Value("${financialmodelingprep.apikey}") String apiKey) {
        this.webClient = webClient;
        this.eventRepository = eventRepository;
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

    @Override
    public Flux<EconomicEventEntity> saveAll(Flux<EconomicEventDto> eventsStream) {
        return eventRepository.saveAll(eventsStream.map(this::mapToEconomicEventEntity));
    }

    @Override
    public Mono<Void> deleteAll() {
        return eventRepository.deleteAll();
    }


    public EconomicEventEntity mapToEconomicEventEntity(EconomicEventDto dto) {
        log.info("Map economic event: {} - {}", dto.getEvent(), dto.getDateTimeUtc());
        return EconomicEventEntity.builder()
                .id(Uuids.timeBased())
                .event(dto.getEvent())
                .date(dto.getDateTimeUtc())
                .build();
    }
}
