package com.quatex.evaproxy.config;

import com.datastax.oss.driver.api.core.CqlSession;
import com.quatex.evaproxy.dto.PartnerEventDto;
import com.quatex.evaproxy.keitaro.config.FlywayConfiguration;
import io.netty.channel.ChannelOption;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.SneakyThrows;
import net.javacrumbs.shedlock.core.DefaultLockingTaskExecutor;
import net.javacrumbs.shedlock.core.LockProvider;
import net.javacrumbs.shedlock.core.LockingTaskExecutor;
import net.javacrumbs.shedlock.provider.cassandra.CassandraLockProvider;
import net.javacrumbs.shedlock.provider.cassandra.CassandraLockProvider.Configuration;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import org.springframework.boot.autoconfigure.cassandra.CassandraProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Sinks;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@EnableScheduling
@EnableSchedulerLock(defaultLockAtMostFor = "10s")
@EnableWebFlux
@Import(FlywayConfiguration.class)
@org.springframework.context.annotation.Configuration
public class AppConfig {

    public static final String LOCK_TABLE_NAME = "lock";

    @Bean
    @SneakyThrows
    public WebClient webClient() {
        final HttpClient httpClient = HttpClient.create()
                .wiretap(true)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn.addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS)));
       //ignore ssl
        final SslContext sslContext = SslContextBuilder.forClient().trustManager(InsecureTrustManagerFactory.INSTANCE).build();
        httpClient.secure(t -> t.sslContext(sslContext));

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    @Bean
    public CassandraLockProvider lockProvider(CqlSession cqlSession, CassandraProperties properties) {
        return new CassandraLockProvider(Configuration.builder()
                .withCqlSession(cqlSession)
                .withTableName(LOCK_TABLE_NAME)
                .withConsistencyLevel(properties.getRequest().getConsistency())
                .build());
    }

    @Bean
    public LockingTaskExecutor lockingTaskExecutor(LockProvider lockProvider) {
        return new DefaultLockingTaskExecutor(lockProvider);
    }

    @Bean
    public Sinks.Many<PartnerEventDto> sink(){
        return Sinks.many().replay().latest();
    }
}
