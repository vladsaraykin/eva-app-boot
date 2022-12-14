package com.quatex.evaproxy.entity;

import lombok.*;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.io.Serializable;
import java.util.UUID;

@Table(value = "promo_code")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class PromoCodeEntity implements Serializable {

    @PrimaryKey
    private UUID id;
    private String tittle;
    private String subtitle;
    private String code;
}
