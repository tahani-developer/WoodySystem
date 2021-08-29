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

    @SerializedName("DEBT")
    private String DEBT="0";

    @SerializedName("CASHES")
    private String CASHES="0";

    @SerializedName("CASHPAYMENT")
    private String CASHPAYMENT="0";

    @SerializedName("BANKPAYMENT")
    private String BANKPAYMENT="0";

    @SerializedName("START_CASH")
    private String START_CASH="0";

    @SerializedName("START_BANK")
    private String START_BANK="0";

    @SerializedName("REMININGBANK")
    private String REMININGBANK="0";

    @SerializedName("REMININGCASH")
    private String REMININGCASH="0";

    @SerializedName("DETAILS_LIST")
    private List<PaymentAccountSupplier> DETAILS_LIST;

    @SerializedName("PAYMENT_ACCOUNT")
    private  List<PaymentAccountSupplier>PAYMENT_ACCOUNT;



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

    public List<PaymentAccountSupplier> getPAYMENT_ACCOUNT() {
        return PAYMENT_ACCOUNT;
    }

    public void setPAYMENT_ACCOUNT(List<PaymentAccountSupplier> PAYMENT_ACCOUNT) {
        this.PAYMENT_ACCOUNT = PAYMENT_ACCOUNT;
    }

    public String getDEBT() {
        return DEBT;
    }

    public void setDEBT(String DEBT) {
        this.DEBT = DEBT;
    }

    public String getCASHES() {
        return CASHES;
    }

    public void setCASHES(String CASHES) {
        this.CASHES = CASHES;
    }

    public String getCASHPAYMENT() {
        return CASHPAYMENT;
    }

    public void setCASHPAYMENT(String CASHPAYMENT) {
        this.CASHPAYMENT = CASHPAYMENT;
    }

    public String getBANKPAYMENT() {
        return BANKPAYMENT;
    }

    public void setBANKPAYMENT(String BANKPAYMENT) {
        this.BANKPAYMENT = BANKPAYMENT;
    }

    public String getSTART_CASH() {
        return START_CASH;
    }

    public void setSTART_CASH(String START_CASH) {
        this.START_CASH = START_CASH;
    }

    public String getSTART_BANK() {
        return START_BANK;
    }

    public void setSTART_BANK(String START_BANK) {
        this.START_BANK = START_BANK;
    }

    public String getREMININGBANK() {
        return REMININGBANK;
    }

    public void setREMININGBANK(String REMININGBANK) {
        this.REMININGBANK = REMININGBANK;
    }

    public String getREMININGCASH() {
        return REMININGCASH;
    }

    public void setREMININGCASH(String REMININGCASH) {
        this.REMININGCASH = REMININGCASH;
    }
}
