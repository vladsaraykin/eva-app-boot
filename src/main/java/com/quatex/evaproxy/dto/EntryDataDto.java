package com.quatex.evaproxy.dto;

import com.quatex.evaproxy.entity.PromoCodeEntity;

import java.util.List;

public class EntryDataDto {

    private final List<PromoCodeEntity> promos;
    private final String link;
    private final String linkPay;

    public EntryDataDto(List<PromoCodeEntity> promos, String link, String linkPay) {
        this.promos = promos;
        this.link = link;
        this.linkPay = linkPay;
    }

    public List<PromoCodeEntity> getPromos() {
        return promos;
    }

    public String getLink() {
        return link;
    }

    public String getLinkPay() {
        return linkPay;
    }

}
