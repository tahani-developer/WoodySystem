package com.falconssoft.woodysystem.models;

public class SupplierInfo {

    private String supplierNo;
    private String supplierName;

    public SupplierInfo() {
    }

    public SupplierInfo(String supplierNo, String supplierName) {
        this.supplierNo = supplierNo;
        this.supplierName = supplierName;
    }

    public String getSupplierNo() {
        return supplierNo;
    }

    public void setSupplierNo(String supplierNo) {
        this.supplierNo = supplierNo;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }
}
