package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.EventEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface EventRepository extends ReactiveCassandraRepository<EventEntity, UUID> {

    @AllowFiltering
    //tmp bug fix with unique
    Flux<EventEntity> findByClickId(String clickId);

    @AllowFiltering
    Flux<EventEntity> findAllBy(Pageable pageable);
}
