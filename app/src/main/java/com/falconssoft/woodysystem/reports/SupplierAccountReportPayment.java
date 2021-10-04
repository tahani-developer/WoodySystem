package com.falconssoft.woodysystem.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.AddToInventory;
import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.PaymentAccountSupplier;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.stage_one.AccountSupplier;
import com.falconssoft.woodysystem.stage_one.SuppliersAdapter;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class SupplierAccountReportPayment extends AppCompatActivity implements View.OnClickListener {
    private Settings generalSettings;
    List<PaymentAccountSupplier> paymentAccountSuppliersList;
    AccountSupplierReportAdapter adapter;
    TextView count,fromDate,toDate,total,supplier;
    ListView list;
    Button pdf,excel,email;
    private Calendar myCalendar;
    String myFormat;
    private SimpleDateFormat sdf;
    int openLinkFlag=0;
    private SuppliersAdapter suppliersAdapter;
    private List<SupplierInfo> suppliers = new ArrayList<>();
    public static String supplierName = "All";
    private List<SupplierInfo> arraylist = new ArrayList<>();
    private Dialog searchDialog;
    private RecyclerView recyclerView;
    Spinner payType;
    String payTypes="All";
    private ArrayAdapter<String> locationAdapter;
    private List<String> locationList;
    ProgressDialog progressDialogTack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_supplier_account);
        initializatin();

    }

    private void initializatin() {
        generalSettings = new DatabaseHandler(this).getSettings();
        paymentAccountSuppliersList=new ArrayList<>();
        count=findViewById(R.id.acceptanceReport_count);
        list=findViewById(R.id.list);
        pdf=findViewById(R.id.pdf);
        excel=findViewById(R.id.excel);
        email=findViewById(R.id.email);
        fromDate=findViewById(R.id.fromDateText);
        toDate=findViewById(R.id.toDateText);
        supplier=findViewById(R.id.supplier);
        payType=findViewById(R.id.payType);
        locationList=new ArrayList<>();
        locationList.clear();
        locationList.add("Bank");
        locationList.add("Cash");
        locationList.add(0, "All");
        payType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payTypes = parent.getSelectedItem().toString();
                new JSONTask3().execute();
//                filters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        payType.setAdapter(locationAdapter);

        myCalendar = Calendar.getInstance();
        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        fromDate.setText(sdf.format(myCalendar.getTime()));//"1/12/2019"
        toDate.setText(sdf.format(myCalendar.getTime()));

        pdf.setOnClickListener(this);
        excel.setOnClickListener(this);
        email.setOnClickListener(this);
        supplier.setOnClickListener(this);
        fromDate.setOnClickListener(this);
        toDate.setOnClickListener(this);
        new JSONTask1().execute();
        new JSONTask3().execute();
    }

    void supplierDialog(){
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
        suppliersAdapter = new SuppliersAdapter(null, suppliers, null, null,0,null,null,SupplierAccountReportPayment.this,null);
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


    void showPdf(){
        ExportToPDF obj = new ExportToPDF(SupplierAccountReportPayment.this);
        obj.exportSupplierAccountPayment(paymentAccountSuppliersList);


    }

    public void showPdfExport(PaymentAccountSupplier newRowInfo){
        ExportToPDF obj = new ExportToPDF(SupplierAccountReportPayment.this);
        obj.exportSupplierAccountPayment(Collections.singletonList(newRowInfo));


    }
    public void showExcelExport(PaymentAccountSupplier newRowInfo){

        ExportToExcel.getInstance().createExcelFile(SupplierAccountReportPayment.this, "Payment_Account_supplier_Report_2.xls", 11, null, Collections.singletonList(newRowInfo));

    }
    void showExcel(){

        ExportToExcel.getInstance().createExcelFile(SupplierAccountReportPayment.this, "Payment_Account_supplier_Report_2.xls", 11, null, paymentAccountSuppliersList);

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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.pdf:
                if(paymentAccountSuppliersList.size()!=0) {
                    showPdf();
                }else {
                    Toast.makeText(this, "no data for pdf ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.excel:
                if(paymentAccountSuppliersList.size()!=0) {
                    showExcel();
                }else {
                    Toast.makeText(this, "no data for excel ", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.email:

                if(paymentAccountSuppliersList.size()!=0) {

                    try {
                        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
                        deleteTempFolder(folder.getPath());
                    }catch (Exception e){
                        Log.e("Delete Folder ","folder");
                    }


                    ExportToPDF obj = new ExportToPDF(SupplierAccountReportPayment.this);
                    obj.exportSupplierAccountPaymentEmail(paymentAccountSuppliersList);

                    sendEmail("","");
                }else {
                    Toast.makeText(this, "no data For create email", Toast.LENGTH_SHORT).show();
                }
                break;

            case  R.id.fromDateText:
                openLinkFlag=0;
                date(0);
                break;
            case R.id.toDateText:
                openLinkFlag=0;
                date(1);
                break;
            case R.id.supplier:
             supplierDialog();
                break;
        }
    }

    public void sendEmail(PaymentAccountSupplier paymentAccountSupplier){

        try {
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
            deleteTempFolder(folder.getPath());
        }catch (Exception e){
            Log.e("Delete Folder ","folder");
        }


        ExportToPDF obj = new ExportToPDF(SupplierAccountReportPayment.this);
        obj.exportSupplierAccountPaymentEmail(Collections.singletonList(paymentAccountSupplier));

        sendEmail("","");
    }

    public void deleteRaw (PaymentAccountSupplier paymentAccountSupplier){

        new DELETE(paymentAccountSupplier).execute();


    }
    public void date (int flag){
        new DatePickerDialog(SupplierAccountReportPayment.this, openDatePickerDialog(flag), myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                if (flag == 0)
                    fromDate.setText(sdf.format(myCalendar.getTime()));
                else
                    toDate.setText(sdf.format(myCalendar.getTime()));

//                if (!from.getText().toString().equals("") && !to.getText().toString().equals(""))
//                    filters();


                Log.e("url555222","in "+(view.getId()==R.id.Loding_Order_from)+"   "+(view.getId()==R.id.Loding_Order_to));

                if(openLinkFlag==0) {
                    openLinkFlag=1;
                    new JSONTask3().execute();
                }

            }

        };
        return date;
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
        suppliersAdapter = new SuppliersAdapter(null, arraylist, null, null,0,null,null,SupplierAccountReportPayment.this,null);
        recyclerView.setAdapter(suppliersAdapter);
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
            Toast.makeText(SupplierAccountReportPayment.this, "Storage Permission"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        //  deleteTempFolder(pdfFile.getPath());
    }


    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        supplierName = supplierNameLocal;
        supplier.setText(supplierName);
        supplier.setError(null);
        searchDialog.dismiss();
        new JSONTask3().execute();

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

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("SUPPLIERS");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        SupplierInfo supplier = new SupplierInfo();
                        supplier.setSupplierNo(innerObject.getString("SUPPLIER_NO"));
                        supplier.setSupplierName(innerObject.getString("SUPPLIER_NAME"));

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
             //   adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(SupplierAccountReportPayment.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // **************************** GET DATA  ****************************
    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson = null;
            try {// + supplierName.trim().replace(" ", "%20")
//                http://10.0.0.22/woody/import.php?FLAG=2on
                String pan="1";
                if(payTypes.equals("Bank")){
                    pan="1";
                }else if(payTypes.equals("Cash")){
                    pan="0";
                }else {
                    pan="All";
                }

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=23&SUPLIER="+supplierName.trim().replace(" ","%20")
                        +"&FROM_DATE="+fromDate.getText().toString()+"&TO_DATE="+toDate.getText().toString()+
                        "&PAYMENT_TYPE="+pan);

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
            try {
                if(progressDialogTack!=null) {
                    progressDialogTack.dismiss();
                }
            }catch (Exception e){


            }            if (s == null)
                Log.e("tag", "JSONTask3/Failed to export data Please check internet connection");
            else if (!s.contains("noPaymentFound")) {

                paymentAccountSuppliersList.clear();
                Gson gson = new Gson();
                PaymentAccountSupplier lists = gson.fromJson(s, PaymentAccountSupplier.class);
                if (lists != null)
                    paymentAccountSuppliersList.addAll(lists.getDETAILS_LIST());
                else
                    paymentAccountSuppliersList = null;

                count.setText("" + paymentAccountSuppliersList.size());
                adapter = new AccountSupplierReportAdapter(SupplierAccountReportPayment.this, paymentAccountSuppliersList);
                list.setAdapter(adapter);

                //notiList(0,0,12);
               // totalCalculate();
                //rejectAdd();
            } else {
                paymentAccountSuppliersList.clear();
                count.setText("" + paymentAccountSuppliersList.size());
                adapter = new AccountSupplierReportAdapter(SupplierAccountReportPayment.this, paymentAccountSuppliersList);
                list.setAdapter(adapter);
                //totalCalculate();
            }

        }
    }


    private class DELETE extends AsyncTask<String, String, String> {

        PaymentAccountSupplier paymentAccountSupplier;

        public DELETE(PaymentAccountSupplier paymentAccountSupplier) {
            this.paymentAccountSupplier = paymentAccountSupplier;
        }

        @Override
        protected void onPreExecute() {

            progressDialogTack = new ProgressDialog(SupplierAccountReportPayment.this, R.style.MyAlertDialogStyle);
            progressDialogTack.setMessage("Please Waiting...");
            progressDialogTack.setCanceledOnTouchOutside(false);

            progressDialogTack.show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

//                JSONArray jsonArray = new JSONArray();
//                jsonArray.put(editPlannedAndBundleInfo.getJSONObject());

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                nameValuePairs.add(new BasicNameValuePair("UPDATE_BUNDLE", "1"));
//                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", oldBundleNoString)); // the old bundle number
                nameValuePairs.add(new BasicNameValuePair("DELETE_PAYMENT_SUPPLIER", "1"));
                nameValuePairs.add(new BasicNameValuePair("INVOICE_NO", paymentAccountSupplier.getINVOICE_NO())); // the old bundle number
                nameValuePairs.add(new BasicNameValuePair("SERIAL", paymentAccountSupplier.getSERIAL())); // the updated bundle info

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
                Log.e("tag", "" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                if (s.contains("UPDATE_PAYMENT_SUCCESS")) {
                    new JSONTask3().execute();
                } else {
                    Log.e("tag", "updated bundle raw/Failed to export data");
                }
            } else {
                Log.e("tag", "updated bundle raw/Failed to export data Please check internet connection");
            }
        }
    }


}
