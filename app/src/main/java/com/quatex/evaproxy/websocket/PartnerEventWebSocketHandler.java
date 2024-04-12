package com.quatex.evaproxy.websocket;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.quatex.evaproxy.dto.PartnerEventSocketMessage;
import com.quatex.evaproxy.keitaro.repository.PartnerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartnerEventWebSocketHandler implements WebSocketHandler {


    private final Sinks.Many<PartnerEventSocketMessage> partnerEventSink;
    private final PartnerEventRepository partnerEventRepository;
    private final String START_LISTEN = "start listen";
    public static final String EVENT_REGISTRATION = "portal_event_registration_completed";
    public static final String EVENT_DEPOSIT = "portal_event_deposit_completed";

    private final Cache<String, WebSocketSessionDataPublisher> ttlCache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofMinutes(10))
            .removalListener((key, item, cause) -> {
                var ws =(WebSocketSessionDataPublisher)item;
                if (ws != null) {
                    log.info("remove old session {} {}", key, ws.clickId);
                    ws.disposable.dispose();
                    ws.session.close().subscribe();
                }
            })
            .build();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri());
        Map<String,String> queryParams = builder.build().getQueryParams().toSingleValueMap();
        String clickId = queryParams.get("clickId");

        log.info("Websocket connected for id " + clickId);
        var wsPublisher = new WebSocketSessionDataPublisher(clickId, session);

        wsPublisher.disposable = partnerEventSink.asFlux().subscribe(wsPublisher);
        ttlCache.put(session.getId(), wsPublisher);

        Flux<WebSocketMessage> messageFlux = session.receive().takeUntil(s -> !session.isOpen()).share();

        Flux<String> input = messageFlux
                .filter(webSocketMessage -> webSocketMessage.getType() == WebSocketMessage.Type.TEXT)
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(inputTxt -> {
                    log.info("Received message from " + clickId);
                    if (START_LISTEN.equalsIgnoreCase(inputTxt)) {
                        return partnerEventRepository.findByClickId(clickId)
                                .map(entity -> {
                                    if (Boolean.TRUE.equals(entity.getRegistration())) {
                                        partnerEventSink.tryEmitNext(PartnerEventSocketMessage.builder()
                                                .message(EVENT_REGISTRATION)
                                                .clickId(entity.getClickId())
                                                .eventSource(entity.getEventSource())
                                                .build());
                                    }
                                    if (Boolean.TRUE.equals(entity.getFistReplenishment())) {
                                        partnerEventSink.tryEmitNext(PartnerEventSocketMessage.builder()
                                                .message(EVENT_DEPOSIT)
                                                .clickId(entity.getClickId())
                                                .eventSource(entity.getEventSource())
                                                .build());
                                    }
                                    return "success";
                                });
                    }
                    return Mono.empty();
                });

        log.info("WebSocket subscribers {}", partnerEventSink.currentSubscriberCount());
        return Flux.merge(input).then();
    }
    private class WebSocketSessionDataPublisher implements Consumer<PartnerEventSocketMessage> {

        String clickId;
        WebSocketSession session;

        Disposable disposable;
        public WebSocketSessionDataPublisher(String clickId, WebSocketSession session){
            this.clickId = clickId;
            this.session = session;
        }

        @SneakyThrows
        @Override
        public void accept(PartnerEventSocketMessage eventSocketMessage) {
            if(this.clickId.equalsIgnoreCase(eventSocketMessage.getClickId()) && eventSocketMessage.getMessage() != null) {
                if (session.isOpen()) {
                    log.info("Message to be sent for clickId " + eventSocketMessage.getClickId());
                    ttlCache.put(session.getId(), this); //update time to live up
                    session.send(Mono.just(session.textMessage(eventSocketMessage.getMessage() + " " + eventSocketMessage.getEventSource()))).subscribe();
                } else {
                    disposable.dispose();
                }
            }
        }
    }
}
