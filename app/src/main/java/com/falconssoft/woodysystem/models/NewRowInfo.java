package com.falconssoft.woodysystem.models;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class NewRowInfo implements Serializable {

    @SerializedName("RAW_INFO_MASTER")
    private List<NewRowInfo> master;

    @SerializedName("RAW_INFO_MIX")
    private List<NewRowInfo> details;

    @SerializedName("LOCATION_LIST")
    private List<NewRowInfo> locationList;// locationOfAcceptance

//    @SerializedName("CUBIC_LIST")
//    private List<NewRowInfo> cubicList;// serial and cubic

    @SerializedName("DETAILS_LIST")
    private List<NewRowInfo> detailsList;// get details based on serial

    @SerializedName("SUPLIER")
    private String supplierName;

    @SerializedName("THICKNESS")
    private double thickness;

    @SerializedName("WIDTH")
    private double width;

    @SerializedName("LENGTH")
    private double length;

    @SerializedName("PIECES")
    private double noOfPieces;

    @SerializedName("REJ")
    private double noOfRejected;

    @SerializedName("NO_BUNDLES")
    private double noOfBundles;

    @SerializedName("GRADE")
    private String grade;

    private boolean checked;

    @SerializedName("SERIAL")
    private String serial;

    @SerializedName("CUBIC")
    private double cubic;

    @SerializedName("TOTAL")
    private double totalCubic;// this used to get cubic summation without using any filter

    @SerializedName("TRUCK_NO")
    private String truckNo;

    @SerializedName("DATE_OF_ACCEPTANCE")
    private String date;

    @SerializedName("NAME_OF_ACCEPTER")
    private String acceptedPersonName;

    @SerializedName("LOCATION_OF_ACCEPTANCE")
    private String locationOfAcceptance;

    @SerializedName("TTN_NO")
    private String ttnNo;

    @SerializedName("REJECTED")
    private String totalRejectedNo;

    @SerializedName("NET_BUNDLES")
    private String netBundles;

    public NewRowInfo() {
    }

    public NewRowInfo(String supplierName, double thickness, double width, double length, double noOfPieces, double noOfRejected, double noOfBundles, String grade, String truckNo, String serial) {
        this.supplierName = supplierName;
        this.thickness = thickness;
        this.width = width;
        this.length = length;
        this.noOfPieces = noOfPieces;
        this.noOfRejected = noOfRejected;
        this.noOfBundles = noOfBundles;
        this.grade = grade;
        this.truckNo = truckNo;
        this.serial = serial;

    }

    public double getTotalCubic() {
        return totalCubic;
    }

    public void setTotalCubic(double totalCubic) {
        this.totalCubic = totalCubic;
    }

    public List<NewRowInfo> getDetailsList() {
        return detailsList;
    }

    public void setDetailsList(List<NewRowInfo> detailsList) {
        this.detailsList = detailsList;
    }

    public List<NewRowInfo> getLocationList() {
        return locationList;
    }

    public void setLocationList(List<NewRowInfo> locationList) {
        this.locationList = locationList;
    }

    public List<NewRowInfo> getMaster() {
        return master;
    }

    public void setMaster(List<NewRowInfo> master) {
        this.master = master;
    }

    public List<NewRowInfo> getDetails() {
        return details;
    }

    public void setDetails(List<NewRowInfo> details) {
        this.details = details;
    }

    public double getCubic() {
        return cubic;
    }

    public void setCubic(double cubic) {
        this.cubic = cubic;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public boolean getChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
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

    public double getNoOfRejected() {
        return noOfRejected;
    }

    public void setNoOfRejected(double noOfRejected) {
        this.noOfRejected = noOfRejected;
    }

    public double getNoOfBundles() {
        return noOfBundles;
    }

    public void setNoOfBundles(double noOfBundles) {
        this.noOfBundles = noOfBundles;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTruckNo() {
        return truckNo;
    }

    public void setTruckNo(String truckNo) {
        this.truckNo = truckNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAcceptedPersonName() {
        return acceptedPersonName;
    }

    public void setAcceptedPersonName(String acceptedPersonName) {
        this.acceptedPersonName = acceptedPersonName;
    }

    public String getLocationOfAcceptance() {
        return locationOfAcceptance;
    }

    public void setLocationOfAcceptance(String locationOfAcceptance) {
        this.locationOfAcceptance = locationOfAcceptance;
    }

    public String getTtnNo() {
        return ttnNo;
    }

    public void setTtnNo(String ttnNo) {
        this.ttnNo = ttnNo;
    }

    public String getTotalRejectedNo() {
        return totalRejectedNo;
    }

    public void setTotalRejectedNo(String totalRejectedNo) {
        this.totalRejectedNo = totalRejectedNo;
    }

    public String getNetBundles() {
        return netBundles;
    }

    public void setNetBundles(String netBundles) {
        this.netBundles = netBundles;
    }

    public JSONObject getJsonData() {

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("SUPLIER", "'" + supplierName + "'");
            jsonObject.put("TRUCK_NO", "'" + truckNo + "'");
            jsonObject.put("THICKNESS", "'" + thickness + "'");
            jsonObject.put("WIDTH", "'" + width + "'");
            jsonObject.put("LENGTH", "'" + length + "'");
            jsonObject.put("PIECES", "'" + noOfPieces + "'");
            jsonObject.put("REJECTED", "'" + noOfRejected + "'");
            jsonObject.put("NO_BUNDLES", "'" + noOfBundles + "'");
            jsonObject.put("GRADE", "'" + grade + "'");
            jsonObject.put("SERIAL", "'" + serial + "'");


            jsonObject.put("DATE_OF_ACCEPTANCE", "'" + date + "'");
            jsonObject.put("NAME_OF_ACCEPTER", "'" + acceptedPersonName + "'");
            jsonObject.put("LOCATION_OF_ACCEPTANCE", "'" + locationOfAcceptance + "'");
            jsonObject.put("TTN_NO", "'" + ttnNo + "'");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public JSONObject getJsonDataMaster() {

        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.put("TRUCK_NO", "'" + truckNo + "'");
            jsonObject.put("DATE_OF_ACCEPTANCE", "'" + date + "'");
            jsonObject.put("NAME_OF_ACCEPTER", "'" + acceptedPersonName + "'");
            jsonObject.put("LOCATION_OF_ACCEPTANCE", "'" + locationOfAcceptance + "'");
            jsonObject.put("TTN_NO", "'" + ttnNo + "'");
            jsonObject.put("REJECTED", "'" + totalRejectedNo + "'");
            jsonObject.put("SERIAL", "'" + serial + "'");
            jsonObject.put("NO_BUNDLES", "'" + netBundles + "'");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
