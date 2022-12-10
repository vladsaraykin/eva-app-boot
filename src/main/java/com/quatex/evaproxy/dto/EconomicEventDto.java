package com.quatex.evaproxy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EconomicEventDto {

    @JsonProperty("event")
    private String event;

    @JsonFormat(
            shape = JsonFormat.Shape.STRING,
            pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonProperty("date")
    private LocalDateTime dateTimeUtc;

    @JsonProperty("country")
    private String country;

    @JsonProperty("actual")
    private Double actual;

    @JsonProperty("previous")
    private Double previous;

    @JsonProperty("change")
    private Double change;

    @JsonProperty("changePercentage")
    private Double changePercentage;

    @JsonProperty("estimate")
    private Double estimate;

    @JsonProperty("impact")
    private String impact;
}
