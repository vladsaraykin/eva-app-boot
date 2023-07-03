package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Table(value = "setting")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettingEntity implements Serializable {

    @PrimaryKey
    private Integer id;

    @CassandraType(type = CassandraType.Name.TUPLE, typeArguments = {
            CassandraType.Name.TEXT,
            CassandraType.Name.TEXT
    })
    private VersionStructure<String> link;

    @CassandraType(type = CassandraType.Name.TUPLE, typeArguments = {
            CassandraType.Name.INT,
            CassandraType.Name.INT
    })
    private VersionStructure<Integer> enabled;

    private Integer version;

    private String linkCryptoPay;

}
