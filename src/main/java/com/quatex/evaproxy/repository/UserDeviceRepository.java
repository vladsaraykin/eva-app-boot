package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.UserDeviceEntity;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;

public interface UserDeviceRepository extends ReactiveCassandraRepository<UserDeviceEntity, String> {
}
