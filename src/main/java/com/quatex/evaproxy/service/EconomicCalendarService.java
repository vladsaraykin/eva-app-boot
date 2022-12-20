package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.EconomicEventDto;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface EconomicCalendarService {

    Flux<EconomicEventDto> getEventsFromRemoteService(LocalDate startDate, LocalDate endDate);
}
