package com.falconssoft.woodysystem.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class SupplierInfo {

    private String supplierNo;
    private String supplierName;
    private String startCash;
    private String startBank;

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

    public String getStartCash() {
        return startCash;
    }

    public void setStartCash(String startCash) {
        this.startCash = startCash;
    }

    public String getStartBank() {
        return startBank;
    }

    public void setStartBank(String startBank) {
        this.startBank = startBank;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("SUPPLIER_NO", "'" + supplierNo + "'");
            obj.put("SUPPLIER_NAME", "'" +supplierName+ "'");


        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }

}
