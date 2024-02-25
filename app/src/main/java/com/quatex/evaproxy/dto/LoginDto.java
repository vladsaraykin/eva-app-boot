package com.quatex.evaproxy.dto;

import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//dto for integration
public class LoginDto {
    private UserInfoDto data;
}
