package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.SettingEntity;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerRepository extends ReactiveCassandraRepository<SettingEntity, Integer> {

    Integer ID = 1;
}
