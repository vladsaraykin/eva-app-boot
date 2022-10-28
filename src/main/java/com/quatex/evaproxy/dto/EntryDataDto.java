package com.quatex.evaproxy.dto;

import com.quatex.evaproxy.entity.PromoEntity;

import java.util.List;

public class EntryDataDto {

    private final List<PromoEntity> promos;
    private final String link;
    private final String linkPay;
    private final Integer status;

    public EntryDataDto(List<PromoEntity> promos, String link, String linkPay, Integer status) {
        this.promos = promos;
        this.link = link;
        this.linkPay = linkPay;
        this.status = status;
    }

    public List<PromoEntity> getPromos() {
        return promos;
    }

    public String getLink() {
        return link;
    }

    public String getLinkPay() {
        return linkPay;
    }

    public Integer getStatus() {
        return status;
    }
}
