package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.EconomicEventDto;
import com.quatex.evaproxy.entity.EconomicEventEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface EconomicCalendarService {

    Flux<EconomicEventDto> getEventsFromRemoteService(LocalDate startDate, LocalDate endDate);
    Flux<EconomicEventEntity> saveAll(Flux<EconomicEventDto> eventsStream);
    Mono<Void> deleteAll();
}
