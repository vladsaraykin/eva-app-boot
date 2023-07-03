package com.quatex.evaproxy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
}
