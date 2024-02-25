package com.quatex.evaproxy.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class RecaptchaDto {
    private String token;
    private String error;

}
