package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class BundleInfo {

    private double thickness;
    private double length;
    private double width;
    private String grade;
    private int noOfPieces;
    private String bundleNo;
    private String location;
    private String area;
    private String addingDate;
    private int placingNo;
    private int orderNo;
    private int containerNo;
    private String dateOfLoad;
    private boolean checked;
    private String barcode;
    private String picture;
    private int ordered; // 0 => order not done, 1 => order done
    private String hideFlag;// 0 => show, 1 => hide

    public BundleInfo() {
    }

    public BundleInfo(double thickness, double length, double width, String grade, int noOfPieces, String bundleNo, String location, String area, String barcode, String pic) {
        this.thickness = thickness;
        this.length = length;
        this.width = width;
        this.grade = grade;
        this.noOfPieces = noOfPieces;
        this.bundleNo = bundleNo;
        this.location = location;
        this.area = area;
        this.barcode = barcode;
        this.picture = pic;
    }

    public BundleInfo(double thickness, double length, double width, String grade, int noOfPieces, String bundleNo, String location, String area, String barcode, String pic, String hideFlag) {
        this.thickness = thickness;
        this.length = length;
        this.width = width;
        this.grade = grade;
        this.noOfPieces = noOfPieces;
        this.bundleNo = bundleNo;
        this.location = location;
        this.area = area;
        this.barcode = barcode;
        this.picture = pic;
        this.hideFlag = hideFlag;
    }

    public BundleInfo(double thickness, double length, double width, String grade, int noOfPieces, String bundleNo, String location, String area, String addingDate) {
        this.thickness = thickness;
        this.length = length;
        this.width = width;
        this.grade = grade;
        this.noOfPieces = noOfPieces;
        this.bundleNo = bundleNo;
        this.location = location;
        this.area = area;
        this.addingDate = addingDate;
    }

    public String getAddingDate() {
        return addingDate;
    }

    public void setAddingDate(String addingDate) {
        this.addingDate = addingDate;
    }

    public String getDateOfLoad() {
        return dateOfLoad;
    }

    public void setDateOfLoad(String dateOfLoad) {
        this.dateOfLoad = dateOfLoad;
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

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public int getOrdered() {
        return ordered;
    }

    public void setOrdered(int ordered) {
        this.ordered = ordered;
    }

    public String getHideFlag() {
        return hideFlag;
    }

    public void setHideFlag(String hideFlag) {
        this.hideFlag = hideFlag;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("THICKNESS", "'" + thickness + "'");
            obj.put("WIDTH", "'" + width + "'");
            obj.put("LENGTH", "'" + length + "'");
            obj.put("GRADE", "'" + grade + "'");
            obj.put("PIECES", "'" + noOfPieces + "'");
            obj.put("BUNDLE_NO", "'" + bundleNo + "'");
            obj.put("LOCATION", "'" + location + "'");
            obj.put("AREA", "'" + area + "'");
            obj.put("BARCODE", "'" + barcode + "'");
            obj.put("ORDERED", "'" + ordered + "'");
            obj.put("BUNDLE_DATE", "'" + addingDate + "'");

        } catch (JSONException e) {
            Log.e("Tag", "JSONException");
        }
        return obj;
    }


}
