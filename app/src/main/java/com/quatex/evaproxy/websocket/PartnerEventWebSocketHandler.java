package com.quatex.evaproxy.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.keitaro.repository.PartnerEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.function.Consumer;

@Component
@Slf4j
@RequiredArgsConstructor
public class PartnerEventWebSocketHandler implements WebSocketHandler {


    private final Sinks.Many<PartnerEventDto> partnerEventSink;
    private final PartnerEventRepository partnerEventRepository;

    private final ObjectMapper objectMapper;
    private final String START_LISTEN = "start listen";
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(session.getHandshakeInfo().getUri());
        Map<String,String> queryParams = builder.build().getQueryParams().toSingleValueMap();
        String clickId = queryParams.get("clickId");

        log.info("Websocket connected for id " + clickId);
        partnerEventSink.asFlux().subscribe(new WebSocketSessionDataPublisher(clickId, session));
        Flux<WebSocketMessage> messageFlux = session.receive().share();

//        partnerEventSink.
//        Sinks.EmitResult emitResult = partnerEventSink.tryEmitNext(mapToPartnerEventDto(partnerEventEntity));
//        log.info("ClickId({})Emit result status {} {}", clickId, emitResult.name(), emitResult.isSuccess());

        Flux<String> input = messageFlux
                .filter(webSocketMessage -> webSocketMessage.getType() == WebSocketMessage.Type.TEXT)
                .map(WebSocketMessage::getPayloadAsText)
                .flatMap(inputTxt -> {
                    if (START_LISTEN.equalsIgnoreCase(inputTxt)) {
                        return partnerEventRepository.findByClickId(clickId)
                                .map(entity -> {
                                    partnerEventSink.tryEmitNext(PartnerEventDto.fromEntity(entity));
                                    return "succes";
                                });
                    }
                    return Mono.empty();
                })
                .doOnNext(s -> {
                    log.info("Received message from " + clickId);
                });


        return Flux.merge(input).then();
    }

    private class WebSocketSessionDataPublisher implements Consumer<PartnerEventDto> {

        String clickId;
        WebSocketSession session;
        public WebSocketSessionDataPublisher(String clickId, WebSocketSession session){
            this.clickId = clickId;
            this.session = session;
        }

        @SneakyThrows
        @Override
        public void accept(PartnerEventDto eventDto) {
            log.info("Message to be sent for clickId " + eventDto.getClickId());
            if(this.clickId.equalsIgnoreCase(eventDto.getClickId())){
                log.info("[Before Send]Message to be sent for clickId " + eventDto.getClickId());
                session.send(Mono.just(session.textMessage("Update user event:\n" + objectMapper.writeValueAsString(eventDto)))).subscribe();
            }
        }
    }
}
