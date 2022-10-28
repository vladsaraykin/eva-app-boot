package com.quatex.evaproxy.entity;

import org.springframework.lang.NonNull;

public class PromoEntity {

    private final String tittle;
    private final String subtitle;

    @NonNull
    private final String code;

    public PromoEntity(String tittle, String subtitle, String code) {
        this.tittle = tittle;
        this.subtitle = subtitle;
        this.code = code;
    }

    public String getTittle() {
        return tittle;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCode() {
        return code;
    }
}
