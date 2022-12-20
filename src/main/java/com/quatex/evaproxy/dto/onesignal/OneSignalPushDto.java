package com.quatex.evaproxy.dto.onesignal;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Builder
@Data
public class OneSignalPushDto {

    @JsonProperty("included_segments")
    private List<String> includedSegments;
    private Map<String, String> contents;
    private Map<String, String> headings;
    private boolean isIos;
    @JsonProperty("app_id")
    private String appId;
}
