package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(value = "partner_event")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class EventEntity implements Serializable {
    @PrimaryKey
    private UUID id;
    private String clickId;
    private String status;
    private Boolean registration;
    private Boolean fistReplenishment;
    private LocalDateTime created;
    private LocalDateTime lastChangeUpdated;
}
