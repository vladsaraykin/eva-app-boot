package com.quatex.evaproxy.config;

import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.websocket.PartnerEventWebSocketHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import reactor.core.publisher.Sinks;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class WebConfig {

    @Bean
    public Sinks.Many<PartnerEventDto> partnerEventSink(){
        return Sinks.many().multicast().directBestEffort();
    }
    @Bean
    public HandlerMapping handlerMapping(PartnerEventWebSocketHandler partnerEventWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/listen", partnerEventWebSocketHandler);

        SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping();
        mapping.setUrlMap(map);
        mapping.setOrder(1);
        return mapping;
    }
}
