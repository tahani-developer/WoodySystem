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
import android.util.Base64;
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
import com.falconssoft.woodysystem.HorizontalListView;
import com.falconssoft.woodysystem.ItemsListAdapter4;
import com.falconssoft.woodysystem.PicturesAdapter;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;

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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_FLAG;

public class AcceptanceReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    // Report 1

    private TextView textView, count;
    private static LinearLayout linearLayout;
    private EditText from, to;
    private Button arrow;
    private static ListView listView;
    private static List<NewRowInfo> master, details;
    private static List<Pictures> pictures;
    private Animation animation;
    static ItemsListAdapter4 adapter;
    static AcceptanceReportAdapter adapter2;
    private ListView list;
    private Calendar myCalendar;
    private Spinner location, truckSpinner, acceptorSpinner, ttnSpinner;
    private ArrayAdapter<String> locationAdapter, truckAdapter, acceptorAdapter, ttnAdapter;
    private String loc = "All", truckString = "All", acceptorString = "All", ttnString = "All";
    private Settings generalSettings;
    private String orderNo;
    private JSONArray bundleNo = new JSONArray();
    private DatabaseHandler MHandler;
    List<NewRowInfo> rawInfos;
    static Dialog dialog;
    private List<String> locationList, truckList, acceptorList, ttnList;
    String myFormat;
    SimpleDateFormat sdf;
    private ProgressDialog progressDialog;
    private int rowsCount = 0;
    private List<NewRowInfo> filtered;
    public static String truckNoBeforeUpdate2 = "";
    public static final String EDIT_LIST2 = "EDIT_LIST";
    public static final String EDIT_RAW2 = "EDIT_RAW";

    //    public static final String EDIT_FLAG2= "EDIT_FLAG";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acceptance_report);

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");

        MHandler = new DatabaseHandler(AcceptanceReport.this);
        generalSettings = MHandler.getSettings();
        dialog = new Dialog(AcceptanceReport.this);
        master = new ArrayList<>();
        details = new ArrayList<>();
        pictures = new ArrayList<>();
        truckList = new ArrayList<>();
        acceptorList = new ArrayList<>();
        ttnList = new ArrayList<>();
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
        truckSpinner = findViewById(R.id.acceptanceInfoReport_truckNo);
        acceptorSpinner = findViewById(R.id.acceptanceInfoReport_acceptor);
        ttnSpinner = findViewById(R.id.acceptanceInfoReport_ttn);

        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);


        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        adapter = new ItemsListAdapter4(AcceptanceReport.this, new ArrayList<>());
        listView.setAdapter(adapter);

        adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, new ArrayList<>(), new ArrayList<>());
        list.setAdapter(adapter2);


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

    }

    public void goToEditPage(NewRowInfo newRowInfo) {
        List<NewRowInfo> list = new ArrayList<>();
        for (int i = 0; i < details.size(); i++)
            if (details.get(i).getTruckNo().equals(newRowInfo.getTruckNo())
                    && details.get(i).getSerial().equals(newRowInfo.getSerial())
            )
                list.add(details.get(i));

//        if (!(details.get(i).getThickness() == newRowInfo.getThickness()
//                && details.get(i).getLength() == newRowInfo.getLength()
//                && details.get(i).getWidth() == newRowInfo.getWidth()
//                && details.get(i).getNoOfPieces() == newRowInfo.getNoOfPieces()
//                && details.get(i).getGrade() == newRowInfo.getGrade()
//                && details.get(i).getNoOfRejected() == newRowInfo.getNoOfRejected()
//                && details.get(i).getNoOfBundles() == newRowInfo.getNoOfBundles()
//                && details.get(i).getSupplierName() == newRowInfo.getSupplierName())
//        )
        Intent intent = new Intent(AcceptanceReport.this, AddNewRaw.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_RAW2, newRowInfo);
//        bundle.putParcelable(EDIT_LIST, list);
        intent.putExtras(bundle);
        intent.putExtra(EDIT_FLAG, 11);
        intent.putExtra(EDIT_LIST2, (Serializable) list);
        //todo remove
        Log.e("look", "" + details.get(0).getTruckNo());
        startActivity(intent);

    }

    void fillSpinnerAdapter() {

        locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
        locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        location.setAdapter(locationAdapter);
        location.setOnItemSelectedListener(this);

        truckAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, truckList);
        truckAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        truckSpinner.setAdapter(truckAdapter);
        truckSpinner.setOnItemSelectedListener(this);

        acceptorAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, acceptorList);
        acceptorAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        acceptorSpinner.setAdapter(acceptorAdapter);
        acceptorSpinner.setOnItemSelectedListener(this);

        ttnAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, ttnList);
        ttnAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        ttnSpinner.setAdapter(ttnAdapter);
        ttnSpinner.setOnItemSelectedListener(this);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
        switch (parent.getId()) {
            case R.id.acceptanceInfoReport_truckNo:
                truckString = parent.getItemAtPosition(position).toString();
                // todo remove log
                Log.e("showtruck", truckString);
                filters();
                break;
            case R.id.acceptanceInfoReport_acceptor:
                acceptorString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfoReport_ttn:
                ttnString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.Loding_Order_Location:
                loc = parent.getItemAtPosition(position).toString();
                filters();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    // ******************************************** GET DATA *****************************************
    private class JSONTask extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();

        }

        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.214/woody/import.php?FLAG=5
                //                http://5.189.130.98:8085/import.php?FLAG=5

                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=5");

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
                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_MASTER");
                    master.clear();
                    ttnList.clear();
                    acceptorList.clear();
                    truckList.clear();
                    locationList.clear();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        NewRowInfo newRowInfo = new NewRowInfo();
                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
                        newRowInfo.setDate(finalObject.getString("DATE_OF_ACCEPTANCE"));
                        newRowInfo.setAcceptedPersonName(finalObject.getString("NAME_OF_ACCEPTER"));
                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));
                        newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
                        newRowInfo.setTotalRejectedNo(finalObject.getString("REJECTED"));
                        newRowInfo.setSerial(finalObject.getString("SERIAL"));
                        newRowInfo.setNetBundles(finalObject.getString("NET_BUNDLES"));

                        // todo remove log
                        Log.e("showdatamaster", finalObject.getString("TRUCK_NO") + finalObject.getString("SERIAL"));

                        master.add(newRowInfo);
                        ttnList.add(finalObject.getString("TTN_NO"));
                        acceptorList.add(finalObject.getString("NAME_OF_ACCEPTER"));
                        truckList.add(finalObject.getString("TRUCK_NO"));
                        locationList.add(finalObject.getString("LOCATION_OF_ACCEPTANCE"));

                    }

                    removeDuplicate(ttnList);
                    removeDuplicate(acceptorList);
                    removeDuplicate(truckList);
                    removeDuplicate(locationList);

                    ttnList.add(0, "All");
                    acceptorList.add(0, "All");
                    truckList.add(0, "All");
                    locationList.add(0, "All");
                    rowsCount = master.size();

                } catch (JSONException e) {
                    Log.e("Import Data1", e.getMessage());
                }

                try {
                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_DETAILS");//RAW_INFO_DETAILS
                    details.clear();
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        NewRowInfo newRowInfo = new NewRowInfo();
                        newRowInfo.setSupplierName(finalObject.getString("SUPLIER"));
                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
                        newRowInfo.setThickness(finalObject.getInt("THICKNESS"));
                        newRowInfo.setWidth(finalObject.getInt("WIDTH"));
                        newRowInfo.setLength(finalObject.getInt("LENGTH"));
                        newRowInfo.setNoOfPieces(finalObject.getInt("PIECES"));
                        newRowInfo.setNoOfRejected(finalObject.getInt("REJECTED"));//REJ
                        newRowInfo.setNoOfBundles(finalObject.getInt("NO_BUNDLES"));
                        newRowInfo.setGrade(finalObject.getString("GRADE"));
//                        newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
                        newRowInfo.setSerial(finalObject.getString("SERIAL"));
//                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));

                        // todo remove log
                        Log.e("showdatamix", finalObject.getString("TRUCK_NO") + finalObject.getString("SERIAL"));
//                        String pic = finalObject.getString("PART1") + finalObject.getString("PART2") +
//                                finalObject.getString("PART3") + finalObject.getString("PART4") +
//                                finalObject.getString("PART5") + finalObject.getString("PART6") +
//                                finalObject.getString("PART7") + finalObject.getString("PART8");
//
//                        pic = pic.replaceAll("null", "");
//
//                        newRowInfo.setPicture(pic);

                        details.add(newRowInfo);
                    }
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
            return master;
        }


        @Override
        protected void onPostExecute(final List<NewRowInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + master.size());
                count.setText("" + rowsCount);
                fillSpinnerAdapter();
                adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, master, details);
                list.setAdapter(adapter2);

            } else {
                Toast.makeText(AcceptanceReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
            progressDialog.dismiss();
        }
    }

    public void previewLinear(NewRowInfo newRowInfo, Context Context, List<NewRowInfo> details) {

        rawInfos = new ArrayList<>();

        for (int i = 0; i < details.size(); i++) {
            Log.e("acceptanceReport", "/truck/" + truckString + "/truckd/" + details.get(i).getTruckNo()
                    + "/serial/" + details.get(i).getSerial() + "/seriald/" + newRowInfo.getSerial() + "/thickness/" + newRowInfo.getThickness());
            if (details.get(i).getTruckNo().equals(newRowInfo.getTruckNo())
                    && details.get(i).getSerial().equals(newRowInfo.getSerial())
            ) {
//                Log.e("acceptanceReport", "/truck/" + truckString + "/truckd/" + details.get(i).getTruckNo()
//                        + "/serial/" + details.get(i).getSerial() + "/seriald/" + newRowInfo.getSerial());

                rawInfos.add(new NewRowInfo(
                        details.get(i).getSupplierName(),
                        details.get(i).getThickness(),
                        details.get(i).getWidth(),
                        details.get(i).getLength(),
                        details.get(i).getNoOfPieces(),
                        details.get(i).getNoOfRejected(),
                        details.get(i).getNoOfBundles(),
                        details.get(i).getGrade(),
                        details.get(i).getTruckNo(),
                        details.get(i).getSerial()
                ));

            }
        }

        Log.e("ooo  ", "" + rawInfos.size());
        adapter = new ItemsListAdapter4(Context, rawInfos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                openLargePicDialog(StringToBitMap(bundleInfos.get(position).getPicture()) , Context);
            }
        });

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        slideUp(linearLayout);

    }

    void removeDuplicate(List<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
    }

    public void previewPics(int index, Context context) {
        Pictures pics = new Pictures();
        for (int i = 0; i < pictures.size(); i++) {
//            if (pictures.get(i).getOrderNo().equals(orders.get(index).getOrderNo())) {
//                pics = pictures.get(i);
//                break;
//            }
        }
        openPicDialog(pics, context);
    }

    public void openLargePicDialog(Bitmap picts, Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pic_dialog2);
        dialog.setCanceledOnTouchOutside(true);

        ImageView imageView = dialog.findViewById(R.id.main_pic);
        imageView.setImageBitmap(picts);

        dialog.show();

    }

    public void openPicDialog(Pictures picts, Context context) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pic_dialog);
        dialog.setCanceledOnTouchOutside(true);


        HorizontalListView listView = dialog.findViewById(R.id.listview);

        ArrayList<Bitmap> pics = new ArrayList<>();
        pics.add(StringToBitMap(picts.getPic1()));
        pics.add(StringToBitMap(picts.getPic2()));
        pics.add(StringToBitMap(picts.getPic3()));
        pics.add(StringToBitMap(picts.getPic4()));
        pics.add(StringToBitMap(picts.getPic5()));
        pics.add(StringToBitMap(picts.getPic6()));
        pics.add(StringToBitMap(picts.getPic7()));
        pics.add(StringToBitMap(picts.getPic8()));

        PicturesAdapter adapter = new PicturesAdapter(context, pics);
        listView.setAdapter(adapter);

        dialog.show();

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

    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    private class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("DELETE_ORDER", "1"));
                nameValuePairs.add(new BasicNameValuePair("ORDER_NO", orderNo));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", bundleNo.toString()));

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
                if (s.contains("DELETE ORDER SUCCESS")) {
                    MHandler.deleteOrder(orderNo);
                } else {
                    Toast.makeText(AcceptanceReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AcceptanceReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
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
            filtered = new ArrayList<>();
            for (int k = 0; k < master.size(); k++) {
//                Log.e("AcceptanceReport", "filter/" + loc + "/" + truckString + "/"
//                        + acceptorString + "/" + ttnString
//                        + "/" + master.size()
//                        + "/" + (truckString.equals("All") || truckString.equals(master.get(k).getTruckNo())));

//                Log.e("****", fromDate + "  " + master.get(k).getDate());
                if ((formatDate(master.get(k).getDate()).after(formatDate(fromDate)) || formatDate(master.get(k).getDate()).equals(formatDate(fromDate))) &&
                        (formatDate(master.get(k).getDate()).before(formatDate(toDate)) || formatDate(master.get(k).getDate()).equals(formatDate(toDate))))
                    if (loc.equals("All") || loc.equals(master.get(k).getLocationOfAcceptance()))
                        if (truckString.equals("All") || truckString.equals(master.get(k).getTruckNo()))
                            if (acceptorString.equals("All") || acceptorString.equals(master.get(k).getAcceptedPersonName()))
                                if (ttnString.equals("All") || ttnString.equals(master.get(k).getTtnNo()))
                                    filtered.add(master.get(k));

            }

            adapter2 = new AcceptanceReportAdapter(AcceptanceReport.this, filtered, details);
            list.setAdapter(adapter2);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}

