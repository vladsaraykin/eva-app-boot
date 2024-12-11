package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;

@Table(value = "manager_setting")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ManagerSetting implements Serializable {

    @PrimaryKey
    private Integer version;
    private String link;
    private Integer enabled;
}
