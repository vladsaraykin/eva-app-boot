package com.quatex.evaproxy.dto;

import com.quatex.evaproxy.keitaro.entity.EventSource;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class PartnerEventSocketMessage {
    
    private String clickId;
    private String message;
    private EventSource eventSource;
}
