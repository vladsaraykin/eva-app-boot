package com.quatex.evaproxy.keitaro.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(value = "partner_event")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Setter
@Getter
@ToString
public class PartnerEventEntity implements Serializable {
    @Id
    private UUID id;
    private String clickId;
    private String status;
    private Boolean registration;
    private Boolean fistReplenishment;
    private LocalDateTime created;
    private LocalDateTime lastChangeUpdated;
}
