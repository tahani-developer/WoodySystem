package com.falconssoft.woodysystem.models;

import android.graphics.Bitmap;

import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

public class NewRowInfo implements Serializable {
//{"DETAILS_LIST":[{"TTN_NO":"335","DATE_OF_ACCEPTANCE":"17\/06\/2020","THICKNESS":"19.0","WIDTH":"75.0","LENGTH":"3650.0",
// "PIECES":"132.0",
// "NO_BUNDLES":"1","REJECTED":"0","SUPLIER":"Paleta Ukraina","TRUCK_NO":"CB 6722 AK","GRADE":"Fresh","TRUCKCMB":".687","CBMREJ":"0"
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

    @SerializedName("CUBIC_REJ")
    private double cubicRej;

    @SerializedName("TOTAL")
    private double totalCubic;// this used to get cubic summation without using any filter

    @SerializedName("TOTAL_REJ")
    private double totalCubicRej;// this used to get cubic summation without using any filter

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

    @SerializedName("PIC1")
    private String imageOne;

    @SerializedName("PIC2")
    private String imageTwo;

    @SerializedName("PIC3")
    private String imageThree;

    @SerializedName("PIC4")
    private String imageFour;

    @SerializedName("PIC5")
    private String imageFive;

    @SerializedName("PIC6")
    private String imageSix;

    @SerializedName("PIC7")
    private String imageSeven;

    @SerializedName("PIC8")
    private String imageEight;


    @SerializedName("PIC9")
    private String image9;

    @SerializedName("PIC10")
    private String image10;

    @SerializedName("PIC11")
    private String image11;

    @SerializedName("PIC12")
    private String image12;

    @SerializedName("PIC13")
    private String image13;

    @SerializedName("PIC14")
    private String image14;

    @SerializedName("PIC15")
    private String image15;

    @SerializedName("CBMREJ")
    private  String CbmRej;

   // @SerializedName("CbmAccept")
    private String CbmAccept;

   @SerializedName("TRUCKCMB")
    private String truckCMB;

    @SerializedName("PRICE")
   private double price;

    @SerializedName("PRICE_CASH")
    private double cash;


    private  double debt$;
    private  double cash$;
    private Bitmap pic11;
    private Bitmap pic22;
    private Bitmap pic33;
    private Bitmap pic44;
    private Bitmap pic55;
    private Bitmap pic66;
    private Bitmap pic77;
    private Bitmap pic88;

    private Bitmap pic99;
    private Bitmap pic1010;
    private Bitmap pic1111;
    private Bitmap pic1212;
    private Bitmap pic1313;
    private Bitmap pic1414;
    private Bitmap pic1515;

    private int remove;

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

    public double getTotalCubic() {
        return totalCubic;
    }

    public double getTotalCubicRej() {
        return totalCubicRej;
    }

    public void setTotalCubic(double totalCubic) {
        this.totalCubic = totalCubic;
    }

    public void setTotalCubicRej(double totalCubic) {
        this.totalCubicRej = totalCubic;
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

    public double getCubicRej() {
        return cubicRej;
    }

    public void setCubic(double cubic) {
        this.cubic = cubic;
    }

    public void setCubicRej(double cubicRej) {
        this.cubicRej = cubicRej;
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

    public boolean isChecked() {
        return checked;
    }

    public String getImageOne() {
        return imageOne;
    }

    public void setImageOne(String imageOne) {
        this.imageOne = imageOne;
    }

    public String getImageTwo() {
        return imageTwo;
    }

    public void setImageTwo(String imageTwo) {
        this.imageTwo = imageTwo;
    }

    public String getImageThree() {
        return imageThree;
    }

    public void setImageThree(String imageThree) {
        this.imageThree = imageThree;
    }

    public String getImageFour() {
        return imageFour;
    }

    public void setImageFour(String imageFour) {
        this.imageFour = imageFour;
    }

    public String getImageFive() {
        return imageFive;
    }

    public void setImageFive(String imageFive) {
        this.imageFive = imageFive;
    }

    public String getImageSix() {
        return imageSix;
    }

    public void setImageSix(String imageSix) {
        this.imageSix = imageSix;
    }

    public String getImageSeven() {
        return imageSeven;
    }

    public void setImageSeven(String imageSeven) {
        this.imageSeven = imageSeven;
    }

    public String getImageEight() {
        return imageEight;
    }

    public void setImageEight(String imageEight) {
        this.imageEight = imageEight;
    }

    public String getImage9() {
        return image9;
    }

    public void setImage9(String image9) {
        this.image9 = image9;
    }

    public String getImage10() {
        return image10;
    }

    public void setImage10(String image10) {
        this.image10 = image10;
    }

    public String getImage11() {
        return image11;
    }

    public void setImage11(String image11) {
        this.image11 = image11;
    }

    public String getImage12() {
        return image12;
    }

    public void setImage12(String image12) {
        this.image12 = image12;
    }

    public String getImage13() {
        return image13;
    }

    public void setImage13(String image13) {
        this.image13 = image13;
    }

    public String getImage14() {
        return image14;
    }

    public void setImage14(String image14) {
        this.image14 = image14;
    }

    public String getImage15() {
        return image15;
    }

    public Bitmap getPic99() {
        return pic99;
    }

    public void setPic99(Bitmap pic99) {
        this.pic99 = pic99;
    }

    public Bitmap getPic1010() {
        return pic1010;
    }

    public void setPic1010(Bitmap pic1010) {
        this.pic1010 = pic1010;
    }

    public Bitmap getPic1111() {
        return pic1111;
    }

    public void setPic1111(Bitmap pic1111) {
        this.pic1111 = pic1111;
    }

    public Bitmap getPic1212() {
        return pic1212;
    }

    public void setPic1212(Bitmap pic1212) {
        this.pic1212 = pic1212;
    }

    public Bitmap getPic1313() {
        return pic1313;
    }

    public void setPic1313(Bitmap pic1313) {
        this.pic1313 = pic1313;
    }

    public Bitmap getPic1414() {
        return pic1414;
    }

    public void setPic1414(Bitmap pic1414) {
        this.pic1414 = pic1414;
    }

    public Bitmap getPic1515() {
        return pic1515;
    }

    public void setPic1515(Bitmap pic1515) {
        this.pic1515 = pic1515;
    }

    public void setImage15(String image15) {
        this.image15 = image15;
    }

    public int getRemove() {
        return remove;
    }

    public void setRemove(int remove) {
        this.remove = remove;
    }

    public String getCbmRej() {
        return CbmRej;
    }

    public void setCbmRej(String cbmRej) {
        CbmRej = cbmRej;
    }

    public String getCbmAccept() {
        return CbmAccept;
    }

    public void setCbmAccept(String cbmAccept) {
        CbmAccept = cbmAccept;
    }

    public String getTruckCMB() {
        return truckCMB;
    }

    public void setTruckCMB(String truckCMB) {
        this.truckCMB = truckCMB;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getDebt$() {
        return debt$;
    }

    public void setDebt$(double debt$) {
        this.debt$ = debt$;
    }

    public double getCash$() {
        return cash$;
    }

    public void setCash$(double cash$) {
        this.cash$ = cash$;
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

            jsonObject.put("PIC1", "'" +imageOne+ "'");
            jsonObject.put("PIC2", "'" +imageTwo+ "'");
            jsonObject.put("PIC3", "'" +imageThree+ "'");
            jsonObject.put("PIC4","'" + imageFour+ "'");
            jsonObject.put("PIC5", "'" +imageFive+ "'");
            jsonObject.put("PIC6", "'" +imageSix+ "'");
            jsonObject.put("PIC7", "'" +imageSeven+ "'");
            jsonObject.put("PIC8", "'" +imageEight+ "'");
            jsonObject.put("PIC9", "'" +image9+ "'");
            jsonObject.put("PIC10", "'" +image10+ "'");
            jsonObject.put("PIC11", "'" +image11+ "'");
            jsonObject.put("PIC12", "'" +image12+ "'");
            jsonObject.put("PIC13", "'" +image13+ "'");
            jsonObject.put("PIC14", "'" +image14+ "'");
            jsonObject.put("PIC15", "'" +image15+ "'");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

}
