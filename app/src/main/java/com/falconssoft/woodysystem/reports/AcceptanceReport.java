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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.HorizontalListView;
import com.falconssoft.woodysystem.ItemsListAdapter4;
import com.falconssoft.woodysystem.PicturesAdapter;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.stage_one.EditPage;
import com.google.gson.Gson;

import java.io.BufferedReader;
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

public class AcceptanceReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Serializable {
    // truck Report

    private TextView textView, count, totalCubic;
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
    public static String truckNoBeforeUpdate2 = "";
    public static final String EDIT_LIST2 = "EDIT_LIST";
    public static final String EDIT_RAW2 = "EDIT_RAW";
    public double sum = 0;
    private Context previewContext, previewLinearContext;
    private String previewSerial;
    NewRowInfo newRowInfoPic;

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
        location = (Spinner) findViewById(R.id.Loding_Order_Location);
        from = (EditText) findViewById(R.id.Loding_Order_from);
        to = (EditText) findViewById(R.id.Loding_Order_to);
        truckEditText = findViewById(R.id.acceptanceInfoReport_truckNo);
        acceptorEditText = findViewById(R.id.acceptanceInfoReport_acceptor);
        ttnEditText = findViewById(R.id.acceptanceInfoReport_ttn);
        export = findViewById(R.id.acceptance_report_export);
        exportToExcel = findViewById(R.id.acceptance_report_export_Excel);
        totalCubic = findViewById(R.id.truck_report_cubic);

        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        dfReport = new SimpleDateFormat("yyyyMMdd_hhmmss");

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        adapter = new ItemsListAdapter4(AcceptanceReport.this, new ArrayList<>());
        listView.setAdapter(adapter);

        adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, new ArrayList<>(), new ArrayList<>());
        list.setAdapter(adapter2);


        export.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExportToPDF obj = new ExportToPDF(AcceptanceReport.this);
                obj.exportReportOne(details, filtered, truckString, loc, from.getText().toString(), to.getText().toString(), dfReport.format(myCalendar.getTime()));

            }
        });

        exportToExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExportToExcel.getInstance().createExcelFile(AcceptanceReport.this, "Acceptance_Report.xls", 6, filtered, details);

            }
        });

        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(linearLayout);
            }
        });

        new JSONTask().execute();

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

        from.setText("1/12/2019");
        to.setText(sdf.format(myCalendar.getTime()));

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AcceptanceReport.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(AcceptanceReport.this, openDatePickerDialog(1), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        truckEditText.addTextChangedListener(new WatchTextChange(truckEditText));
        ttnEditText.addTextChangedListener(new WatchTextChange(ttnEditText));
        acceptorEditText.addTextChangedListener(new WatchTextChange(acceptorEditText));

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

    void fillSpinnerAdapter() {

        locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        location.setAdapter(locationAdapter);
        location.setOnItemSelectedListener(this);

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

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                if (flag == 0)
                    from.setText(sdf.format(myCalendar.getTime()));
                else
                    to.setText(sdf.format(myCalendar.getTime()));

                if (!from.getText().toString().equals("") && !to.getText().toString().equals(""))
                    filters();

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

    public void filters() {

        if (linearLayout.getVisibility() == View.VISIBLE)
            slideDown(linearLayout);
        String fromDate = from.getText().toString().trim();
        String toDate = to.getText().toString();

//        Log.e("AcceptanceReport", "filter/" + loc + "/" + truckString + "/" + acceptorString + "/" + ttnString);
        try {
            sum = 0;
            filtered = new ArrayList<>();
            for (int k = 0; k < master.size(); k++)
                if ((formatDate(master.get(k).getDate()).after(formatDate(fromDate)) || formatDate(master.get(k).getDate()).equals(formatDate(fromDate))) &&
                        (formatDate(master.get(k).getDate()).before(formatDate(toDate)) || formatDate(master.get(k).getDate()).equals(formatDate(toDate))))
                    if (loc.equals("All") || loc.equals(master.get(k).getLocationOfAcceptance()))
                        if (truckString.equals("") || master.get(k).getTruckNo().toLowerCase().contains(truckString.toLowerCase()))
                            if (acceptorString.equals("") || master.get(k).getAcceptedPersonName().toLowerCase().contains(acceptorString.toLowerCase()))
                                if (ttnString.equals("") || master.get(k).getTtnNo().toLowerCase().contains(ttnString.toLowerCase())) {
                                    sum += master.get(k).getCubic();
                                    filtered.add(master.get(k));
                                }

            count.setText("" + filtered.size());
            adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, filtered, details);
            list.setAdapter(adapter2);
            totalCubic.setText("" + String.format("%.3f", sum));


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void previewPics2(NewRowInfo info, Context context) {
        previewLinearContext = context;
        new BitmapImage2().execute(info);
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
                    for (int i = 0; i < 8; i++) {

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
            Log.e("fromclass2", "exception:onPostExecute: " + pictures.getImageOne());
            if (pictures == null)
                Toast.makeText(previewLinearContext, "No image found!", Toast.LENGTH_SHORT).show();
            else
                openPicDialog2(pictures);

        }
    }

    public void openPicDialog2(NewRowInfo picts) {
        dialog = new Dialog(previewLinearContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setContentView(R.layout.pic_dialog);
        HorizontalListView listView = dialog.findViewById(R.id.listview);
        ImageView close = dialog.findViewById(R.id.picDialog_close);
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


        PicturesAdapter adapter = new PicturesAdapter(pics,null, this);
        listView.setAdapter(adapter);

        dialog.show();

    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
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

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=55");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuffer sb = new StringBuffer();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    try{
                    sb.append(line);
                    }catch (Exception e){}
                }

                String finalJson = sb.toString();
                Log.e("finalJson*********", finalJson);

//                JSONObject parentObject = new JSONObject(finalJson);

//                try {
//                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_MASTER");
                master.clear();
                details.clear();
                locationList.clear();
//                cubicList.clear();
                Gson gson = new Gson();
                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
                master.addAll(list.getMaster());
                details.addAll(list.getDetails());
//                cubicList.addAll(list.getCubicList());
                for (int i = 0; i < list.getLocationList().size(); i++)
                    locationList.add(list.getLocationList().get(i).getLocationOfAcceptance());
                locationList.add(0, "All");

//                    ttnList.clear();
//                    acceptorList.clear();
//                    truckList.clear();
//                    locationList.clear();
//                    for (int i = 0; i < parentArray.length(); i++) {
//                        JSONObject finalObject = parentArray.getJSONObject(i);
//
//                        NewRowInfo newRowInfo = new NewRowInfo();
//                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
//                        newRowInfo.setDate(finalObject.getString("DATE_OF_ACCEPTANCE"));
//                        newRowInfo.setAcceptedPersonName(finalObject.getString("NAME_OF_ACCEPTER"));
//                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));
//                        newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
//                        newRowInfo.setTotalRejectedNo(finalObject.getString("REJECTED"));
//                        newRowInfo.setSerial(finalObject.getString("SERIAL"));
//                        newRowInfo.setNetBundles(finalObject.getString("NET_BUNDLES"));
//
//                        // todo remove log
//                        Log.e("showdatamaster", finalObject.getString("TRUCK_NO") + finalObject.getString("SERIAL"));
//
//                        master.add(newRowInfo);
//                        ttnList.add(finalObject.getString("TTN_NO"));
//                        acceptorList.add(finalObject.getString("NAME_OF_ACCEPTER"));
//                        truckList.add(finalObject.getString("TRUCK_NO"));
//                        locationList.add(finalObject.getString("LOCATION_OF_ACCEPTANCE"));

//                    }

//                    removeDuplicate(ttnList);
//                    removeDuplicate(acceptorList);
//                    removeDuplicate(truckList);
//                    removeDuplicate(locationList);

//                    ttnList.add(0, "All");
//                    acceptorList.add(0, "All");
//                    truckList.add(0, "All");
//                    locationList.add(0, "All");
                rowsCount = master.size();
//
//                } catch (JSONException e) {
//                    Log.e("Import Data1", e.getMessage());
//                }

//                try {
//                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_DETAILS");//RAW_INFO_DETAILS
//                    details.clear();
//                    for (int i = 0; i < parentArray.length(); i++) {
//                        JSONObject finalObject = parentArray.getJSONObject(i);
//
//                        NewRowInfo newRowInfo = new NewRowInfo();
//                        newRowInfo.setSupplierName(finalObject.getString("SUPLIER"));
//                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
//                        newRowInfo.setThickness(finalObject.getInt("THICKNESS"));
//                        newRowInfo.setWidth(finalObject.getInt("WIDTH"));
//                        newRowInfo.setLength(finalObject.getInt("LENGTH"));
//                        newRowInfo.setNoOfPieces(finalObject.getInt("PIECES"));
//                        newRowInfo.setNoOfRejected(finalObject.getInt("REJECTED"));//REJ
//                        newRowInfo.setNoOfBundles(finalObject.getInt("NO_BUNDLES"));
//                        newRowInfo.setGrade(finalObject.getString("GRADE"));
////                      newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
//                        newRowInfo.setSerial(finalObject.getString("SERIAL"));
//                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));

                // todo remove log
//                        Log.e("showdatamix", finalObject.getString("TRUCK_NO") + finalObject.getString("SERIAL"));
//                        String pic = finalObject.getString("PART1") + finalObject.getString("PART2") +
//                                finalObject.getString("PART3") + finalObject.getString("PART4") +
//                                finalObject.getString("PART5") + finalObject.getString("PART6") +
//                                finalObject.getString("PART7") + finalObject.getString("PART8");
//
//                        pic = pic.replaceAll("null", "");
//
//                        newRowInfo.setPicture(pic);

//                        details.add(newRowInfo);
//                    }
//                } catch (JSONException e) {
//                    Log.e("Import Data2", e.getMessage().toString());
//                }


            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            }
//            catch (JSONException e) {
//                Log.e("Customer", "********ex3  " + e.toString());
//                e.printStackTrace();
//            }
            finally {
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
                if (master.size() > 0)
                    totalCubic.setText("" + master.get(0).getTotalCubic());
                else
                    totalCubic.setText("0.000");

                fillSpinnerAdapter();
                adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, master, details);
                list.setAdapter(adapter2);

            } else {
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
}

