package com.quatex.evaproxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class StaticRouteConfig {

    @Bean
    public RouterFunction<ServerResponse> htmlRouter() {
        return RouterFunctions.resources("/**", new ClassPathResource("static/"));
    }

}
