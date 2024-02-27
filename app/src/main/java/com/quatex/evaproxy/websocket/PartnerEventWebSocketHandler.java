package com.quatex.evaproxy.websocket;

import com.quatex.evaproxy.keitaro.repository.PartnerEventRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Component
@AllArgsConstructor
public class PartnerEventWebSocketHandler implements WebSocketHandler {

    private final PartnerEventRepository partnerEventRepository;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String protocol = session.getHandshakeInfo().getSubProtocol();
        WebSocketMessage message = session.textMessage(partnerEventRepository.findAll().takeLast(0).toString());
        return doSend(session, Mono.just(message));
    }

    // TODO: workaround for suspected RxNetty WebSocket client issue
    // https://github.com/ReactiveX/RxNetty/issues/560
    private Mono<Void> doSend(WebSocketSession session, Mono<WebSocketMessage> output) {
        return session.send(Mono.delay(Duration.ofMillis(100)).thenMany(output));
    }
}
