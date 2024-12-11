package com.quatex.evaproxy.dto;

import com.quatex.evaproxy.entity.PromoCodeEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class EntryDataDto {

    private final List<PromoCodeEntity> promos;
    private final String link;
    private final Integer status;

    public EntryDataDto(List<PromoCodeEntity> promos, String link, Integer status) {
        this.promos = promos;
        this.link = link;
        this.status = status;
    }

}
