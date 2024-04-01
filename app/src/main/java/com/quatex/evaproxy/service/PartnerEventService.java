package com.quatex.evaproxy.service;

import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.keitaro.entity.EventSource;
import com.quatex.evaproxy.keitaro.entity.PartnerEventEntity;
import com.quatex.evaproxy.keitaro.repository.PartnerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PartnerEventService {

    private final Sinks.Many<PartnerEventDto> partnerEventSink;
    private final PartnerEventRepository eventRepository;
    public Mono<PartnerEventEntity> storeEvent(String clickId,
                                                String status,
                                                Boolean registration,
                                                Boolean fistReplenishment,
                                                EventSource eventSource) {
        return eventRepository.findByClickIdAndEventSource(clickId, eventSource)
                .switchIfEmpty(
                        eventRepository.save(PartnerEventEntity.builder()
                                        .clickId(clickId)
                                        .eventSource(eventSource)
                                        .created(LocalDateTime.now(ZoneOffset.UTC)).build())
                                .doOnSuccess(e -> log.info("Store new user with click: {}", clickId))
                ).flatMap(eventEntityDb -> {
                    boolean needStore = false;
                    if (registration != null) {
                        needStore = true;
                        eventEntityDb.setRegistration(registration);
                    }
                    if (fistReplenishment != null) {
                        needStore = true;
                        eventEntityDb.setFistReplenishment(fistReplenishment);
                    }
                    if (status != null) {
                        needStore = true;
                        eventEntityDb.setStatus(status);
                    }
                    if (needStore) {
                        eventEntityDb.setLastChangeUpdated(LocalDateTime.now(ZoneOffset.UTC));
                        log.info("Event updated: {}", eventEntityDb);
                    }

                    return needStore ? eventRepository.save(eventEntityDb) : Mono.just(eventEntityDb);
                })
                .next().doOnSuccess(partnerEventEntity -> {
                    Sinks.EmitResult emitResult = partnerEventSink.tryEmitNext(PartnerEventDto.fromEntity(partnerEventEntity));
                    log.info("ClickId({})Emit result status {} {}", clickId, emitResult.name(), emitResult.isSuccess());
                });
    }

    public Flux<PartnerEventEntity> findByClickIdAndEventSource(String clickId, EventSource eventSource) {
        return eventRepository.findByClickIdAndEventSource(clickId, eventSource);
    }


    public Flux<PartnerEventEntity> findAllBy(PageRequest page) {
        return eventRepository.findAllBy(page);
    }

    public Mono<Void> deleteById(UUID id) {
        return eventRepository.deleteById(id);
    }
}
