package com.falconssoft.woodysystem.stage_one;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SearchView;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.AcceptanceSupplierReport;
import com.falconssoft.woodysystem.reports.SupplierAccountAdapter;
import com.google.gson.Gson;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class AccountSupplier extends AppCompatActivity implements View.OnClickListener {

    ListView list;
    List<NewRowInfo> newRowInfoList;
    SupplierAccountAdapter adapter;
    private Settings generalSettings;
    TextView count, supplier, total;
    public  String supplierName = "";
    public  String supplierNo = "";
    private RecyclerView recyclerView;
    private List<SupplierInfo> suppliers = new ArrayList<>();
    private List<SupplierInfo> arraylist = new ArrayList<>();
    private SuppliersAdapter suppliersAdapter;
    private Dialog searchDialog;
    Button payment,pdfReport,exportExcel,email;
    private Calendar myCalendar;
    String myFormat;
    private SimpleDateFormat sdf;
    TextView totalAcce,balances,totalBanks,totalCashs;
    ProgressDialog progressDialogTack;
    TextView startBank,startCash,remainBank,remainCash,PaymentCash,PaymentBank;
    Dialog passwordDialog;
    CheckBox PriceCashCheckBox;
    Button cashBuAll,priceBuAll;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supplier_account);
        initializationView();


    }

    private void initializationView() {
        list = findViewById(R.id.list);
        PriceCashCheckBox=findViewById(R.id.PriceCashCheckBox);
        generalSettings = new DatabaseHandler(this).getSettings();
        newRowInfoList = new ArrayList<>();
        supplier = findViewById(R.id.truck_report_supplier);
        supplier.setOnClickListener(this);
        payment = findViewById(R.id.payment);
        payment.setOnClickListener(this);
        count = findViewById(R.id.acceptanceReport_count);
        totalAcce= findViewById(R.id.totalAcc);
        balances= findViewById(R.id.balance);
        totalBanks= findViewById(R.id.totalBank);
        pdfReport=findViewById(R.id.pdf_report);
        totalCashs= findViewById(R.id.totalCash);
        exportExcel=findViewById(R.id.export_Excel);
        startCash=findViewById(R.id.startCash);
        startBank=findViewById(R.id.startBank);
        remainCash=findViewById(R.id.remainCash);
        remainBank=findViewById(R.id.remainBank);
        PaymentCash=findViewById(R.id.PaymentCash);
        PaymentBank=findViewById(R.id.PaymentBank);
        priceBuAll=findViewById(R.id.priceBuAll);
        cashBuAll=findViewById(R.id.cashBuAll);
        email=findViewById(R.id.email);
        myCalendar = Calendar.getInstance();
        pdfReport.setOnClickListener(this);
        exportExcel.setOnClickListener(this);
        email.setOnClickListener(this);
        startBank.setOnClickListener(this);
        startCash.setOnClickListener(this);
        PriceCashCheckBox.setOnClickListener(this);
        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        supplier.setText("");
        new JSONTask1().execute();
        new JSONTask3().execute();
//        adapter = new SupplierAccountAdapter(AccountSupplier.this, newRowInfoList);
//        list.setAdapter(adapter);

        cashBuAll.setOnClickListener(this);
        priceBuAll.setOnClickListener(this);

    }

    public void notiList(int index, double price, int flag) {
        double accCubic= 0;
        accCubic=Double.parseDouble( convertToEnglish(newRowInfoList.get(index).getTruckCMB()))- Double.parseDouble(convertToEnglish(newRowInfoList.get(index).getCbmRej()));
        accCubic=Double.parseDouble(convertToEnglish(""+accCubic));
       newRowInfoList.get(index).setCbmAccept(""+accCubic);
        if (flag == 1) {
            newRowInfoList.get(index).setPrice(price);
            newRowInfoList.get(index).setDebt$(price*accCubic);
        } else if (flag == 2) {
            newRowInfoList.get(index).setCash(price);
            newRowInfoList.get(index).setCash$(price*accCubic);
        }


        new JSONTaskPrice(newRowInfoList.get(index)).execute();

    }

    public void Acc(int index) {
        double accCubic= 0;
        accCubic=Double.parseDouble(convertToEnglish( newRowInfoList.get(index).getTruckCMB()))- Double.parseDouble(convertToEnglish(newRowInfoList.get(index).getCbmRej()));
        accCubic=Double.parseDouble(String.format("%.3f", accCubic));
        newRowInfoList.get(index).setCbmAccept(""+accCubic);
        Log.e("master444"," "+accCubic);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.truck_report_supplier:
                SupplierDialog();
                break;
            case R.id.payment:
                showPasswordDialog(2, null);

                break;
            case R.id.pdf_report:
                if (newRowInfoList.size() != 0) {
                    showPdf();
                } else {
                    Toast.makeText(this, "no data For create Pdf", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.export_Excel:
                showPasswordDialog(1, null);


                break;
            case R.id.email:
                if (newRowInfoList.size() != 0) {

                    try {
                        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
                        deleteTempFolder(folder.getPath());
                    } catch (Exception e) {
                        Log.e("Delete Folder ", "folder");
                    }


                    ExportToPDF obj = new ExportToPDF(AccountSupplier.this);
                    obj.exportSupplierAccountSupplierEmail(newRowInfoList, supplierName);

                    sendEmail("", "");
                } else {
                    Toast.makeText(this, "no data For create email", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.startBank:
                showPasswordDialog(4, null);
                break;
            case R.id.startCash:
                showPasswordDialog(5, null);
                break;
            case R.id.PriceCashCheckBox:
                checkAll();
                break;
            case R.id.cashBuAll:
                priceDialog(2);
                break;
            case R.id.priceBuAll:
                priceDialog(1);
                break;
        }
    }

    public void checkAll() {

        for(int i=0;i<list.getChildCount();i++){
//            LinearLayout linearLayout=(LinearLayout) list.getChildAt(i);
//            TableRow tableRow=(TableRow) linearLayout.getChildAt(i);
//            CheckBox checkBox=(CheckBox) tableRow.getChildAt(1);
//            if(PriceCashCheckBox.isChecked()) {
//                checkBox.setChecked(true);
//            }else {
//                checkBox.setChecked(false);
//            }


            if(PriceCashCheckBox.isChecked()) {
                newRowInfoList.get(i).setCh(true);
            }else {
                newRowInfoList.get(i).setCh(false);
            }


        }

        adapter.notifyDataSetChanged();

    }

    void priceDialog(int flag){
        final Dialog dialog = new Dialog(AccountSupplier.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.price_editing_dialog);

        Button doneButton=dialog.findViewById(R.id.doneButton);
        EditText priceText=dialog.findViewById(R.id.new_price);



        priceText.setText("0.0");
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!priceText.getText().toString().equals("")&&!priceText.getText().toString().equals(".")) {
                    // itemsList.get(index).setPrice(Double.parseDouble(priceText.getText().toString()));
                 //notiList(index,Double.parseDouble(priceText.getText().toString()),flag);
                    Log.e("checkBox123456  ",""+list.getChildCount());
                    for(int i=0;i<newRowInfoList.size();i++){
//                        LinearLayout linearLayout=(LinearLayout) list.getChildAt(i);
//                        TableRow tableRow=(TableRow) linearLayout.getChildAt(i);
//                        CheckBox checkBox=(CheckBox) tableRow.findViewById(R.id.PCCheckBox);
                        Log.e("checkBox123456  ","i="+i+"  "+newRowInfoList.get(i).isCh());
                        if(newRowInfoList.get(i).isCh()){
                            //Log.e("checkBox123456  ","i="+i+"  "+checkBox.isChecked());
                          notiList(i,Double.parseDouble(priceText.getText().toString()),flag);
                        }
                    }


                    dialog.dismiss();
                }else{
                    priceText.setError("Required !");
                }


             //   adapter.notifyDataSetChanged();
            }
        });

        dialog.show();
    }


    void showPdf(){
        ExportToPDF obj = new ExportToPDF(AccountSupplier.this);
        obj.exportSupplierAccountSupplier(newRowInfoList,supplierName);


    }

   public void showPdfExport(NewRowInfo newRowInfo){
        ExportToPDF obj = new ExportToPDF(AccountSupplier.this);
        obj.exportSupplierAccountSupplier(Collections.singletonList(newRowInfo),supplierName);


    }
   public void showExcelExport(NewRowInfo newRowInfo){

        showPasswordDialog(3,null);

    }
    void showExcel(){

        ExportToExcel.getInstance().createExcelFile(AccountSupplier.this, "Acceptance_supplier_Report_2.xls", 10, null, newRowInfoList);

    }
    public void deleteTempFolder(String dir) {
        File myDir = new File(dir);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }


    public void date (int flag,TextView from){
        new DatePickerDialog(AccountSupplier.this, openDatePickerDialog(flag,from), myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag,TextView from) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


//                if (flag == 0)
                    from.setText(sdf.format(myCalendar.getTime()));


//                if (!from.getText().toString().equals("") && !to.getText().toString().equals(""))
//                    filters();
//                date_from = from.getText().toString();
//                date_to = to.getText().toString();

//                Log.e("url555222","in "+(view.getId()==R.id.Loding_Order_from)+"   "+(view.getId()==R.id.Loding_Order_to));

//                if(openLinkFlag==0) {
//                    openLinkFlag=1;
//                    new AcceptanceReport.JSONTask().execute();
//                }

            }

        };
        return date;
    }


    private void paymentDialog() {
        final Dialog dialog = new Dialog(AccountSupplier.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.payment_dialog);
        EditText   value, invNo, payer;
        TextView from;
        RadioButton bankPay, cashPay;
        Button doneButton = dialog.findViewById(R.id.doneButton);
        from = dialog.findViewById(R.id.Date);
        value = dialog.findViewById(R.id.value);
        invNo = dialog.findViewById(R.id.invNo);
        payer = dialog.findViewById(R.id.payer);
        bankPay = dialog.findViewById(R.id.bankPay);
        cashPay = dialog.findViewById(R.id.cashPay);
        from.setText(sdf.format(myCalendar.getTime()));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!from.getText().toString().equals("")) {
                    if (!value.getText().toString().equals("")) {
                        if (!invNo.getText().toString().equals("")) {
                            if (!payer.getText().toString().equals("")) {
                                String payType="1";

                                if(bankPay.isChecked()){
                                    payType="1";
                                }else {
                                    payType="0";
                                }

                                new JSONTaskAddPayment(invNo.getText().toString(),
                                        from.getText().toString(),
                                        value.getText().toString(),
                                        payer.getText().toString(),
                                        payType,
                                        "25",
                                        "45",
                                        "74",
                                        "85"
                                        ,dialog
                                ).execute();

                            } else {
                                payer.setError("Required !");
                            }
                        } else {
                            invNo.setError("Required !");
                        }
                    } else {
                        value.setError("Required !");
                    }
                } else {
                    from.setError("Required !");
                }
            }
        });

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                date(0,from);
            }
        });

        dialog.show();

    }



    void SupplierDialog() {

        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_supplier_dialog);
        searchDialog.setCancelable(false);

        SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog.findViewById(R.id.search_supplier_close);
        total = searchDialog.findViewById(R.id.total_suppliers);
        total.setText("" + suppliers.size());

        recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        suppliersAdapter = new SuppliersAdapter(null, suppliers, null, null, 0, null, AccountSupplier.this,null,null);
        recyclerView.setAdapter(suppliersAdapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog.dismiss();
            }
        });
        searchDialog.show();

    }

    public void totalCalculate(){
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            double totalCash=0.0,totalBank=0.0,balance=0.0,totalAcc=0.0;
        for(int i=0;i<newRowInfoList.size();i++){

            double ac=0,cash=0,price=0;


            if(!TextUtils.isEmpty(newRowInfoList.get(i).getCbmAccept())) {
                ac= Double.parseDouble(newRowInfoList.get(i).getCbmAccept());
                Log.e("master1",ac+"     "+newRowInfoList.get(i).getCbmAccept());
            }else {
                Log.e("master2","0");
                ac =0;
            }
            try{

                cash=newRowInfoList.get(i).getCash();
                Log.e("master3",cash+"     "+newRowInfoList.get(i).getCash());

            }catch (Exception e){
                cash =0;
                Log.e("master4","0");

            }
          try{
                price=newRowInfoList.get(i).getPrice();
                Log.e("master5",price+"     "+newRowInfoList.get(i).getPrice());
            }catch (Exception w){
                price =0;
                Log.e("master6","0");

            }

            double toC=cash*ac;
            totalCash+=(toC);


                double toB = price * ac;
                totalBank += toB;

                totalAcc +=ac;


        }
        balance=totalBank+totalCash;
        
        totalAcce.setText(convertToEnglish(String.format("%.3f", totalAcc)));
        balances.setText(convertToEnglish(String.format("%.3f", balance)) );
        totalBanks.setText(convertToEnglish(String.format("%.3f", totalBank)));
        totalCashs.setText(convertToEnglish(String.format("%.3f", totalCash)) );

        totalPayment();

    }

    public void filter(String charText) { // by Name
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(suppliers);
        } else {
            for (SupplierInfo supplierInfo : suppliers) {//for (SupplierInfo supplierInfo : arraylist){
                if (supplierInfo.getSupplierName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraylist.add(supplierInfo);
                }
            }
        }
        total.setText("" + arraylist.size());
        suppliersAdapter = new SuppliersAdapter(null, arraylist, null, null, 0, null, AccountSupplier.this,null,null);
        recyclerView.setAdapter(suppliersAdapter);
    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal,SupplierInfo supplierInfo) {
        supplierName = supplierNameLocal;
        supplierNo=supplierNoLocal;
        supplier.setText(supplierName);
        supplier.setError(null);
        searchDialog.dismiss();
        startBank.setText(""+supplierInfo.getStartBank());
        startCash.setText(""+supplierInfo.getStartCash());
        Log.e("Bank121","    "+supplierInfo.getStartBank()+"      "+supplierInfo.getStartCash());
        new JSONTaskGetRemainingBank().execute();
        new JSONTask3().execute();


    }
    void BankDialog(){
        final Dialog dialog = new Dialog(AccountSupplier.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.bank_editing_dialog);

        Button doneButton=dialog.findViewById(R.id.doneButton);
        EditText priceText=dialog.findViewById(R.id.new_price);

            priceText.setText("" + startBank.getText().toString());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!priceText.getText().toString().equals("")&&!priceText.getText().toString().equals(".")) {
                    startBank.setText(priceText.getText().toString());

                    new JSONTaskStartCashBank().execute();
                    dialog.dismiss();
                }else{
                    priceText.setError("Required !");
                }


            }
        });

        dialog.show();
    }
    void CashDialog(){
        final Dialog dialog = new Dialog(AccountSupplier.this, R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.cash_editing_dialog);

        Button doneButton=dialog.findViewById(R.id.doneButton);
        EditText priceText=dialog.findViewById(R.id.new_price);



            priceText.setText("" + startCash.getText().toString());

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!priceText.getText().toString().equals("")&&!priceText.getText().toString().equals(".")) {
                    startCash.setText(priceText.getText().toString());
                    new JSONTaskStartCashBank().execute();
                    dialog.dismiss();
                }else{
                    priceText.setError("Required !");
                }


            }
        });

        dialog.show();
    }

    public  void   totalPayment(){

        double bank= Double.parseDouble(convertToEnglish(PaymentBank.getText().toString()));
        double totalPBank= convertToEnglish1(totalBanks.getText().toString());
        double sumation= (Double.parseDouble(convertToEnglish(startBank.getText().toString()))+bank-totalPBank);
        remainBank.setText(""+convertToEnglish(""+String.format("%.3f",sumation)));
        double cash= Double.parseDouble(convertToEnglish(PaymentCash.getText().toString()));
        double totalPCash=convertToEnglish1(totalCashs.getText().toString());
        double sumationC= (Double.parseDouble(convertToEnglish(startCash.getText().toString()))+cash-totalPCash);
        remainCash.setText(""+convertToEnglish(""+String.format("%.3f",sumationC)));

    }
    public void sendEmail(String toEmil, String subject) {


        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
        File[] listOfFiles = folder.listFiles();
        Log.e("pathh= ", "" + folder.getPath().toString() + "  " + listOfFiles.length);
        List<String> images = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            //if (listOfFiles[i].getName().endsWith(".jpg")) {
            images.add(listOfFiles[i].getPath());
            // }
        }
        String subject2 = "";
        if (!TextUtils.isEmpty(subject)) {
            subject2 = subject+" ";
        } else {
            subject2 = "Quality";
        }


        shareWhatsAppA(folder,2,images);
    }

    public void shareWhatsAppA(File pdfFile,int pdfExcel,List<String> filePaths){
        try {

            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
            Uri uri = Uri.fromFile(pdfFile);
            Intent sendIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
            if (pdfFile.exists()) {
                if (pdfExcel == 1) {
                    sendIntent.setType("application/excel");
                } else if (pdfExcel == 2) {
                    sendIntent.setType("plain/text");//46.185.208.4
                }
                //  sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(String.valueOf(uri)));

                sendIntent.putExtra(Intent.EXTRA_SUBJECT,
                        pdfFile.getName() + " Sharing File...");

                sendIntent.putExtra(Intent.EXTRA_TEXT, pdfFile.getName() + " Sharing File");

                ArrayList<Uri> uris = new ArrayList<Uri>();
                //convert from paths to Android friendly Parcelable Uri's
                for (String file : filePaths)
                {
                    File fileIn = new File(file);
                    Uri u = Uri.fromFile(fileIn);
                    uris.add(u);
                }
                sendIntent.putExtra(Intent.EXTRA_STREAM, uris);

                Intent shareIntent = Intent.createChooser(sendIntent, null);
               startActivity(shareIntent);

            }
        }catch (Exception e){
            Log.e("drk;d","dfrtr"+e.toString());
            Toast.makeText(AccountSupplier.this, "Storage Permission"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        //  deleteTempFolder(pdfFile.getPath());
    }


    void showPasswordDialog(int flag,NewRowInfo newRowInfo) {
        passwordDialog = new Dialog(this);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("3030300")) {
                    passwordDialog.dismiss();
                 switch (flag){
                     case 1:
                         if(newRowInfoList.size()!=0) {
                             showExcel();
                         }else {
                             Toast.makeText(AccountSupplier.this, "no data For create excel", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 2:
                         if (newRowInfoList.size() != 0) {
                             paymentDialog();
                         } else {
                             Toast.makeText(AccountSupplier.this, "No Data For Payment", Toast.LENGTH_SHORT).show();
                         }
                         break;
                     case 3:
                         ExportToExcel.getInstance().createExcelFile(AccountSupplier.this, "Account_supplier_Report_2.xls", 10, null, Collections.singletonList(newRowInfo));
                         break;
                     case 4:
                         BankDialog();
                         break;
                     case 5:
                         CashDialog();
                         break;
                 }
                } else
                    Toast.makeText(AccountSupplier.this, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }

    public void checkedValueInMainList(int i,boolean flag) {
        newRowInfoList.get(i).setCh(flag);
    }


    // *************************************** GET SUPPLIERS ***************************************
    private class JSONTask1 extends AsyncTask<String, String, List<SupplierInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SupplierInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=4");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String finalJson = sb.toString();
                Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);
                suppliers.clear();
                arraylist.clear();
                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("SUPPLIERS");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        SupplierInfo supplier = new SupplierInfo();
                        supplier.setSupplierNo(innerObject.getString("SUPPLIER_NO"));
                        supplier.setSupplierName(innerObject.getString("SUPPLIER_NAME"));
                        supplier.setStartCash(innerObject.getString("START_CASH"));
                        supplier.setStartBank(innerObject.getString("START_BANK"));

                        Log.e("Bank122","   "+supplier.getStartCash()+"    "+supplier.getStartBank());
                        suppliers.add(supplier);
                        arraylist.add(supplier);

                    }
                    suppliers.add(0, new SupplierInfo("-1", "All"));
                    arraylist.add(0, new SupplierInfo("-1", "All"));
                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
                }

            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } catch (JSONException e) {
                Log.e("Customer", "********ex3  " + e.toString());
                e.printStackTrace();
            } finally {
                Log.e("Customer", "********finally");
                if (connection != null) {
                    Log.e("Customer", "********ex4");
                    // connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return suppliers;
        }


        @Override
        protected void onPostExecute(final List<SupplierInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + result.size());
                //adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AccountSupplier.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    // **************************** GET DATA  ****************************
    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialogTack = new ProgressDialog(AccountSupplier.this, R.style.MyAlertDialogStyle);
            progressDialogTack.setMessage("Please Waiting...");
            progressDialogTack.setCanceledOnTouchOutside(false);
            progressDialogTack.show();


        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson = null;
            try {
//                http://10.0.0.22/woody/import.php?FLAG=2
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=21&SUPPLIER=" + supplierName.trim().replace(" ", "%20"));

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                finalJson = sb.toString();
                Log.e("JSONTask3", url + " : " + finalJson);

//                newRowInfoList.clear();
//                Gson gson = new Gson();
//                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
//                if (list != null)
//                    newRowInfoList.addAll(list.getDetailsList());
//                else
//                    newRowInfoList = null;

            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } finally {
                Log.e("Customer", "********finally");
                if (connection != null) {
                    Log.e("Customer", "********ex4");
                    // connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return finalJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialogTack.dismiss();
            if (s == null)
                Log.e("tag", "JSONTask3/Failed to export data Please check internet connection");
            else if (!s.contains("noBundleFound")) {

                newRowInfoList.clear();
                Gson gson = new Gson();
                NewRowInfo lists = gson.fromJson(s, NewRowInfo.class);
                if (lists != null)
                    newRowInfoList.addAll(lists.getDetailsList());
                else
                    newRowInfoList = null;

                count.setText("" + newRowInfoList.size());
                adapter = new SupplierAccountAdapter(AccountSupplier.this, newRowInfoList);
                list.setAdapter(adapter);

                //notiList(0,0,12);
                totalCalculate();
                //rejectAdd();
            } else {
                newRowInfoList.clear();
                count.setText("" + newRowInfoList.size());
                adapter = new SupplierAccountAdapter(AccountSupplier.this, newRowInfoList);
                list.setAdapter(adapter);
                totalCalculate();

            }

        }
    }
    // **************************** GET DATA Payment  ****************************
    private class JSONTaskGetRemainingBank extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
//            progressDialogTack = new ProgressDialog(EditPage.this, R.style.MyAlertDialogStyle);
//            progressDialogTack.setMessage("Please Waiting...");
//            progressDialogTack.setCanceledOnTouchOutside(false);

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson = null;
            try {
//                http://10.0.0.22/woody/import.php?FLAG=2
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=24&SUPLIER=" + supplierName.trim().replace(" ", "%20"));

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                finalJson = sb.toString();
                Log.e("JSONTask3", url + " : " + finalJson);

//                newRowInfoList.clear();
//                Gson gson = new Gson();
//                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
//                if (list != null)
//                    newRowInfoList.addAll(list.getDetailsList());
//                else
//                    newRowInfoList = null;

            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } finally {
                Log.e("Customer", "********finally");
                if (connection != null) {
                    Log.e("Customer", "********ex4");
                    // connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return finalJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null)
                Log.e("tag", "JSONTask3/Failed to export data Please check internet connection");
            else if (!s.contains("noPaymentFound")) {
                JSONObject parentObject = null;
                try {
                    parentObject = new JSONObject(s);

                    JSONArray parentArrayOrders = parentObject.getJSONArray("Payment_LIST");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        if(innerObject.getString("PAYMENT_TYPE").equals("1")){//bank
                            double bank= innerObject.getDouble("SUM");
                            PaymentBank.setText(""+bank);
                            double sumation= (Double.parseDouble(convertToEnglish(startBank.getText().toString()))+bank);
                            remainBank.setText(""+sumation);

                        }else  if(innerObject.getString("PAYMENT_TYPE").equals("0")){//cash
                            double cash= innerObject.getDouble("SUM");
                            double sumation= (Double.parseDouble(convertToEnglish(startCash.getText().toString()))+cash);
                            PaymentCash.setText(""+cash);

                            remainCash.setText(""+sumation);
                        }

                        totalPayment();

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                remainCash.setText("0.0");
                remainBank.setText("0.0");
                PaymentCash.setText("0.0");
                PaymentBank.setText("0.0");
                Toast.makeText(AccountSupplier.this, "no payment", Toast.LENGTH_SHORT).show();

            }

        }
    }

    // *************************************** editing ***************************************
    private class JSONTaskPrice extends AsyncTask<String, String, String> {

        NewRowInfo newRowInfo;

        @Override
        protected void onPreExecute() {
            // progressDialog.show();
            super.onPreExecute();
        }

        public JSONTaskPrice(NewRowInfo newRowInfo) {
            this.newRowInfo = newRowInfo;
        }

        @Override
        protected String doInBackground(String... params) {
            try {
//https://5.189.130.98/WOODY/export.php

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                Log.e("addToInventory/", "" + jsonArrayBundles.toString());
                nameValuePairs.add(new BasicNameValuePair("UPDATE_PRICE_CASH", "1"));// list
//                for (int i=0 ;i< newRowList.size();i++) {
//                    nameValuePairs.add(new BasicNameValuePair("ROW_INFO_DETAILS", newRowList.get(i).toString()));
//                }

                nameValuePairs.add(new BasicNameValuePair("PRICE_CASH", "" + newRowInfo.getCash()));//oldTruck

                nameValuePairs.add(new BasicNameValuePair("DATE_OF_ACCEPTANCE", newRowInfo.getDate()));//oldTruck
                nameValuePairs.add(new BasicNameValuePair("PRICE", "" + newRowInfo.getPrice()));// list
                nameValuePairs.add(new BasicNameValuePair("GRADE", newRowInfo.getGrade())); // json object
                nameValuePairs.add(new BasicNameValuePair("THICKNESS", "" + newRowInfo.getThickness())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("WIDTH", "" + newRowInfo.getWidth())); // TTnNo

                nameValuePairs.add(new BasicNameValuePair("LENGTH", "" + newRowInfo.getLength())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("PIECES", "" + newRowInfo.getNoOfPieces())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("SUPLIER", newRowInfo.getSupplierName())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("TRUCK_NO", newRowInfo.getTruckNo())); // TTnNo

//                Log.e("addNewRow/", "update" + masterData.toString().trim() + " ///oldTruck" + oldTruck);

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JsonResponse = sb.toString();
                Log.e("tag/", "updatePrice" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag of delete row info", s);
            //progressDialog.dismiss();
            if (s != null) {
                if (s.contains("UPDATE_PRICE_SUCCESS")) {

                    //showSnackbar("Delete Successfully", true);
                    totalCalculate();
                    adapter.notifyDataSetChanged();
                    Log.e("tag", "UPDATE_PRICE_SUCCESS Success");
                } else {
                    totalCalculate();
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                totalCalculate();
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AccountSupplier.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    // *************************************** editing ***************************************
    private class JSONTaskStartCashBank extends AsyncTask<String, String, String> {



        @Override
        protected void onPreExecute() {
            // progressDialog.show();
            super.onPreExecute();
        }

        public JSONTaskStartCashBank() {
        }

        @Override
        protected String doInBackground(String... params) {
            try {
//https://5.189.130.98/WOODY/export.php

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("UPDATE_SUPPLIER_CASH_BANK", "1"));// list

                nameValuePairs.add(new BasicNameValuePair("START_CASH", "" + startCash.getText().toString()));//oldTruck
                nameValuePairs.add(new BasicNameValuePair("START_BANK", startBank.getText().toString()));//oldTruck
                nameValuePairs.add(new BasicNameValuePair("SUPPLIER_NO",supplierNo ));// list
                nameValuePairs.add(new BasicNameValuePair("SUPPLIER_NAME", supplierName)); // json object


                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JsonResponse = sb.toString();
                Log.e("tag/", "updatePrice" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag_update_start", s);
            //progressDialog.dismiss();
            if (s != null) {
                if (s.contains("UPDATE_SUCCESS")) {
                    totalPayment();
                    new JSONTask1().execute();


                    Log.e("tag", "UPDATE_PRICE_SUCCESS Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AccountSupplier.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }


    // *************************************** Add payment ***************************************
    private class JSONTaskAddPayment extends AsyncTask<String, String, String> {

        NewRowInfo newRowInfo;
        String invoiceNo,dateOfPayment, value, payer, paymentType, accDate, totalCash, totalPay,
         startBalance;
        Dialog dialog;
        @Override
        protected void onPreExecute() {
            // progressDialog.show();
            super.onPreExecute();
        }

        public JSONTaskAddPayment(String invoiceNo,String dateOfPayment,String value,String payer,String paymentType,String accDate,String totalCash,String totalPay,
                                  String startBalance,Dialog dialog) {
          //  this.newRowInfo = newRowInfo;
            this.invoiceNo = invoiceNo;
            this.dateOfPayment = dateOfPayment;
            this.value = value;
            this.payer = payer;
            this.paymentType = paymentType;
            this.accDate = accDate;
            this.totalCash = totalCash;
            this.totalPay = totalPay;
            this.startBalance = startBalance;
            this.dialog=dialog;

        }

        @Override
        protected String doInBackground(String... params) {
            try {

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

                nameValuePairs.add(new BasicNameValuePair("PAYMENT_SUPPLIER_ACCOUNT", "1"));// list
                nameValuePairs.add(new BasicNameValuePair("SUPLIER", "" + supplierName));//oldTruck

                nameValuePairs.add(new BasicNameValuePair("DATE_OF_PAYMENT", dateOfPayment));//oldTruck
                nameValuePairs.add(new BasicNameValuePair("VALUE_OF_PAYMENT", "" +value));// list
                nameValuePairs.add(new BasicNameValuePair("INVOICE_NO",invoiceNo)); // json object
                nameValuePairs.add(new BasicNameValuePair("PAYER", "" + payer)); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("PAYMENT_TYPE", "" + paymentType)); // TTnNo

                nameValuePairs.add(new BasicNameValuePair("ACCEPTANCE_DATE", "" +dateOfPayment)); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("TOTAL_CASH", "" + totalCashs.getText().toString())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("TOTAL_BANK", totalBanks.getText().toString())); // TTnNo
                nameValuePairs.add(new BasicNameValuePair("START_BALANCE", balances.getText().toString())); // TTnNo


                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();
                Log.e("tag/payment", "arr " + nameValuePairs.toArray().toString());
                JsonResponse = sb.toString();
                Log.e("tag/payment", "payment" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("PAYMENT_ACCOUNT_ ", s);
            //progressDialog.dismiss();
            if (s != null) {
                if (s.contains("PAYMENT_SUPPLIER_ACCOUNT_SUCCESS")) {

                    //showSnackbar("Delete Successfully", true);
                    adapter.notifyDataSetChanged();
                    Log.e("tag", "PAYMENT_SUPPLIER_ACCOUNT_SUCCESS Success");
                    new JSONTaskGetRemainingBank().execute();
                    dialog.dismiss();
                } else {

                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AccountSupplier.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String convertToEnglish(String value) {
        String newValue = (((((((((((value + "").replaceAll("١", "1")).replaceAll("٢", "2")).replaceAll("٣", "3")).replaceAll("٤", "4")).replaceAll("٥", "5")).replaceAll("٦", "6")).replaceAll("٧", "7")).replaceAll("٨", "8")).replaceAll("٩", "9")).replaceAll("٠", "0").replaceAll("٫", "."));
        return newValue;
    }

    public  double convertToEnglish1(String value){
        double d=0.0;
        try {
             d=Double.parseDouble(String.valueOf(NumberFormat.getInstance(new Locale("en","US")).parse(value)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return  d;
    }
}
