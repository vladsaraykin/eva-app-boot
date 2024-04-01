package com.quatex.evaproxy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.quatex.evaproxy.keitaro.entity.EventSource;
import com.quatex.evaproxy.keitaro.entity.PartnerEventEntity;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PartnerEventDto implements Serializable {
    private UUID id;
    private String clickId;
    private String status;
    private Boolean registration;
    private Boolean fistReplenishment;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastChangeUpdated;

    private EventSource eventSource;

    public static PartnerEventDto fromEntity(PartnerEventEntity eventEntity) {
        return PartnerEventDto.builder()
                .id(eventEntity.getId())
                .lastChangeUpdated(eventEntity.getLastChangeUpdated())
                .created(eventEntity.getCreated())
                .clickId(eventEntity.getClickId())
                .status(eventEntity.getStatus())
                .registration(eventEntity.getRegistration())
                .fistReplenishment(eventEntity.getFistReplenishment())
                .eventSource(eventEntity.getEventSource())
                .build();
    }
}
