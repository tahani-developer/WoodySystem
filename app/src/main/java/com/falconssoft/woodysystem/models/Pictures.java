package com.falconssoft.woodysystem.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Pictures {

    String orderNo;
    String pic1;
    String pic2;
    String pic3;
    String pic4;
    String pic5;
    String pic6;
    String pic7;
    String pic8;

    public Pictures(String orderNo, String pic1, String pic2, String pic3, String pic4, String pic5, String pic6, String pic7, String pic8) {
        this.orderNo = orderNo;
        this.pic1 = pic1;
        this.pic2 = pic2;
        this.pic3 = pic3;
        this.pic4 = pic4;
        this.pic5 = pic5;
        this.pic6 = pic6;
        this.pic7 = pic7;
        this.pic8 = pic8;
    }

    public Pictures() {

    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPic1() {
        return pic1;
    }

    public void setPic1(String pic1) {
        this.pic1 = pic1;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }

    public String getPic3() {
        return pic3;
    }

    public void setPic3(String pic3) {
        this.pic3 = pic3;
    }

    public String getPic4() {
        return pic4;
    }

    public void setPic4(String pic4) {
        this.pic4 = pic4;
    }

    public String getPic5() {
        return pic5;
    }

    public void setPic5(String pic5) {
        this.pic5 = pic5;
    }

    public String getPic6() {
        return pic6;
    }

    public void setPic6(String pic6) {
        this.pic6 = pic6;
    }

    public String getPic7() {
        return pic7;
    }

    public void setPic7(String pic7) {
        this.pic7 = pic7;
    }

    public String getPic8() {
        return pic8;
    }

    public void setPic8(String pic8) {
        this.pic8 = pic8;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("ORDER_NO", "'" + orderNo + "'");
            obj.put("PIC1", "'" +pic1+ "'");
            obj.put("PIC2", "'" +pic2+ "'");
            obj.put("PIC3", "'" +pic3+ "'");
            obj.put("PIC4","'" + pic4+ "'");
            obj.put("PIC5", "'" +pic5+ "'");
            obj.put("PIC6", "'" +pic6+ "'");
            obj.put("PIC7", "'" +pic7+ "'");
            obj.put("PIC8", "'" +pic8+ "'");

        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }
}
