package com.quatex.evaproxy.entity;

public class SettingEntity {

    private ManageEntity<String> link;
    private ManageEntity<Integer> enabled;
    private Integer version;
    private String linkCryptoPay;

    public SettingEntity() {
    }

    public SettingEntity(ManageEntity<String> link, ManageEntity<Integer> enabled, Integer version, String linkCryptoPay) {
        this.link = link;
        this.enabled = enabled;
        this.version = version;
        this.linkCryptoPay = linkCryptoPay;
    }

    public String getLinkCryptoPay() {
        return linkCryptoPay;
    }

    public ManageEntity<String> getLink() {
        return link;
    }

    public void setLink(ManageEntity<String> link) {
        this.link = link;
    }

    public ManageEntity<Integer> getEnabled() {
        return enabled;
    }

    public void setEnabled(ManageEntity<Integer> enabled) {
        this.enabled = enabled;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void setLinkCryptoPay(String linkCryptoPay) {
        this.linkCryptoPay = linkCryptoPay;
    }
}
