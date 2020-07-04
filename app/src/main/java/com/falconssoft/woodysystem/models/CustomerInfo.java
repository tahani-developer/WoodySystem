package com.falconssoft.woodysystem.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomerInfo {

    private String custNo;
    private String custName;

    public CustomerInfo(String custNo, String custName) {
        this.custNo = custNo;
        this.custName = custName;
    }

    public CustomerInfo() {

    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("CUSTOMER_NO", "'" + custNo + "'");
            obj.put("CUSTOMER_NAME", "'" +custName+ "'");


        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }
}
