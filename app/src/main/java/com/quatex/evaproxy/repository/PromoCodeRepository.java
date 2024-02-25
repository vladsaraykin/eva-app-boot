package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.PromoCodeEntity;
import org.springframework.data.cassandra.repository.AllowFiltering;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PromoCodeRepository extends ReactiveCassandraRepository<PromoCodeEntity, UUID> {

    @AllowFiltering
    Mono<PromoCodeEntity> findByCode(String code);
}
