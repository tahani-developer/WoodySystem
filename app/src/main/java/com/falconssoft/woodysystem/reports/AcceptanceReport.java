package com.falconssoft.woodysystem.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.HorizontalListView;
import com.falconssoft.woodysystem.ItemsListAdapter4;
import com.falconssoft.woodysystem.PicturesAdapter;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.stage_one.AddNewAdapter;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.falconssoft.woodysystem.stage_one.EditPage;
import com.falconssoft.woodysystem.stage_one.SuppliersAdapter;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_FLAG;

public class AcceptanceReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Serializable, View.OnClickListener {
    // truck Report

    private TextView textView, count, totalCubic, totalCubicReg, supplier, total,totalAcceptCbm;
    private static LinearLayout linearLayout;
    private EditText from, to, truckEditText, acceptorEditText, ttnEditText;
    private Button arrow, export, exportToExcel;
    private static ListView listView;
    private static List<NewRowInfo> master, details;
    private static List<Pictures> pictures;
    private Animation animation;
    static ItemsListAdapter4 adapter;
    static AcceptanceReportAdapter adapter2;
    private ListView list;
    private Calendar myCalendar;
    private Spinner location;//, truckSpinner, acceptorSpinner, ttnSpinner;
    private ArrayAdapter<String> locationAdapter, truckAdapter, acceptorAdapter, ttnAdapter;
    private String loc = "All", truckString = "", acceptorString = "", ttnString = "";
    private Settings generalSettings;
    private DatabaseHandler MHandler;
    static Dialog dialog;
    private List<String> locationList;//, truckList, acceptorList, ttnList;
    String myFormat;
    private SimpleDateFormat sdf, dfReport;
    private ProgressDialog progressDialog;
    private int rowsCount = 0;
    private List<NewRowInfo> filtered, detailsListBasedOnSerial;
    public static String truckNoBeforeUpdate2 = "", date_from = "", date_to = "";
    public static final String EDIT_LIST2 = "EDIT_LIST";
    public static final String EDIT_RAW2 = "EDIT_RAW";
    public double sum = 0, sumRej = 0;
    private Context previewContext, previewLinearContext;
    private String previewSerial;
    NewRowInfo newRowInfoPic;
    private Dialog searchDialog;
    private RecyclerView recyclerView;
    private SuppliersAdapter suppliersAdapter;
    private List<SupplierInfo> suppliers = new ArrayList<>();
    public static String supplierName = "All";
    private List<SupplierInfo> arraylist = new ArrayList<>();
    double acceptedReject=0,acceptedBundle=0;
    TextView reportTotalReject,reportTotalBundle;
    int openLinkFlag=0;

    //    public static final String EDIT_FLAG2= "EDIT_FLAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance_report);

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);

        MHandler = new DatabaseHandler(AcceptanceReport.this);
        generalSettings = MHandler.getSettings();
        dialog = new Dialog(AcceptanceReport.this);
        master = new ArrayList<>();
        details = new ArrayList<>();
        pictures = new ArrayList<>();
        locationList = new ArrayList<>();
        filtered = new ArrayList<>();

        count = findViewById(R.id.acceptanceReport_count);
        list = findViewById(R.id.list);
        textView = findViewById(R.id.loading_order_report);
        listView = findViewById(R.id.listview);
        linearLayout = findViewById(R.id.linearLayout);
        arrow = findViewById(R.id.arrow);
        location = findViewById(R.id.Loding_Order_Location);
        from = findViewById(R.id.Loding_Order_from);
        to = findViewById(R.id.Loding_Order_to);
        truckEditText = findViewById(R.id.acceptanceInfoReport_truckNo);
        acceptorEditText = findViewById(R.id.acceptanceInfoReport_acceptor);
        ttnEditText = findViewById(R.id.acceptanceInfoReport_ttn);
        export = findViewById(R.id.acceptance_report_export);
        exportToExcel = findViewById(R.id.acceptance_report_export_Excel);
        totalCubic = findViewById(R.id.truck_report_cubic);
        totalCubicReg = findViewById(R.id.truck_report_cubic_rej);
        totalAcceptCbm = findViewById(R.id.Report_total_accept_cbm);
        supplier = findViewById(R.id.truck_report_supplier);
        reportTotalReject=findViewById(R.id.Report_total_rejected);
        reportTotalBundle=findViewById(R.id.Report_total_bundles);

        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        dfReport = new SimpleDateFormat("yyyyMMdd_hhmmss");

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        adapter = new ItemsListAdapter4(AcceptanceReport.this, new ArrayList<>());
        listView.setAdapter(adapter);

        adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, new ArrayList<>(), new ArrayList<>());
        list.setAdapter(adapter2);

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = parent.getSelectedItem().toString();
//                filters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        myCalendar = Calendar.getInstance();

        from.setText(sdf.format(myCalendar.getTime()));//"1/12/2019"
        to.setText(sdf.format(myCalendar.getTime()));
        date_from = sdf.format(myCalendar.getTime());
        date_to = sdf.format(myCalendar.getTime());
        truckEditText.addTextChangedListener(new WatchTextChange(truckEditText));
        ttnEditText.addTextChangedListener(new WatchTextChange(ttnEditText));
        acceptorEditText.addTextChangedListener(new WatchTextChange(acceptorEditText));

        supplier.setOnClickListener(this);
        export.setOnClickListener(this);
        exportToExcel.setOnClickListener(this);
        arrow.setOnClickListener(this);
        from.setOnClickListener(this);
        to.setOnClickListener(this);

        new JSONTask().execute();
        new JSONTask1().execute();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.truck_report_supplier:
//                suppliers.clear();

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
                suppliersAdapter = new SuppliersAdapter(null, suppliers, null, this,0);
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
                break;
            case R.id.acceptance_report_export:
                ExportToPDF obj = new ExportToPDF(AcceptanceReport.this);
                obj.exportReportOne(details, filtered, truckString, loc, from.getText().toString(), to.getText().toString(), dfReport.format(myCalendar.getTime()),totalCubic.getText().toString(),totalCubicReg.getText().toString(),reportTotalBundle.getText().toString(),reportTotalReject.getText().toString(),totalAcceptCbm.getText().toString());
                break;
            case R.id.acceptance_report_export_Excel:
                ExportToExcel.getInstance().createExcelFile(AcceptanceReport.this, "Acceptance_Report.xls", 6, filtered, details);
                break;
            case R.id.arrow:
                slideDown(linearLayout);
                break;
            case R.id.Loding_Order_from:
                openLinkFlag=0;
               date(0);
                break;
            case R.id.Loding_Order_to:
                openLinkFlag=0;
                date(1);
                break;
        }
    }


    public void date (int flag){
        new DatePickerDialog(AcceptanceReport.this, openDatePickerDialog(flag), myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void goToEditPage(NewRowInfo newRowInfo) {
        Intent intent = new Intent(AcceptanceReport.this, EditPage.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_RAW2, newRowInfo);
//        bundle.putParcelable(EDIT_LIST, list);
        intent.putExtras(bundle);
        intent.putExtra(EDIT_FLAG, 11);
//        intent.putExtra(EDIT_LIST2, (Serializable) list);
        Log.e("look", "" + details.get(0).getTruckNo());
        startActivity(intent);

    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        supplierName = supplierNameLocal;
        supplier.setText(supplierName);
        searchDialog.dismiss();
        new JSONTask().execute();
    }

    void fillSpinnerAdapter() {

        locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        location.setAdapter(locationAdapter);
        location.setOnItemSelectedListener(this);

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
        suppliersAdapter = new SuppliersAdapter(null, arraylist, null, this,0);
        recyclerView.setAdapter(suppliersAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        loc = parent.getItemAtPosition(position).toString();
        filters();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    class WatchTextChange implements TextWatcher {

        private View view;

        public WatchTextChange(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.acceptanceInfoReport_truckNo:
                    truckString = String.valueOf(s);
                    filters();
                    break;
                case R.id.acceptanceInfoReport_acceptor:
                    acceptorString = String.valueOf(s);
                    filters();
                    break;
                case R.id.acceptanceInfoReport_ttn:
                    ttnString = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    public void previewLinear(String serial, Context context) {
        previewContext = context;
        previewSerial = serial;
        new JSONTaskDetails().execute();
//        rawInfos = new ArrayList<>();
//
//        for (int i = 0; i < details.size(); i++) {
//            Log.e("acceptanceReport", "/truck/" + truckString + "/truckd/" + details.get(i).getTruckNo()
//                    + "/serial/" + details.get(i).getSerial() + "/seriald/" + newRowInfo.getSerial() + "/thickness/" + newRowInfo.getThickness());
//            if (details.get(i).getTruckNo().equals(newRowInfo.getTruckNo())
//                    && details.get(i).getSerial().equals(newRowInfo.getSerial())
//            ) {
////                Log.e("acceptanceReport", "/truck/" + truckString + "/truckd/" + details.get(i).getTruckNo()
////                        + "/serial/" + details.get(i).getSerial() + "/seriald/" + newRowInfo.getSerial());
//
//                rawInfos.add(new NewRowInfo(
//                        details.get(i).getSupplierName(),
//                        details.get(i).getThickness(),
//                        details.get(i).getWidth(),
//                        details.get(i).getLength(),
//                        details.get(i).getNoOfPieces(),
//                        details.get(i).getNoOfRejected(),
//                        details.get(i).getNoOfBundles(),
//                        details.get(i).getGrade(),
//                        details.get(i).getTruckNo(),
//                        details.get(i).getSerial()
//                ));
//
//            }
//        }
//
//        Log.e("ooo  ", "" + rawInfos.size());
//        adapter = new ItemsListAdapter4(Context, rawInfos);
//        listView.setAdapter(adapter);
//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
////                openLargePicDialog(StringToBitMap(bundleInfos.get(position).getPicture()) , Context);
//            }
//        });
//
//        try {
//            Thread.sleep(300);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        slideUp(linearLayout);

    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                if (flag == 0)
                    from.setText(sdf.format(myCalendar.getTime()));
                else
                    to.setText(sdf.format(myCalendar.getTime()));

//                if (!from.getText().toString().equals("") && !to.getText().toString().equals(""))
//                    filters();
                date_from = from.getText().toString();
                date_to = to.getText().toString();

                Log.e("url555222","in "+(view.getId()==R.id.Loding_Order_from)+"   "+(view.getId()==R.id.Loding_Order_to));

                if(openLinkFlag==0) {
                    openLinkFlag=1;
                    new JSONTask().execute();
                }

            }

        };
        return date;
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
//        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setStartTime(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);
    }

    public void slideDown(View view) {
        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight() + 3);                // toYDelta
        animate.setDuration(500);
        animate.setStartTime(200);
//        animate.setFillAfter(true);
        view.startAnimation(animate);

    }

    public Date formatDate(String date) throws ParseException {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date d = sdf.parse(date);
        return d;
    }

    void getRejectNetBundle(){
        Log.e("rejjj1",""+master.size());
        acceptedReject=0;
        acceptedBundle=0;
    for (int i=0;i<master.size();i++){

        acceptedReject+=Double.parseDouble(master.get(i).getTotalRejectedNo());
        acceptedBundle+=Double.parseDouble(master.get(i).getNetBundles());
        Log.e("rejjj",""+acceptedBundle+"   "+acceptedReject);

        Log.e("rejjj2",""+master.get(i).getNoOfBundles());

    }

        reportTotalReject.setText(""+acceptedReject);
        reportTotalBundle.setText(""+acceptedBundle);

    }

    public void filters() {

        if (linearLayout.getVisibility() == View.VISIBLE)
            slideDown(linearLayout);
        String fromDate = from.getText().toString().trim();
        String toDate = to.getText().toString();

//        Log.e("AcceptanceReport", "filter/" + loc + "/" + truckString + "/" + acceptorString + "/" + ttnString);
        try {
            sum = 0;
            sumRej = 0;
            filtered = new ArrayList<>();
            for (int k = 0; k < master.size(); k++)
                if ((formatDate(master.get(k).getDate()).after(formatDate(fromDate)) || formatDate(master.get(k).getDate()).equals(formatDate(fromDate))) &&
                        (formatDate(master.get(k).getDate()).before(formatDate(toDate)) || formatDate(master.get(k).getDate()).equals(formatDate(toDate))))
                    if (loc.equals("All") || loc.equals(master.get(k).getLocationOfAcceptance()))
                        if (truckString.equals("") || master.get(k).getTruckNo().toLowerCase().contains(truckString.toLowerCase()))
                            if (acceptorString.equals("") || master.get(k).getAcceptedPersonName().toLowerCase().contains(acceptorString.toLowerCase()))
                                if (ttnString.equals("") || master.get(k).getTtnNo().toLowerCase().contains(ttnString.toLowerCase())) {
                                    sum += master.get(k).getCubic();
                                    sumRej += master.get(k).getCubicRej();
                                    filtered.add(master.get(k));
                                }

            count.setText("" + filtered.size());
            adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, filtered, details);
            list.setAdapter(adapter2);
            totalCubic.setText("" + String.format("%.3f", sum));
            totalCubicReg.setText("" + String.format("%.3f", sumRej));
            double acc=0;
            acc=sum-sumRej;
            totalAcceptCbm.setText("" + String.format("%.3f", acc));
            getRejectNetBundle();


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void previewPics2(NewRowInfo info, Context context) {
        previewLinearContext = context;
        new BitmapImage2().execute(info);
        progressDialog.show();
    }

    private class BitmapImage2 extends AsyncTask<NewRowInfo, String, NewRowInfo> {
        Settings generalSettings = new DatabaseHandler(previewLinearContext).getSettings();

        @Override
        protected NewRowInfo doInBackground(NewRowInfo... pictures) {

            newRowInfoPic = pictures[0];
            URL url;
            Bitmap bitmap;
            try {
                if (!newRowInfoPic.equals("null")) {
                    for (int i = 0; i < 15; i++) {

                        switch (i) {
                            case 0:
                                if (pictures[0].getImageOne() != null) {//http://192.168.2.17:8088/woody/images/2342_img_1.png
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageOne());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic11(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic11(bitmap);
                                    }
                                }
                                break;
                            case 1:
                                if (pictures[0].getImageTwo() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageTwo());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic22(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic22(bitmap);
                                    }
                                }
                                break;
                            case 2:
                                if (pictures[0].getImageThree() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageThree());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic33(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic33(bitmap);
                                    }
                                }
                                break;
                            case 3:
                                if (pictures[0].getImageFour() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFour());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic44(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic44(bitmap);
                                    }
                                }
                                break;
                            case 4:
                                if (pictures[0].getImageFive() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFive());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic55(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic55(bitmap);
                                    }
                                }
                                break;
                            case 5:
                                if (pictures[0].getImageSix() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSix());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic66(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic66(bitmap);
                                    }
                                }
                                break;
                            case 6:
                                if (pictures[0].getImageSeven() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSeven());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic77(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic77(bitmap);
                                    }
                                }
                                break;
                            case 7:
                                if (pictures[0].getImageEight() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageEight());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic88(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 8:
                                if (pictures[0].getImage9() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage9());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic99(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;



                            case 9:
                                if (pictures[0].getImage10() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage10());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1010(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 10:
                                if (pictures[0].getImage11() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage11());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1111(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 11:
                                if (pictures[0].getImage12() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage12());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1212(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 12:
                                if (pictures[0].getImage13() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage13());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1313(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 13:
                                if (pictures[0].getImage14() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage14());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1414(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 14:

                                if (pictures[0].getImage15() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage15());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1515(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("fromclass2", "exception:doInBackground " + e.getMessage());
                return null;
            }
            return newRowInfoPic;// BitmapFactory.decodeStream(in);
        }

        @Override
        protected void onPostExecute(NewRowInfo pictures) {
            progressDialog.dismiss();
            Log.e("fromclass2", "exception:onPostExecute: " + pictures.getImageOne());
            if (pictures == null)
                Toast.makeText(previewLinearContext, "No image found!", Toast.LENGTH_SHORT).show();
            else
                openPicDialog2(pictures);

        }
    }

    public void openPicDialog2(NewRowInfo picts) {
        dialog = new Dialog(previewLinearContext,R.style.Theme_Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.pic_dialog);
        ListView listView = dialog.findViewById(R.id.listview);
        ImageView close = dialog.findViewById(R.id.picDialog_close);
        PhotoView photoView = dialog.findViewById(R.id.pic_dialog_image);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newRowInfoPic.setPic11(null);
                newRowInfoPic.setPic22(null);
                newRowInfoPic.setPic33(null);
                newRowInfoPic.setPic44(null);
                newRowInfoPic.setPic55(null);
                newRowInfoPic.setPic66(null);
                newRowInfoPic.setPic77(null);
                newRowInfoPic.setPic88(null);

                newRowInfoPic.setPic99(null);
                newRowInfoPic.setPic1010(null);
                newRowInfoPic.setPic1111(null);
                newRowInfoPic.setPic1212(null);
                newRowInfoPic.setPic1313(null);
                newRowInfoPic.setPic1414(null);
                newRowInfoPic.setPic1515(null);

                dialog.dismiss();
            }
        });

        ArrayList<Bitmap> pics = new ArrayList<>();
        pics.add(picts.getPic11());
        pics.add(picts.getPic22());
        pics.add(picts.getPic33());
        pics.add(picts.getPic44());
        pics.add(picts.getPic55());
        pics.add(picts.getPic66());
        pics.add(picts.getPic77());
        pics.add(picts.getPic88());

        pics.add(picts.getPic99());
        pics.add(picts.getPic1010());
        pics.add(picts.getPic1111());
        pics.add(picts.getPic1212());
        pics.add(picts.getPic1313());
        pics.add(picts.getPic1414());
        pics.add(picts.getPic1515());

        PicturesAdapter adapter = new PicturesAdapter(pics, null, this);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.e("bitmap", "" + (Bitmap) adapter.getItem(i));
                if (pics.get(i) == null)
                    photoView.setImageResource(R.drawable.pic);
                else
                    photoView.setImageBitmap(pics.get(i));
            }
        });

        dialog.show();

    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void fillData(NewRowInfo info) {
        master.clear();
        details.clear();
        locationList.clear();
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add(0, "All");
//        for (int i = 0; i < info.getLocationList().size(); i++)
//            locationList.add(info.getLocationList().get(i).getLocationOfAcceptance());
//        locationList.add(0, "All");

        rowsCount = master.size();
        count.setText("" + rowsCount);
        if (master.size() > 0) {
            totalCubic.setText("" + master.get(0).getTotalCubic());
            totalCubicReg.setText("" + master.get(0).getTotalCubicRej());
        }else {
            totalCubic.setText("0.000");
            totalCubicReg.setText("0.000");
        }
        fillSpinnerAdapter();
        getRejectNetBundle();
        adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, master, details);
        list.setAdapter(adapter2);
        progressDialog.dismiss();
    }

    // ******************************************** GET DATA *****************************************
    private class JSONTask extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
//            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                // http://192.168.2.17:8088/woody/import.php?FLAG=55
                // http://5.189.130.98:8085/import.php?FLAG=5

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=55&DATE_FROM="
                        + date_from + "&DATE_TO=" + date_to + "&SUPPLIER_SEARCH='" + supplierName + "'");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);
                Log.e("url555", ""+url );
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

                String finalJson = sb.toString();
                Log.e("finalJson*********", url + "***" + finalJson);

                master.clear();
                details.clear();
                locationList.clear();
//                cubicList.clear();
                Gson gson = new Gson();
                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
                if (!(list.getMaster() == null)) {
                    master.addAll(list.getMaster());
                    details.addAll(list.getDetails());
                }
//                cubicList.addAll(list.getCubicList());
//                for (int i = 0; i < list.getLocationList().size(); i++)
//                    locationList.add(list.getLocationList().get(i).getLocationOfAcceptance());
//                locationList.add(0, "All");

                locationList.add("Kalinovka");
                locationList.add("Rudniya Store");
                locationList.add(0, "All");
                rowsCount = master.size();

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
            return master;
        }


        @Override
        protected void onPostExecute(final List<NewRowInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                count.setText("" + rowsCount);
                if (master.size() > 0) {
                    totalCubic.setText("" + master.get(0).getTotalCubic());
                    totalCubicReg.setText("" + master.get(0).getTotalCubicRej());
                    double acc=0;

                    acc=(master.get(0).getTotalCubic()-master.get(0).getTotalCubicRej());
                    acc=Double.parseDouble(String.format("%.3f", acc));
                    Log.e("totalCubic", master.get(0).getTotalCubic() +"  - " + master.get(0).getTotalCubicRej()+"  "+acc);

                    totalAcceptCbm.setText(""+acc);
                } else {
                    totalCubic.setText("0.000");
                    totalCubicReg.setText("0.000");
                    totalAcceptCbm.setText("0.000");
                }

                fillSpinnerAdapter();
                adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, master, details);
                list.setAdapter(adapter2);

                openLinkFlag=0;

            } else {
                openLinkFlag=0;
                Toast.makeText(AcceptanceReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    // ******************************************** GET Details *****************************************
    private class JSONTaskDetails extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            progressDialog.show();

        }

        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            detailsListBasedOnSerial = new ArrayList<>();

            try {
                // http://192.168.2.17/woody/import.php?FLAG=6&SERIAL=
                // http://5.189.130.98:8085/import.php?FLAG=5

                URL url = new URL("http://" + new DatabaseHandler(previewContext).getSettings().getIpAddress() + "/import.php?FLAG=6&SERIAL=" + previewSerial);
                Log.e("details", "" + url);
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

                Gson gson = new Gson();
                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
                detailsListBasedOnSerial.addAll(list.getDetailsList());
                Log.e("finalJson details", "" + detailsListBasedOnSerial.size());

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
            return detailsListBasedOnSerial;
        }


        @Override
        protected void onPostExecute(final List<NewRowInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
//                Log.e("result", "*****************" + master.size());
                adapter = new ItemsListAdapter4(previewContext, detailsListBasedOnSerial);
                listView.setAdapter(adapter);
                slideUp(linearLayout);

            } else {
//                Toast.makeText(AcceptanceReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
//            progressDialog.dismiss();
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
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AcceptanceReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
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


}

