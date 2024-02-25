package com.quatex.evaproxy.dto;

import com.quatex.evaproxy.entity.PromoCodeEntity;
import lombok.Getter;

import java.util.List;

@Getter
public class EntryDataDto {

    private final List<PromoCodeEntity> promos;
    private final String link;
    private final String linkPay;
    private final Integer status;

    public EntryDataDto(List<PromoCodeEntity> promos, String link, String linkPay, Integer status) {
        this.promos = promos;
        this.link = link;
        this.linkPay = linkPay;
        this.status = status;
    }

}
