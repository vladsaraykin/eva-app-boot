package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;

import static com.quatex.evaproxy.config.AppConfig.LOCK_TABLE_NAME;

@Table(value = LOCK_TABLE_NAME)
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Lock {

    @Id
    private String name;
    private LocalDateTime lockUntil;
    private LocalDateTime lockedAt;
    private String lockedBy;
}
