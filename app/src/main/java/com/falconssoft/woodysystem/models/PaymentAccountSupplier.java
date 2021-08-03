package com.falconssoft.woodysystem.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PaymentAccountSupplier {

    //{"DETAILS_LIST":[{"SUPLIER":"Fortune","DATE_OF_PAYMENT":"31\/07\/2021","VALUE_OF_PAYMENT":"45","INVOICE_NO":"kk",
    // "PAYER":"kk","PAYMENT_TYPE":"1","ACCEPTANCE_DATE":"12\/5\/2525","TOTAL_CASH":"0","TOTAL_BANK":"0","SERIAL":"3","START_BALANCE":"0"}]}

    @SerializedName("SUPLIER")
    private  String SUPLIER;

    @SerializedName("DATE_OF_PAYMENT")
    private  String DATE_OF_PAYMENT;

    @SerializedName("VALUE_OF_PAYMENT")
    private  String VALUE_OF_PAYMENT;

    @SerializedName("INVOICE_NO")
    private  String INVOICE_NO;

    @SerializedName("PAYER")
    private  String PAYER;

    @SerializedName("PAYMENT_TYPE")
    private  String PAYMENT_TYPE;

    @SerializedName("ACCEPTANCE_DATE")
    private  String ACCEPTANCE_DATE;

    @SerializedName("TOTAL_CASH")
    private  String TOTAL_CASH;

    @SerializedName("TOTAL_BANK")
    private  String TOTAL_BANK;

    @SerializedName("SERIAL")
    private  String SERIAL;

    @SerializedName("START_BALANCE")
    private  String START_BALANCE;

    @SerializedName("DETAILS_LIST")
    private List<PaymentAccountSupplier> DETAILS_LIST;


    public PaymentAccountSupplier(String SUPLIER, String DATE_OF_PAYMENT, String VALUE_OF_PAYMENT, String INVOICE_NO, String PAYER, String PAYMENT_TYPE, String ACCEPTANCE_DATE,
                                  String TOTAL_CASH, String TOTAL_BANK, String SERIAL, String START_BALANCE, List<PaymentAccountSupplier> DETAILS_LIST) {
        this.SUPLIER = SUPLIER;
        this.DATE_OF_PAYMENT = DATE_OF_PAYMENT;
        this.VALUE_OF_PAYMENT = VALUE_OF_PAYMENT;
        this.INVOICE_NO = INVOICE_NO;
        this.PAYER = PAYER;
        this.PAYMENT_TYPE = PAYMENT_TYPE;
        this.ACCEPTANCE_DATE = ACCEPTANCE_DATE;
        this.TOTAL_CASH = TOTAL_CASH;
        this.TOTAL_BANK = TOTAL_BANK;
        this.SERIAL = SERIAL;
        this.START_BALANCE = START_BALANCE;
        this.DETAILS_LIST = DETAILS_LIST;
    }

    public String getSUPLIER() {
        return SUPLIER;
    }

    public void setSUPLIER(String SUPLIER) {
        this.SUPLIER = SUPLIER;
    }

    public String getDATE_OF_PAYMENT() {
        return DATE_OF_PAYMENT;
    }

    public void setDATE_OF_PAYMENT(String DATE_OF_PAYMENT) {
        this.DATE_OF_PAYMENT = DATE_OF_PAYMENT;
    }

    public String getVALUE_OF_PAYMENT() {
        return VALUE_OF_PAYMENT;
    }

    public void setVALUE_OF_PAYMENT(String VALUE_OF_PAYMENT) {
        this.VALUE_OF_PAYMENT = VALUE_OF_PAYMENT;
    }

    public String getINVOICE_NO() {
        return INVOICE_NO;
    }

    public void setINVOICE_NO(String INVOICE_NO) {
        this.INVOICE_NO = INVOICE_NO;
    }

    public String getPAYER() {
        return PAYER;
    }

    public void setPAYER(String PAYER) {
        this.PAYER = PAYER;
    }

    public String getPAYMENT_TYPE() {
        return PAYMENT_TYPE;
    }

    public void setPAYMENT_TYPE(String PAYMENT_TYPE) {
        this.PAYMENT_TYPE = PAYMENT_TYPE;
    }

    public String getACCEPTANCE_DATE() {
        return ACCEPTANCE_DATE;
    }

    public void setACCEPTANCE_DATE(String ACCEPTANCE_DATE) {
        this.ACCEPTANCE_DATE = ACCEPTANCE_DATE;
    }

    public String getTOTAL_CASH() {
        return TOTAL_CASH;
    }

    public void setTOTAL_CASH(String TOTAL_CASH) {
        this.TOTAL_CASH = TOTAL_CASH;
    }

    public String getTOTAL_BANK() {
        return TOTAL_BANK;
    }

    public void setTOTAL_BANK(String TOTAL_BANK) {
        this.TOTAL_BANK = TOTAL_BANK;
    }

    public String getSERIAL() {
        return SERIAL;
    }

    public void setSERIAL(String SERIAL) {
        this.SERIAL = SERIAL;
    }

    public String getSTART_BALANCE() {
        return START_BALANCE;
    }

    public void setSTART_BALANCE(String START_BALANCE) {
        this.START_BALANCE = START_BALANCE;
    }

    public List<PaymentAccountSupplier> getDETAILS_LIST() {
        return DETAILS_LIST;
    }

    public void setDETAILS_LIST(List<PaymentAccountSupplier> DETAILS_LIST) {
        this.DETAILS_LIST = DETAILS_LIST;
    }
}
