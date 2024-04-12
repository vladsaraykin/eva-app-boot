package com.quatex.evaproxy.keitaro.repository;

import com.quatex.evaproxy.keitaro.entity.EventSource;
import com.quatex.evaproxy.keitaro.entity.PartnerEventEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface PartnerEventRepository extends ReactiveCrudRepository<PartnerEventEntity, UUID> {

    Flux<PartnerEventEntity> findByClickId(String clickId);
    Flux<PartnerEventEntity> findAllBy(Pageable pageable);

    Flux<PartnerEventEntity> findByClickIdAndEventSource(String clickId, EventSource eventSource);
}
