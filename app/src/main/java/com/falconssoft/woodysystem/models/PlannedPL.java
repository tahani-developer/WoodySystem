package com.falconssoft.woodysystem.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class PlannedPL {
    private String custName;
    private String custNo;
    private String packingList;
    private String destination;
    private double thickness;
    private double width;
    private double length;
    private double noOfPieces;
    private String date;
    private boolean exist;

    public PlannedPL(String custName, String custNo, String packingList, String destination, double thickness, double width, double length, double noOfPieces, String date) {
        this.custName = custName;
        this.custNo = custNo;
        this.packingList = packingList;
        this.destination = destination;
        this.thickness = thickness;
        this.width = width;
        this.length = length;
        this.noOfPieces = noOfPieces;
        this.date = date;
    }

    public PlannedPL() {

    }

    public String getCustName() {
        return custName;
    }

    public void setCustName(String custName) {
        this.custName = custName;
    }

    public String getCustNo() {
        return custNo;
    }

    public void setCustNo(String custNo) {
        this.custNo = custNo;
    }

    public String getPackingList() {
        return packingList;
    }

    public void setPackingList(String packingList) {
        this.packingList = packingList;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public double getThickness() {
        return thickness;
    }

    public void setThickness(double thickness) {
        this.thickness = thickness;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public double getNoOfPieces() {
        return noOfPieces;
    }

    public void setNoOfPieces(double noOfPieces) {
        this.noOfPieces = noOfPieces;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean getExist() {
        return exist;
    }

    public void setExist(boolean exist) {
        this.exist = exist;
    }

    public JSONObject getJSONObject() {
        JSONObject obj = new JSONObject();

        try {
            obj.put("CUST_NAME", "'" + custName + "'");
            obj.put("CUST_NO", "'" + custNo + "'");
            obj.put("PACKING_LIST", "'" + packingList + "'");
            obj.put("DESTINATION", "'" + destination + "'");
            obj.put("THICKNESS", "'" + thickness + "'");
            obj.put("WIDTH", "'" + width + "'");
            obj.put("LENGTH", "'" + length + "'");
            obj.put("PIECES", "'" + noOfPieces + "'");
            obj.put("DATE", "'" + date + "'");


        } catch (JSONException e) {
            Log.e("Tag" , "JSONException");
        }
        return obj;
    }
}
