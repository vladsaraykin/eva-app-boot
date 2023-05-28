package com.quatex.evaproxy.dto;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SettingDto {
    private String link;
    private Integer enabled;
    private String linkCryptoPay;
}
