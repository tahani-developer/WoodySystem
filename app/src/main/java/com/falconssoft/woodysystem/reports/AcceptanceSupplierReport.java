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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.ItemsListAdapter4;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.stage_one.EditPage;
import com.falconssoft.woodysystem.stage_one.SuppliersAdapter;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AcceptanceSupplierReport extends AppCompatActivity implements   View.OnClickListener{
    private ListView list;
    private ProgressDialog progressDialog;
    private Settings generalSettings;
    private DatabaseHandler MHandler;
    List<NewRowInfo> dataList, filtered, dateFiltered;
    TextView rawCount,supplier,total;
    AcceptanceSupplierReportAdapter adapter2;
    private Spinner location;
    private String loc = "All";
    private static ListView listView;
    private List<String> locationList;
    private ArrayAdapter<String> locationAdapter;
    EditText from, to,thic,width,length,pieces,truckNo;
    private Dialog searchDialog;
    String myFormat;
    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    public static String supplierName = "All";
    private Calendar myCalendar;
    private List<SupplierInfo> suppliers = new ArrayList<>();
    private List<SupplierInfo> arraylist = new ArrayList<>();
    private SuppliersAdapter suppliersAdapter;
    private RecyclerView recyclerView;
    int openLinkFlag=0;
    private SimpleDateFormat sdf;
    private Spinner gradeSpinner;
    double  netRejectedString=0,netRejCMB=0,netTruckCmb=0,acceptCbm=0,netBundlesString=0;
    String  gradeText = "KD";
    Button preview,pdf,excel,email;
    TextView totalRejected,totalBundles,totalTruckCbm,totalRejCbm,totalAcceptCbm;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_supplier_report);
        initialization();
    }

    private void initialization() {

        MHandler = new DatabaseHandler(AcceptanceSupplierReport.this);
        generalSettings = MHandler.getSettings();
        dataList = new ArrayList<>();

        thic=findViewById(R.id.thic);
        width=findViewById(R.id.width);
        length=findViewById(R.id.length);
        pieces=findViewById(R.id.pieces);
        truckNo=findViewById(R.id.truckNo);
        gradeSpinner = findViewById(R.id.addNewRaw_grade);
        totalRejected = findViewById(R.id.total_rejected);
        totalBundles = findViewById(R.id.total_bundles);
        totalTruckCbm = findViewById(R.id.total_truck_cbm);
        totalRejCbm = findViewById(R.id.total_rej_cbm);
        totalAcceptCbm = findViewById(R.id.total_accept_cbm);
        preview=findViewById(R.id.preview);
        pdf=findViewById(R.id.pdf);
        excel=findViewById(R.id.excel);
        email=findViewById(R.id.email);
        gradeList.clear();
        gradeList.add("ALL");
        gradeList.add("KD");
        gradeList.add("Fresh");
        gradeList.add("Blue Stain");
        gradeList.add("Reject");
//        gradeList.add("KD Blue Stain");
//        gradeList.add("AST");
//        gradeList.add("AST Blue Stain");
//        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (parent.getId()) {
//                    case R.id.addNewRaw_grade:
                gradeText = parent.getItemAtPosition(position).toString();
//                        break;

                if(openLinkFlag==0) {
                    openLinkFlag=1;
                    new JSONTask().execute();
                }
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        list = findViewById(R.id.list);
        rawCount = findViewById(R.id.rawCount);
        location = findViewById(R.id.Loding_Order_Location);
        from=findViewById(R.id.from);
        to=findViewById(R.id.to);
        supplier=findViewById(R.id.supplier_report_supplier);
        listView = findViewById(R.id.listview);

        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        myCalendar = Calendar.getInstance();



        from.setText(sdf.format(myCalendar.getTime()));//"1/12/2019"
        to.setText(sdf.format(myCalendar.getTime()));
        from.setOnClickListener(this);
        to.setOnClickListener(this);
        supplier.setOnClickListener(this);
        preview.setOnClickListener(this);
        pdf.setOnClickListener(this);
        excel.setOnClickListener(this);
        email.setOnClickListener(this);
        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        adapter2 = new AcceptanceSupplierReportAdapter(AcceptanceSupplierReport.this, new ArrayList<>());
        list.setAdapter(adapter2);


//        locationList.clear();
        locationList=new ArrayList<>();
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add(0, "All");
        fillSpinnerAdapter();


        new JSONTask1().execute();
        new JSONTask().execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.from:
                openLinkFlag=0;
                date(0);
                break;
            case R.id.to:
                openLinkFlag=0;
                date(1);
                break;
            case R.id.supplier_report_supplier:
                supplierDialog();
                break;
            case R.id.preview:
                new JSONTask().execute();
                break;
            case R.id.pdf:

                String truckNos="All";
                if(!truckNo.getText().toString().equals("")){
                    truckNos=truckNo.getText().toString();
                }
                ExportToPDF obj = new ExportToPDF(AcceptanceSupplierReport.this);
                obj.exportSupplierAcceptance(dataList, truckNos, gradeText, from.getText().toString(), to.getText().toString(),supplierName,totalTruckCbm.getText().toString(),totalRejCbm.getText().toString(),totalBundles.getText().toString(),totalRejected.getText().toString(),totalAcceptCbm.getText().toString());

                break;
            case R.id.excel:
                ExportToExcel.getInstance().createExcelFile(AcceptanceSupplierReport.this, "Acceptance_Supplier_Report.xls", 9,null, dataList);
                break;
            case R.id.email:
                try {
                    File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
                    deleteTempFolder(folder.getPath());
                }catch (Exception e){
                    Log.e("Delete Folder ","folder");
                }
                ExportToPDF obj2 = new ExportToPDF(AcceptanceSupplierReport.this);
                obj2.exportSupplierAcceptanceForEmail(dataList,  truckNo.getText().toString(), gradeText, from.getText().toString(), to.getText().toString(),supplierName,totalTruckCbm.getText().toString(),totalRejCbm.getText().toString(),totalBundles.getText().toString(),totalRejected.getText().toString(),totalAcceptCbm.getText().toString());

                sendEmail("","");
                break;

        }
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

        //EmailPDF(folder);
        shareWhatsAppA(folder,2,images);


//        new SendMailTask(context,folder.getPath(),0).execute("quality@blackseawood.com",
//                "12345678Q",
//                toEmil,
//                "quality BLACK SEA WOOD",
//                subject2,
//                images
//        );

//
//        BackgroundMail.newBuilder(AddNewRaw.this)//rawanwork2021@gmail.com
//                .withUsername("quality@blackseawood.com")//quality@blackseawood.com
//                .withPassword("12345678Q")
//                .withMailto(toEmil)
//                .withType(BackgroundMail.TYPE_PLAIN)
//                .withSubject("quality BLACK SEA WOOD")
//                .withBody(subject2 /*"this is the body \n www.google.com  \n  http://5.189.130.98:8085/import.php?FLAG=3 \n "  */)
//                .withProcessVisibility(true)
//                .withAttachments(images)
//                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
//                    @Override
//                    public void onSuccess() {
//                        //do some magic
//                        clear();
//                        deleteTempFolder(folder.getPath());
//                    }
//                })
//                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
//                    @Override
//                    public void onFail() {
//                        //do some magic
//                    }
//                })
//                .send();


    }

    public void EmailPDF(File folder)
    {
        File PayslipDir = new File(Environment.getExternalStorageDirectory(), "/SendEmailWood/");
        // Write your file to that directory and capture the Uri
        String strFilename = "PDFFile.pdf";
        File htmlFile = new File(PayslipDir, strFilename);
        // Save file encoded as html
        Uri htmlUri = Uri.fromFile(folder);
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/pdf");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL,new String[] {"rawriy2017@gmail.com"});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Pdf attachment");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT,"Hi PDF is attached in this mail. ");
        emailIntent.putExtra(Intent.EXTRA_STREAM, htmlUri);
        AcceptanceSupplierReport.this.startActivity(Intent.createChooser(emailIntent,"Send mail..."));
        //finish();
    }

    public void shareWhatsAppA(File pdfFile, int pdfExcel, List<String> filePaths){
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
            Toast.makeText(AcceptanceSupplierReport.this, "Storage Permission"+e.toString(), Toast.LENGTH_SHORT).show();
        }

        //deleteTempFolder(pdfFile.getPath());
    }

        public Date formatDate(String date) {

//        Log.e("date", date);
            String myFormat = "dd/MM/yyyy"; //In which you need put here
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
            Date d = null;
            try {
                d = simpleDateFormat.parse(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return d;
        }
    void fillSpinnerAdapter() {

        locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        location.setAdapter(locationAdapter);


    }

    public void date (int flag){
        new DatePickerDialog(AcceptanceSupplierReport.this, openDatePickerDialog(flag), myCalendar
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


                if (flag == 0) {
                    from.setText(sdf.format(myCalendar.getTime()));
                 //   new JSONTask().execute();
                }
                else {
                    to.setText(sdf.format(myCalendar.getTime()));
                 //   new JSONTask().execute();
                }

//
//                date_from = from.getText().toString();
//                date_to = to.getText().toString();

                if(openLinkFlag==0) {
                    openLinkFlag=1;
                    new JSONTask().execute();
                }

            }

        };
        return date;
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
        suppliersAdapter = new SuppliersAdapter(null, suppliers, null, null,0,AcceptanceSupplierReport.this);
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



    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        supplierName = supplierNameLocal;
        supplier.setText(supplierName);
        supplier.setError(null);
        searchDialog.dismiss();
        new JSONTask().execute();

    }

    public void filter(String charText) { // by Name
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(suppliers);
        } else {
            for (SupplierInfo supplierInfo : suppliers) {
                if (supplierInfo.getSupplierName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraylist.add(supplierInfo);
                }
            }
        }
        total.setText("" + arraylist.size());
        suppliersAdapter = new SuppliersAdapter(null, arraylist, null, null,0,AcceptanceSupplierReport.this);
        recyclerView.setAdapter(suppliersAdapter);
    }
    void fillCbmVal(int i, double truckCbm, double rejCbm, double acceptCbms) {

            dataList.get(i).setTruckCMB("" + truckCbm);
            dataList.get(i).setCbmRej("" + rejCbm);
            dataList.get(i).setCbmAccept("" + acceptCbms);

            Log.e("newRawListIII", i + " size  = " + dataList.size());
            if (i == (dataList.size() - 1)) {
                rejectAdd();
            }

    }
    public void rejectAdd() {

       netRejectedString = 0;
        netBundlesString = 0;
        netRejCMB = 0;
        netTruckCmb = 0;
        acceptCbm = 0;
        Log.e("fromedit11", "" + dataList.size());

                for (int n = 0; n < dataList.size(); n++) {
                    netRejectedString += Double.parseDouble(dataList.get(n).getTotalRejectedNo());
                    netBundlesString += dataList.get(n).getNoOfBundles();
                    Log.e("newRawListIII", " vv = " + n + dataList.get(n).getCbmRej());

                    netRejCMB += Double.parseDouble(dataList.get(n).getCbmRej());
                    netTruckCmb += Double.parseDouble(dataList.get(n).getTruckCMB());
                    acceptCbm += Double.parseDouble(dataList.get(n).getCbmAccept());
                }

        Log.e("newRawListIII", " gg = " + netRejectedString + "  " + netBundlesString + "  " + netTruckCmb + "   " + netRejCMB + "   " + acceptCbm);

        netRejectedString = Double.parseDouble(String.format("%.3f", netRejectedString));

        netTruckCmb = Double.parseDouble(String.format("%.3f", netTruckCmb));

        netRejCMB = Double.parseDouble(String.format("%.3f", netRejCMB));

        acceptCbm = Double.parseDouble(String.format("%.3f", acceptCbm));

        totalRejected.setText("" + netRejectedString);
        totalBundles.setText("" + netBundlesString);
        totalTruckCbm.setText("" + netTruckCmb);
        totalRejCbm.setText("" + netRejCMB);
        totalAcceptCbm.setText("" + acceptCbm);

    }

    public void exportToPdf(NewRowInfo newRowInfo) {

        ExportToPDF obj = new ExportToPDF(AcceptanceSupplierReport.this);
        obj.exportSupplierAcceptance(Collections.singletonList(newRowInfo), newRowInfo.getTruckNo(), newRowInfo.getGrade(), from.getText().toString(), to.getText().toString(),supplierName,newRowInfo.getTruckCMB(),newRowInfo.getCbmRej(),newRowInfo.getNetBundles(),newRowInfo.getTotalRejectedNo(),newRowInfo.getCbmAccept());

    }

    public void exportToExcel(NewRowInfo newRowInfo) {
        ExportToExcel.getInstance().createExcelFile(AcceptanceSupplierReport.this, "Acceptance_Supplier_Report.xls", 9,null, Collections.singletonList(newRowInfo));
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

    public void sendEmailFromReport(NewRowInfo newRowInfo) {
        try {
            File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
            deleteTempFolder(folder.getPath());
        }catch (Exception e){
            Log.e("Delete Folder ","folder");
        }
        ExportToPDF obj2 = new ExportToPDF(AcceptanceSupplierReport.this);
        obj2.exportSupplierAcceptanceForEmail(Collections.singletonList(newRowInfo), newRowInfo.getTruckNo(), newRowInfo.getGrade(), from.getText().toString(), to.getText().toString(), supplierName, newRowInfo.getTruckCMB(), newRowInfo.getCbmRej(), newRowInfo.getNetBundles(), newRowInfo.getTotalRejectedNo(), newRowInfo.getCbmAccept());

       sendEmail("","");
    }

    // ******************************************** GET DATA For Report*****************************************
    private class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();

            progressDialog = new ProgressDialog(AcceptanceSupplierReport.this, R.style.MyAlertDialogStyle);
            progressDialog.setMessage("Please Waiting...");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson=null;
            try {
                // http://192.168.2.17:8088/woody/import.php?FLAG=55
                // http://5.189.130.98:8085/import.php?FLAG=5

//                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=55&DATE_FROM="
//                        + date_from + "&DATE_TO=" + date_to + "&SUPPLIER_SEARCH='" + supplierName + "'");
                openLinkFlag=1;
                String thicString="ALL";
                if(thic.getText().toString().equals("")){
                    thicString="ALL";
                }else {
                    thicString=thic.getText().toString().trim();
                }

                String widths="ALL";
                if(width.getText().toString().equals("")){
                    widths="ALL";
                }else {
                    widths=width.getText().toString().trim();
                }

                String lengths="ALL";
                if(length.getText().toString().equals("")){
                    lengths="ALL";
                }else {
                    lengths=length.getText().toString().trim();
                }
                String piecesA="ALL";
                if(pieces.getText().toString().equals("")){
                    piecesA="ALL";
                }else {
                    piecesA=pieces.getText().toString().trim();
                }

//                String ttnNo="ALL";
//                if(thic.getText().toString().equals("")){
//                    ttnNo="ALL";
//                }else {
//                    ttnNo=thic.getText().toString().trim();
//                }
                String truckNos="ALL";
                if(truckNo.getText().toString().equals("")){
                    truckNos="ALL";
                }else {
                    truckNos=truckNo.getText().toString().trim();
                }
                String grade="ALL";
                if(gradeText.equals("")){
                    grade="ALL";
                }else {
                    grade=gradeText;
                }

//                String locations="ALL";
//                if(location.getText().toString().equals("")){
//                    location="ALL";
//                }else {
//                    location=thic.getText().toString().trim();
//                }

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=20&SUPPLIER="+supplierName+"&FROM_DATE=" +
                         from.getText().toString().trim() + "&TO_DATE=" + to.getText().toString().trim()
                        +"&THICKNESS="+thicString
                        +"&WIDTH="+widths
                        +"&LENGTH="+lengths
                        +"&PIECES="+piecesA
                       // +"&ttnNo="+ttnNo
                        +"&TRUCK_NO="+truckNos
                        +"&GRADE="+grade.replace(" ","%20")
                        //+"&location="+location
                );
                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Log.e("url555", "" + url);
                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    try {
                        sb.append(line);
                    } catch (Exception e) {
                    }
                }

                 finalJson = sb.toString();
                Log.e("finalJson*********", url + "***" + finalJson);

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
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            if (result != null) {
                if(result.contains("DETAILS_LIST")) {
                    dataList.clear();
                    Gson gson = new Gson();
                    NewRowInfo liste = gson.fromJson(result, NewRowInfo.class);
                  //  if (!(liste.getMaster() == null)) {
                        dataList.addAll(liste.getDetailsList());
                  //  }

                    rawCount.setText("" + dataList.size());

                    adapter2 = new AcceptanceSupplierReportAdapter(AcceptanceSupplierReport.this, dataList);
                    list.setAdapter(adapter2);

                    //rejectAdd();

                }else {
                    dataList.clear();
                    rawCount.setText("" + dataList.size());
                    adapter2 = new AcceptanceSupplierReportAdapter(AcceptanceSupplierReport.this, dataList);
                    list.setAdapter(adapter2);
                }
            } else {
                dataList.clear();
                rawCount.setText("" + dataList.size());
                adapter2 = new AcceptanceSupplierReportAdapter(AcceptanceSupplierReport.this, dataList);
                list.setAdapter(adapter2);
                Toast.makeText(AcceptanceSupplierReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
            openLinkFlag=0;
            progressDialog.dismiss();
        }
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
                //adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AcceptanceSupplierReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}

