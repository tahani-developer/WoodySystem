package com.falconssoft.woodysystem.models;

public class Orders {

    private double thickness;
    private double length;
    private double width;
    private String grade;
    private int noOfPieces;
    private String bundleNo;
    private String location;
    private String area;
    private int placingNo;
    private int orderNo;
    private int containerNo;
    private String dateOfLoad;

    public Orders() {
    }

    public Orders(double thickness, double length, double width, String grade, int noOfPieces, String bundleNo, String location, String area
            , int placingNo, int orderNo, int containerNo, String dateOfLoad) {
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

    public int getPlacingNo() {
        return placingNo;
    }

    public void setPlacingNo(int placingNo) {
        this.placingNo = placingNo;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(int orderNo) {
        this.orderNo = orderNo;
    }

    public int getContainerNo() {
        return containerNo;
    }

    public void setContainerNo(int containerNo) {
        this.containerNo = containerNo;
    }

    public String getDateOfLoad() {
        return dateOfLoad;
    }

    public void setDateOfLoad(String dateOfLoad) {
        this.dateOfLoad = dateOfLoad;
    }
}
