package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

public class Pictures {

    //     picture.setOrderNo(finalObject.getString("ORDER_NO"));
//                        picture.setPic1(finalObject.getString("PIC1"));
//                        picture.setPic2(finalObject.getString("PIC2"));
//                        picture.setPic3(finalObject.getString("PIC3"));
//                        picture.setPic4(finalObject.getString("PIC4"));
//                        picture.setPic5(finalObject.getString("PIC5"));
//                        picture.setPic6(finalObject.getString("PIC6"));
//                        picture.setPic7(finalObject.getString("PIC7"));
//                        picture.setPic8(finalObject.getString("PIC8"));

    @SerializedName("ORDER_NO")
    String orderNo;

    @SerializedName("PIC1")
    String pic1;

    @SerializedName("PIC2")
    String pic2;

    @SerializedName("PIC3")
    String pic3;

    @SerializedName("PIC4")
    String pic4;

    @SerializedName("PIC5")
    String pic5;

    @SerializedName("PIC6")
    String pic6;

    @SerializedName("PIC7")
    String pic7;

    @SerializedName("PIC8")
    String pic8;

    Bitmap pic11;
    Bitmap pic22;
    Bitmap pic33;
    Bitmap pic44;
    Bitmap pic55;
    Bitmap pic66;
    Bitmap pic77;
    Bitmap pic88;

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

    public Bitmap getPic11() {
        return pic11;
    }

    public void setPic11(Bitmap pic11) {
        this.pic11 = pic11;
    }

    public Bitmap getPic22() {
        return pic22;
    }

    public void setPic22(Bitmap pic22) {
        this.pic22 = pic22;
    }

    public Bitmap getPic33() {
        return pic33;
    }

    public void setPic33(Bitmap pic33) {
        this.pic33 = pic33;
    }

    public Bitmap getPic44() {
        return pic44;
    }

    public void setPic44(Bitmap pic44) {
        this.pic44 = pic44;
    }

    public Bitmap getPic55() {
        return pic55;
    }

    public void setPic55(Bitmap pic55) {
        this.pic55 = pic55;
    }

    public Bitmap getPic66() {
        return pic66;
    }

    public void setPic66(Bitmap pic66) {
        this.pic66 = pic66;
    }

    public Bitmap getPic77() {
        return pic77;
    }

    public void setPic77(Bitmap pic77) {
        this.pic77 = pic77;
    }

    public Bitmap getPic88() {
        return pic88;
    }

    public void setPic88(Bitmap pic88) {
        this.pic88 = pic88;
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
