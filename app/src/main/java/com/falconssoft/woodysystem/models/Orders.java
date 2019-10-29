package com.falconssoft.woodysystem.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class Orders {

    private double thickness;
    private double length;
    private double width;
    private String grade;
    private int noOfPieces;
    private String bundleNo;
    private String location;
    private String area;
    private String placingNo;
    private String orderNo;
    private String containerNo;
    private String dateOfLoad;
    private String destination;

    public Orders() {
    }

    public Orders(double thickness, double length, double width, String grade, int noOfPieces, String bundleNo, String location, String area
            , String placingNo, String orderNo, String containerNo, String dateOfLoad, String destination) {
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
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public int getNoOfPieces() {
        return noOfPieces;
    }

    public void setNoOfPieces(int noOfPieces) {
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


        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }
}
