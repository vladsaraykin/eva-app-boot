package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.EconomicEventEntity;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EconomicEventRepository extends ReactiveCassandraRepository<EconomicEventEntity, UUID> {
}
