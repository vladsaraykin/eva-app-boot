package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(value = "economic_event")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EconomicEventEntity implements Serializable {

    @PrimaryKey
    private UUID id;
    private String event;
    private LocalDateTime date;
}
