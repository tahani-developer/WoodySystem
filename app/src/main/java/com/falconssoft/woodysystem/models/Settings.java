package com.falconssoft.woodysystem.models;

public class Settings {

    private String companyName;

    private String ipAddress;

    private String store;

    public Settings() {
    }

    public Settings(String companyName, String ipAddress, String store) {
        this.companyName = companyName;
        this.ipAddress = ipAddress;
        this.store = store;
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
