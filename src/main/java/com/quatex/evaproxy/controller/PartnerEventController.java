package com.quatex.evaproxy.controller;

import com.datastax.oss.driver.api.core.uuid.Uuids;
import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.entity.EventEntity;
import com.quatex.evaproxy.repository.EventRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("partnerevents")
public class PartnerEventController {

    private final EventRepository eventRepository;
    private final String tokenAccess;

    public PartnerEventController(EventRepository eventRepository,
                                  @Value("${token}") String tokenAccess) {
        this.eventRepository = eventRepository;
        this.tokenAccess = tokenAccess;
    }

    @Operation(summary = "Store event from partner service (postback)")
    @GetMapping("/storeEvent") // GET because service integration doesn't support other http methods
    public Mono<EventEntity> registerEvent(@RequestParam("cid") String clickId,
                                           @RequestParam(value = "eid", required = false) String eventId,
                                           @RequestParam(name = "status", required = false) String status,
                                           @RequestParam(name = "reg", required = false) Boolean registration,
                                           @RequestParam(name = "ftd", required = false) Boolean fistReplenishment) {
        if (StringUtils.isBlank(clickId)) {
            log.info("ClickId is empty for eventId {}", eventId);
            return Mono.empty();
        }
        return eventRepository.findByClickId(clickId)
                .next()
                .switchIfEmpty(
                        eventRepository.save(EventEntity.builder()
                                        .id(Uuids.timeBased())
                                        .clickId(clickId)
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
                });
    }

    @Operation(summary = "Get click data")
    @GetMapping("/partnerEvent")
    public Mono<PartnerEventDto> getPartnerEventDto(@RequestParam String clickId) {
        return eventRepository.findByClickId(clickId)
                .next()
                .map(this::mapToPartnerEventDto)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Get click data")
    @GetMapping("/clickData")
    public Mono<Map<String, Boolean>> getClickData(@RequestParam String clickId) {
        return eventRepository.findByClickId(clickId)
                .next()
                .<Map<String, Boolean>>handle((eventEntity, sink) -> sink.next(Map.of(
                        "registration", Optional.ofNullable(eventEntity.getRegistration()).orElse(false),
                        "firstReplenishment", Optional.ofNullable(eventEntity.getFistReplenishment()).orElse(false)
                )))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Get list events")
    @GetMapping("/events")
    public Flux<PartnerEventDto> getEvents(@RequestParam(defaultValue = "50", required = false) Integer limit,
                                           @RequestParam(defaultValue = "0", required = false) Integer offset) {
        return eventRepository.findAllBy(PageRequest.of(offset, limit)).map(this::mapToPartnerEventDto);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("/events/{id}")
    public Mono<Void> deleteEvent(@PathVariable UUID id, @RequestParam String token) {
        if (!Objects.equals(token, this.tokenAccess)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }

        return eventRepository.deleteById(id);
    }

    private PartnerEventDto mapToPartnerEventDto(EventEntity eventEntity) {
        return PartnerEventDto.builder()
                .id(eventEntity.getId())
                .lastChangeUpdated(eventEntity.getLastChangeUpdated())
                .created(eventEntity.getCreated())
                .clickId(eventEntity.getClickId())
                .status(eventEntity.getStatus())
                .registration(eventEntity.getRegistration())
                .fistReplenishment(eventEntity.getFistReplenishment())
                .build();
    }
}
