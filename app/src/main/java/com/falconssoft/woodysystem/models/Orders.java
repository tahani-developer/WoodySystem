package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class Orders implements Serializable {

    private Double thickness;
    private Double length;
    private Double width;
    private String grade;
    private Double noOfPieces;
    private String bundleNo;
    private String location;
    private String area;
    private String placingNo;
    private String orderNo;
    private String containerNo;
    private String dateOfLoad;
    private String destination;
    private String picture;
    private boolean checked;
    private Bitmap picBitmap;
    private String packingList;
    private String customer;

    public Orders() {
    }

    public Orders(Double thickness,  Double width ,Double length, String grade, Double noOfPieces, String bundleNo, String location, String area
            , String placingNo, String orderNo, String containerNo, String dateOfLoad, String destination, String picture, String packingList, String customer) {
        this.thickness = thickness;
        this.length = length;
        this.width = width;
        this.grade = grade;
        this.noOfPieces = noOfPieces;
        this.bundleNo = bundleNo;
        this.location = location;
        this.area = area;
        this.placingNo = placingNo;
        this.orderNo = orderNo;
        this.containerNo = containerNo;
        this.dateOfLoad = dateOfLoad;
        this.destination = destination;
        this.picture = picture;
        this.packingList = packingList;
        this.customer = customer;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public Double getThickness() {
        return thickness;
    }

    public void setThickness(Double thickness) {
        this.thickness = thickness;
    }

    public Double getLength() {
        return length;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public Double getWidth() {
        return width;
    }

    public void setWidth(Double width) {
        this.width = width;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Double getNoOfPieces() {
        return noOfPieces;
    }

    public void setNoOfPieces(Double noOfPieces) {
        this.noOfPieces = noOfPieces;
    }

    public String getBundleNo() {
        return bundleNo;
    }

    public void setBundleNo(String bundleNo) {
        this.bundleNo = bundleNo;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getPlacingNo() {
        return placingNo;
    }

    public void setPlacingNo(String placingNo) {
        this.placingNo = placingNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(String containerNo) {
        this.containerNo = containerNo;
    }

    public String getDateOfLoad() {
        return dateOfLoad;
    }

    public void setDateOfLoad(String dateOfLoad) {
        this.dateOfLoad = dateOfLoad;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Bitmap getPicBitmap() {
        return picBitmap;
    }

    public void setPicBitmap(Bitmap picBitmap) {
        this.picBitmap = picBitmap;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("THICKNESS", "'" + thickness + "'");
            obj.put("WIDTH", "'" +width+ "'");
            obj.put("LENGTH", "'" +length+ "'");
            obj.put("GRADE", "'" +grade+ "'");
            obj.put("PIECES","'" + noOfPieces+ "'");
            obj.put("BUNDLE_NO", "'" +bundleNo+ "'");
            obj.put("LOCATION", "'" +location+ "'");
            obj.put("AREA", "'" +area+ "'");
            obj.put("PLACING_NO", "'" +placingNo+ "'");
            obj.put("ORDER_NO", "'" +orderNo+ "'");
            obj.put("CONTAINER_NO", "'" +containerNo+ "'");
            obj.put("DATE_OF_LOAD", "'" +dateOfLoad+ "'");
            obj.put("DESTINATION", "'" +destination+ "'");
            obj.put("PIC", "'" +picture+ "'");
            obj.put("PACKING_LIST", "'" +packingList+ "'");
            obj.put("CUSTOMER", "'" +customer+ "'");


        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Orders)
        {
            Orders temp = (Orders) obj;
            if(this.orderNo.equals(temp.orderNo))
                return true;
        }
        return false;

    }
    @Override
    public int hashCode() {
        // TODO Auto-generated method stub

        return (this.orderNo.hashCode());
    }
}
