package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;

public class Pictures {

    String orderNo;
    Bitmap pic1;
    Bitmap pic2;
    Bitmap pic3;
    Bitmap pic4;
    Bitmap pic5;
    Bitmap pic6;
    Bitmap pic7;
    Bitmap pic8;

    public Pictures(String orderNo, Bitmap pic1, Bitmap pic2, Bitmap pic3, Bitmap pic4, Bitmap pic5, Bitmap pic6, Bitmap pic7, Bitmap pic8) {
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

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Bitmap getPic1() {
        return pic1;
    }

    public void setPic1(Bitmap pic1) {
        this.pic1 = pic1;
    }

    public Bitmap getPic2() {
        return pic2;
    }

    public void setPic2(Bitmap pic2) {
        this.pic2 = pic2;
    }

    public Bitmap getPic3() {
        return pic3;
    }

    public void setPic3(Bitmap pic3) {
        this.pic3 = pic3;
    }

    public Bitmap getPic4() {
        return pic4;
    }

    public void setPic4(Bitmap pic4) {
        this.pic4 = pic4;
    }

    public Bitmap getPic5() {
        return pic5;
    }

    public void setPic5(Bitmap pic5) {
        this.pic5 = pic5;
    }

    public Bitmap getPic6() {
        return pic6;
    }

    public void setPic6(Bitmap pic6) {
        this.pic6 = pic6;
    }

    public Bitmap getPic7() {
        return pic7;
    }

    public void setPic7(Bitmap pic7) {
        this.pic7 = pic7;
    }

    public Bitmap getPic8() {
        return pic8;
    }

    public void setPic8(Bitmap pic8) {
        this.pic8 = pic8;
    }
}
