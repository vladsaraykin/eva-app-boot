package com.quatex.evaproxy.repository;

import com.quatex.evaproxy.entity.ManagerSetting;
import org.springframework.data.cassandra.repository.ReactiveCassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManagerSettingRepository extends ReactiveCassandraRepository<ManagerSetting, Integer> {

}
