package com.falconssoft.woodysystem.models;

public class Settings {

    private String companyName;
    private String ipAddress;
    private String store;
    private String userNo;


    public Settings() {
    }

    public Settings(String companyName, String ipAddress, String store) {
        this.companyName = companyName;
        this.ipAddress = ipAddress;
        this.store = store;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }
}
