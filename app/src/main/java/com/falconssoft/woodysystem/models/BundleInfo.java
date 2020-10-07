package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class BundleInfo implements Serializable {

    private double thickness;
    private double length;
    private double width;
    private String grade;
    private double noOfPieces;
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
    private int isPrinted;
    private String description;
    private String serialNo;
    private String userNo;
    private String backingList;
    List<String> bundleNoString;
    private String foucoseColor="0";
    private Bitmap picBitmap;
    private String customer="";
    private int noOfExist;
    private int noOfCopies;
    private int index;

    public BundleInfo() {
    }

    public BundleInfo(double thickness, double length, double width, String grade, double noOfPieces, String bundleNo, String location, String area, String barcode, String pic, Bitmap picBitmap) {
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
        this.picBitmap = picBitmap;
    }

    public BundleInfo(double thickness, double length, double width, String grade, double noOfPieces, String bundleNo, String location, String area, String addingDate, int isPrinted, String description, String serialNo, String userNo, String backingList, int ordered) {
        this.thickness = thickness;
        this.length = length;
        this.width = width;
        this.grade = grade;
        this.noOfPieces = noOfPieces;
        this.bundleNo = bundleNo;
        this.location = location;
        this.area = area;
        this.addingDate = addingDate;
        this.isPrinted = isPrinted;
        this.description = description;
        this.serialNo = serialNo;
        this.userNo = userNo;
        this.backingList = backingList;
        this.ordered = ordered;
        this.customer = customer;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getNoOfCopies() {
        return noOfCopies;
    }

    public void setNoOfCopies(int noOfCopies) {
        this.noOfCopies = noOfCopies;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getBackingList() {
        return backingList;
    }

    public void setBackingList(String backingList) {
        this.backingList = backingList;
    }

    public String getUserNo() {
        return userNo;
    }

    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public int getIsPrinted() {
        return isPrinted;
    }

    public void setIsPrinted(int isPrinted) {
        this.isPrinted = isPrinted;
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

    public double getNoOfPieces() {
        return noOfPieces;
    }

    public void setNoOfPieces(double noOfPieces) {
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

    public Bitmap getPicBitmap() {
        return picBitmap;
    }

    public void setPicBitmap(Bitmap picBitmap) {
        this.picBitmap = picBitmap;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNoOfExist() {
        return noOfExist;
    }

    public void setNoOfExist(int noOfExist) {
        this.noOfExist = noOfExist;
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
            obj.put("DESCRIPTION", "'" + description + "'");
            obj.put("B_SERIAL", "'" + serialNo + "'");
            obj.put("USER_NO", "'" + userNo + "'");
            obj.put("IS_PRINTED", "'" + isPrinted + "'");
            obj.put("BACKING_LIST", "'" + backingList + "'");
//            obj.put("CUSTOMER", "'" + customer + "'");

        } catch (JSONException e) {
            Log.e("Tag", "JSONException");
        }
        return obj;
    }

    public String getFoucoseColor() {
        return foucoseColor;
    }

    public void setFoucoseColor(String foucoseColor) {
        this.foucoseColor = foucoseColor;
    }
}
