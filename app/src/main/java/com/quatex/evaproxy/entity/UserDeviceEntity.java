package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKeyColumn;
import org.springframework.data.cassandra.core.mapping.Table;

@Table(value = "user_device")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDeviceEntity {
    @Id
    @PrimaryKeyColumn
    String deviceId;
    String oneSignalId;
}
