package com.quatex.evaproxy.controller;

import com.quatex.evaproxy.config.PartnerPostBackParams;
import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.keitaro.entity.EventSource;
import com.quatex.evaproxy.keitaro.entity.PartnerEventEntity;
import com.quatex.evaproxy.keitaro.repository.PartnerEventRepository;
import com.quatex.evaproxy.service.PartnerEventService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("partnerevents")
@RequiredArgsConstructor
public class PartnerEventController {

    private final PartnerEventRepository eventRepository;
    private final PartnerPostBackParams partnerPostBackParams;
    private final PartnerEventService partnerEventService;
    @Value("${token}")
    private String tokenAccess;

    @Operation(summary = "Store event(for local using)")
    @PostMapping("/storeLocalEvent")
    public Mono<PartnerEventEntity> registerEventForAction(
            @RequestParam String clickId,
            @RequestParam Boolean reg,
            @RequestParam Boolean ftd) {
        if (StringUtils.isBlank(clickId)) {
            log.warn("ClickId is empty for eventId {}", clickId);
            return Mono.empty();
        }
        return partnerEventService.storeEvent(clickId, "", reg, ftd, EventSource.LOCAL);
    }
    @Operation(summary = "Store event from partner service (postback)")
    @GetMapping("/storeEvent/{action}") // GET because service integration doesn't support other http methods
    public Mono<PartnerEventEntity> registerEventForAction(
            @PathVariable String action,
            @RequestParam Map<String, String> allRequestParams) {
        String clickId = allRequestParams.get(partnerPostBackParams.getClickId());
        String eventId = allRequestParams.get(partnerPostBackParams.getEventId());
        String status = allRequestParams.get(partnerPostBackParams.getStatus());
        Boolean reg = "reg".equalsIgnoreCase(action) ? true : null;
        Boolean ftd = "ftd".equalsIgnoreCase(action) ? true : null;
        log.debug("Store event Received params {}", allRequestParams);
        if (StringUtils.isBlank(clickId)) {
            log.warn("ClickId is empty for eventId {}", eventId);
            return Mono.empty();
        }
        return partnerEventService.storeEvent(clickId, status, reg, ftd, EventSource.PARTNER);
    }
    @Operation(summary = "Store event from partner service (postback)")
    @GetMapping("/storeEvent") // GET because service integration doesn't support other http methods
    public Mono<PartnerEventEntity> registerEvent(@RequestParam Map<String, String> allRequestParams) {
        String clickId = allRequestParams.get(partnerPostBackParams.getClickId());
        String eventId = allRequestParams.get(partnerPostBackParams.getEventId());
        String status = allRequestParams.get(partnerPostBackParams.getStatus());
        Boolean registration = Optional.ofNullable(allRequestParams.get(partnerPostBackParams.getRegistration()))
                .map(Boolean::parseBoolean)
                .orElse(null);
        Boolean fistReplenishment = Optional.ofNullable(allRequestParams.get(partnerPostBackParams.getFistReplenishment()))
                .map(Boolean::parseBoolean)
                .orElse(null);
        log.debug("Store event Received params {}", allRequestParams);
        if (StringUtils.isBlank(clickId)) {
            log.warn("ClickId is empty for eventId {}", eventId);
            return Mono.empty();
        }
        return partnerEventService.storeEvent(clickId, status, registration, fistReplenishment, EventSource.PARTNER);
    }
    @Operation(summary = "Get click data")
    @GetMapping("/partnerEvent")
    public Mono<PartnerEventDto> getPartnerEventDto(@RequestParam String clickId,
                                                    @RequestParam(required = false, defaultValue = "PARTNER") EventSource eventSource) {
        return partnerEventService.findByClickIdAndEventSource(clickId, eventSource)
                .next()
                .map(PartnerEventDto::fromEntity)
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Get click data")
    @GetMapping("/clickData")
    public Mono<Map<String, Boolean>> getClickData(@RequestParam String clickId,
                                                   @RequestParam(required = false, defaultValue = "PARTNER") EventSource eventSource) {
        return eventRepository.findByClickId(clickId)
                .next()
                .<Map<String, Boolean>>handle((eventEntity, sink) -> sink.next(Map.of(
                        "registration", Optional.ofNullable(eventEntity.getRegistration()).orElse(false),
                        "firstReplenishment", Optional.ofNullable(eventEntity.getFistReplenishment()).orElse(false)
                )))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)));
    }

    @Operation(summary = "Get list events", hidden = true)
    @GetMapping("/events")
    public Flux<PartnerEventDto> getEvents(@RequestParam(defaultValue = "50", required = false) Integer limit,
                                           @RequestParam(defaultValue = "0", required = false) Integer offset) {
        return partnerEventService.findAllBy(PageRequest.of(offset, limit)).map(PartnerEventDto::fromEntity);
    }

    @Operation(summary = "Delete event")
    @DeleteMapping("/events/{id}")
    public Mono<Void> deleteEvent(@PathVariable UUID id, @RequestParam String token) {
        if (!Objects.equals(token, this.tokenAccess)) {
            throw new HttpClientErrorException(HttpStatus.FORBIDDEN);
        }

        return partnerEventService.deleteById(id);
    }
}
