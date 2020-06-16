package com.falconssoft.woodysystem.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.NewRowInfo;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static com.falconssoft.woodysystem.stage_one.AddNewRaw.serialBeforeUpdate;
import static com.falconssoft.woodysystem.stage_one.AddNewRaw.truckNoBeforeUpdate;

public class AcceptanceInfoReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Settings generalSettings;
    private DatabaseHandler databaseHandler;
    private AcceptanceInfoReportAdapter adapter;
    private static List<NewRowInfo> master, details, filtered, dateFiltered;
    private ListView listView;
    private Spinner suppliersSpinner, gradeSpinner, truckSpinner, acceptorSpinner, ttnSpinner, accLocationSpinner;
    private List<String> suppliersList, gradeList, truckList, acceptorList, ttnList, accLocationList;//, locationList
    private ArrayAdapter<String> suppliersAdapter, locationAdapter, gradeAdapter, truckAdapter, acceptorAdapter, ttnAdapter, accLocationAdapter;
    private EditText fromThickness, toThickness, fromWidth, toWidth, fromLength, toLength;
    private TextView fromDate, toDate, bundleNo, noOfPieces, cubic, delete;
    private double noOfPiecesSum, cubicSum;
    private String gradeString = "", supplierString = "", truckString = "", acceptorString = ""
            , ttnString = "", accLocationString = "", fromTime = "", toTime = "";
    private Date date;
    private Calendar calendar;
    private int timeFlag = 0;// 0=> from, 1=> to
    private CheckBox allCheckBox;
    private String fromLengthNo = "", toLengthNo = "", fromWidthNo = "", toWidthNo = "", fromThicknessNo = "", toThicknessNo = "";
    private JSONArray jsonArray;
    private Snackbar snackbar;
    private ConstraintLayout containerLayout;
    private List<NewRowInfo> selected;
    public static final String EDIT_LIST = "EDIT_LIST";
    public static final String EDIT_RAW = "EDIT_RAW";
    public static final String EDIT_FLAG = "EDIT_FLAG";

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_info_report);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waiting...");

        databaseHandler = new DatabaseHandler(this);
        generalSettings = databaseHandler.getSettings();
//        map = new HashMap<>();
        jsonArray = new JSONArray();
        master = new ArrayList<>();
        details = new ArrayList<>();
        suppliersList = new ArrayList<>();
        ttnList = new ArrayList<>();
        acceptorList = new ArrayList<>();
        accLocationList = new ArrayList<>();
        truckList = new ArrayList<>();
        gradeList = new ArrayList<>();
//        locationList = new ArrayList<>();

        listView = findViewById(R.id.acceptanceInfo_list);
        truckSpinner = findViewById(R.id.acceptanceInfo_report_truckNo);
        acceptorSpinner = findViewById(R.id.acceptanceInfo_report_acceptor);
        ttnSpinner = findViewById(R.id.acceptanceInfo_report_ttn);
        suppliersSpinner = findViewById(R.id.acceptanceInfo_report_supplier);
        gradeSpinner = findViewById(R.id.acceptanceInfo_report_grade);
        accLocationSpinner = findViewById(R.id.acceptanceInfo_report_location);
        delete = findViewById(R.id.acceptanceInfo_report_delete);
        containerLayout = findViewById(R.id.acceptanceInfo_report_containerLayout);

        fromThickness = findViewById(R.id.acceptanceInfo_report_fromThick);
        toThickness = findViewById(R.id.acceptanceInfo_report_toThick);
        fromWidth = findViewById(R.id.acceptanceInfo_report_fromWidth);
        toWidth = findViewById(R.id.acceptanceInfo_report_toWidth);
        fromLength = findViewById(R.id.acceptanceInfo_report_fromLength);
        toLength = findViewById(R.id.acceptanceInfo_report_toLength);
        fromDate = findViewById(R.id.acceptanceInfo_report_from_date);
        toDate = findViewById(R.id.acceptanceInfo_report_to_date);
        bundleNo = findViewById(R.id.acceptanceInfo_report_no_bundles);
        noOfPieces = findViewById(R.id.acceptanceInfo_report_no_pieces);
        cubic = findViewById(R.id.acceptanceInfo_report_cubic);
        allCheckBox = findViewById(R.id.acceptanceInfo_report_checkBox);
        allCheckBox.setChecked(false);

        date = Calendar.getInstance().getTime();
        calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        fromDate.setText("1/12/2019");
        toDate.setText(dateFormat.format(date));

        adapter = new AcceptanceInfoReportAdapter(AcceptanceInfoReport.this, details);
        new JSONTask().execute();
        new JSONTask1().execute();

        fromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFlag = 0;
                new DatePickerDialog(AcceptanceInfoReport.this, openDatePickerDialog(timeFlag), calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        toDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeFlag = 1;
                new DatePickerDialog(AcceptanceInfoReport.this, openDatePickerDialog(timeFlag), calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        allCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (allCheckBox.isChecked()) {
                    for (int i = 0; i < filtered.size(); i++) {
                        filtered.get(i).setChecked(true);
                    }
                } else {
                    for (int i = 0; i < filtered.size(); i++) {
                        filtered.get(i).setChecked(false);
                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                selected = new AcceptanceInfoReportAdapter().getSelectedItems();
                if (allCheckBox.isChecked() || selected.size() > 0) {

                    Dialog passwordDialog = new Dialog(AcceptanceInfoReport.this);
                    passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    passwordDialog.setContentView(R.layout.password_dialog);
                    passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
                    TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

                    done.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Log.e("selected", "" + selected.size() + (selected.size() > 0) + (allCheckBox.isChecked()));

                            if (password.getText().toString().equals("301190")) {
//                            bundleInfoForPrint.clear();
                                jsonArray = new JSONArray();
                                if (allCheckBox.isChecked()) {
                                    for (int i = 0; i < filtered.size(); i++) {
                                        jsonArray.put(filtered.get(i).getJsonData());
                                    }
                                } else {
                                    for (int i = 0; i < selected.size(); i++) {
                                        if (selected.get(i).getChecked()) {
                                            Log.e("selected array", "" + selected.get(i).getThickness());
                                            jsonArray.put(selected.get(i).getJsonData());
//                                    bundleInfoForPrint.add(selected.get(i));
//                                    jsonArrayBundles.put(bundleInfoForPrint.get(bundleInfoForPrint.size() - 1).getJSONObject());
                                        }
                                    }
                                }
                                Log.e("count selected", "" + jsonArray.length());
                                new JSONTask4().execute();
                                passwordDialog.dismiss();
                            } else {
                                Toast.makeText(AcceptanceInfoReport.this, "Not Authorized!", Toast.LENGTH_SHORT).show();
                                password.setText("");
                            }
                        }
                    });
                    passwordDialog.show();
                } else
                    Toast.makeText(AcceptanceInfoReport.this, "Please select items first!", Toast.LENGTH_SHORT).show();

            }
        });

        fromLength.addTextChangedListener(new watchTextChange(fromLength));
        toLength.addTextChangedListener(new watchTextChange(toLength));
        fromWidth.addTextChangedListener(new watchTextChange(fromWidth));
        toWidth.addTextChangedListener(new watchTextChange(toWidth));
        fromThickness.addTextChangedListener(new watchTextChange(fromThickness));
        toThickness.addTextChangedListener(new watchTextChange(toThickness));

    }

    public void filters() {

        int sumOfBundles = 0;
        double sumOfCubic = 0, sumOfPieces = 0;

//        details.clear();
//        Log.e("inventoryReport", "/details2/size/" + details2.size());
//        for (int v = 0; v < details2.size(); v++) {
//            BundleInfo fake = new BundleInfo();
//            fake = details2.get(v);
//            details.add(fake);
//        }
        String fromDateLocal = fromDate.getText().toString().trim();
        String toDateLocal = toDate.getText().toString();
        filtered = new ArrayList<>();
        dateFiltered = new ArrayList<>();
//        Log.e("follow1/", "size2/dateFiltered/ " + details.size());
        for (int m = 0; m < details.size(); m++) {
            if ((formatDate(details.get(m).getDate()).after(formatDate(fromDateLocal))
                    || formatDate(details.get(m).getDate()).equals(formatDate(fromDateLocal)))
                    && (formatDate(details.get(m).getDate()).before(formatDate(toDateLocal))
                    || formatDate(details.get(m).getDate()).equals(formatDate(toDateLocal))))
                dateFiltered.add(details.get(m));
        }
//        Log.e("follow/", "size2/dateFiltered/ " + dateFiltered.size());
        for (int k = 0; k < dateFiltered.size(); k++) {
            if (supplierString.equals("All") || supplierString.equals(dateFiltered.get(k).getSupplierName()))
                if (gradeString.equals("All") || gradeString.equals(dateFiltered.get(k).getGrade()))
                    if (truckString.equals("All") || truckString.equals(dateFiltered.get(k).getTruckNo()))
                        if (acceptorString.equals("All") || acceptorString.equals(dateFiltered.get(k).getAcceptedPersonName()))
                            if (ttnString.equals("All") || ttnString.equals(dateFiltered.get(k).getTtnNo()))
                                if (accLocationString.equals("All") || accLocationString.equals(dateFiltered.get(k).getLocationOfAcceptance()))
                                    if (fromLengthNo.equals("") || ((dateFiltered.get(k).getLength() > Double.parseDouble(fromLengthNo))
                                            || dateFiltered.get(k).getLength() == Double.parseDouble(fromLengthNo)))

                                        if (toLengthNo.equals("") || ((dateFiltered.get(k).getLength() < Double.parseDouble(toLengthNo))
                                                || dateFiltered.get(k).getLength() == Double.parseDouble(toLengthNo)))

                                            if (fromWidthNo.equals("") || ((dateFiltered.get(k).getWidth() > Double.parseDouble(fromWidthNo))
                                                    || dateFiltered.get(k).getWidth() == Double.parseDouble(fromWidthNo)))

                                                if (toWidthNo.equals("") || ((dateFiltered.get(k).getWidth() < Double.parseDouble(toWidthNo))
                                                        || dateFiltered.get(k).getWidth() == Double.parseDouble(toWidthNo)))

                                                    if (fromThicknessNo.equals("") || ((dateFiltered.get(k).getThickness() > Double.parseDouble(fromThicknessNo))
                                                            || dateFiltered.get(k).getThickness() == Double.parseDouble(fromThicknessNo)))

                                                        if (toThicknessNo.equals("") || ((dateFiltered.get(k).getThickness() < Double.parseDouble(toThicknessNo))
                                                                || dateFiltered.get(k).getThickness() == Double.parseDouble(toThicknessNo))) {
                                                            filtered.add(dateFiltered.get(k));
                                                            sumOfBundles = filtered.size();
                                                            sumOfPieces += (dateFiltered.get(k).getNoOfPieces() * dateFiltered.get(k).getNoOfBundles());
                                                            sumOfCubic += (dateFiltered.get(k).getLength()
                                                                    * dateFiltered.get(k).getWidth()
                                                                    * dateFiltered.get(k).getThickness()
                                                                    * dateFiltered.get(k).getNoOfPieces()
                                                                    * dateFiltered.get(k).getNoOfBundles());

                                                        }
        }

//        Log.e("follow", fromDateLocal + " to " + toDateLocal + " size1 " + details.size() + " size2 " + dateFiltered.size());

//        for (int k = 0; k < dateFiltered.size(); k++) {
//            if (supplierString.equals(dateFiltered.get(k).getSupplierName()))
//                if (gradeString.equals(dateFiltered.get(k).getGrade())) {
//
//                    filtered.add(dateFiltered.get(k));
//                    sumOfBundles = filtered.size();
//                    sumOfPieces += dateFiltered.get(k).getNoOfPieces();
//                    sumOfCubic += (dateFiltered.get(k).getLength() * dateFiltered.get(k).getWidth() * dateFiltered.get(k).getThickness() * dateFiltered.get(k).getNoOfPieces());
//
//                }
//        }
        Log.e("follow/", "size3/filtered/" + filtered.size());

        adapter = new AcceptanceInfoReportAdapter(this, filtered);
        listView.setAdapter(adapter);

//        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
//            @Override
//            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//                serialNumber = filtered.get(position).getSerialNo();
//                String bundleNumber = filtered.get(position).getBundleNo();
//                String location = filtered.get(position).getLocation();
//                Log.e("serialNumber", serialNumber);
//
//                Dialog packingListDialog = new Dialog(InventoryReport.this);
//                packingListDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                packingListDialog.setContentView(R.layout.packing_list_dialog);
//                packingListDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                EditText packingList = packingListDialog.findViewById(R.id.packingList_dialog_packing_list);
//                TextView done = packingListDialog.findViewById(R.id.packingList_dialog_done);
//                TextView bundleNo = packingListDialog.findViewById(R.id.packingList_dialog_bundle_no);
//
//                bundleNo.setText("Bundle: " + bundleNumber);
//
//
//                done.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        String newPackingList = packingList.getText().toString();
//                        if (packingList.getText().toString().equals(""))
//                            newPackingList = "null";
//
//                        woodPresenter.updatePackingList(InventoryReport.this, bundleNumber, newPackingList, location);
//                        adapter.notifyDataSetChanged();
//                        packingListDialog.dismiss();
//
//                    }
//                });
//
//                packingListDialog.show();
//                return true;
//
//            }
//        });

        bundleNo.setText("" + sumOfBundles);
        noOfPieces.setText("" + String.format("%.3f", sumOfPieces));
        cubic.setText("" + String.format("%.3f", (sumOfCubic / 1000000000)));
//        fillTable(filtered);

    }

    public void goToEditPage(NewRowInfo newRowInfo) {
        List<NewRowInfo> list = new ArrayList<>();
        for (int i = 0; i < details.size(); i++)
            if (truckNoBeforeUpdate.equals(details.get(i).getTruckNo()) &&
                    serialBeforeUpdate.equals(details.get(i).getSerial()))
                if (!(details.get(i).getThickness() == newRowInfo.getThickness()
                        && details.get(i).getLength() == newRowInfo.getLength()
                        && details.get(i).getWidth() == newRowInfo.getWidth()
                        && details.get(i).getNoOfPieces() == newRowInfo.getNoOfPieces()
                        && details.get(i).getGrade() == newRowInfo.getGrade()
                        && details.get(i).getNoOfRejected() == newRowInfo.getNoOfRejected()
                        && details.get(i).getNoOfBundles() == newRowInfo.getNoOfBundles()
                        && details.get(i).getSupplierName() == newRowInfo.getSupplierName())
                ) // to add other sons
                    list.add(details.get(i));

        Intent intent = new Intent(AcceptanceInfoReport.this, AddNewRaw.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable(EDIT_RAW, newRowInfo);
        intent.putExtras(bundle);
        intent.putExtra(EDIT_FLAG, 10);
        intent.putExtra(EDIT_LIST, (Serializable) list);
        Log.e("look22", "" + newRowInfo.getSerial());
        startActivity(intent);

    }

    public void changeCheckBoxState() {
        allCheckBox.setChecked(false);
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
        gradeList.clear();
        gradeList.add("All");
        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeList.add("KD Blue Stain");
        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(this);

        suppliersAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, suppliersList);// android.R.layout.simple_spinner_item
        suppliersAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        suppliersSpinner.setAdapter(suppliersAdapter);
        suppliersSpinner.setOnItemSelectedListener(this);

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

        accLocationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, accLocationList);
        accLocationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        accLocationSpinner.setAdapter(accLocationAdapter);
        accLocationSpinner.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.acceptanceInfo_report_grade:
                gradeString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfo_report_supplier:
                supplierString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfo_report_truckNo:
                truckString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfo_report_acceptor:
                acceptorString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfo_report_ttn:
                ttnString = parent.getItemAtPosition(position).toString();
                filters();
                break;
            case R.id.acceptanceInfo_report_location:
                accLocationString = parent.getItemAtPosition(position).toString();
                filters();
                break;
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    class watchTextChange implements TextWatcher {

        private View view;

        public watchTextChange(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.acceptanceInfo_report_fromLength:
                    fromLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.acceptanceInfo_report_toLength:
                    toLengthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.acceptanceInfo_report_fromWidth:
                    fromWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.acceptanceInfo_report_toWidth:
                    toWidthNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.acceptanceInfo_report_fromThick:
                    fromThicknessNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
                case R.id.acceptanceInfo_report_toThick:
                    toThicknessNo = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private void showSnackBar(String message) {
        snackbar = Snackbar.make(containerLayout, Html.fromHtml("<font color=\"#3167F0\">" + message + "</font>"), Snackbar.LENGTH_SHORT);
        View snackbarLayout = snackbar.getView();
        TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
        textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
//                    textView.setCompoundDrawablePadding(10);//getResources().getDimensionPixelOffset(R.dimen.snackbar_icon_padding
        snackbar.show();
    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                if (flag == 0)
                    fromDate.setText(sdf.format(calendar.getTime()));
                else
                    toDate.setText(sdf.format(calendar.getTime()));

                if (!fromDate.getText().toString().equals("") && !toDate.getText().toString().equals("")) {
                    filters();
                }
            }
        };
        return date;
    }

    void removeDuplicate(List<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
    }

    // **************************** GET DATA  ****************************
    private class JSONTask extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            details.clear();
            adapter = new AcceptanceInfoReportAdapter(AcceptanceInfoReport.this, details);
            listView.setAdapter(adapter);
        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.214/woody/import.php?FLAG=5//   http://5.189.130.98:8085/import.php?FLAG=5
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
                    accLocationList.clear();

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

                        master.add(newRowInfo);
                        ttnList.add(finalObject.getString("TTN_NO"));
                        acceptorList.add(finalObject.getString("NAME_OF_ACCEPTER"));
                        truckList.add(finalObject.getString("TRUCK_NO"));
                        accLocationList.add(finalObject.getString("LOCATION_OF_ACCEPTANCE"));

                    }

                    removeDuplicate(ttnList);
                    removeDuplicate(acceptorList);
                    removeDuplicate(truckList);
                    removeDuplicate(accLocationList);

                    ttnList.add(0, "All");
                    acceptorList.add(0, "All");
                    truckList.add(0, "All");
                    accLocationList.add(0, "All");
                } catch (JSONException e) {
                    Log.e("Import Data1", e.getMessage());
                }

                try {
                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_MIX");
                    details.clear();
                    String truckNoLocal = "";
                    noOfPiecesSum = 0;
                    cubicSum = 0;
                    Log.e("mix length", "" + parentArray.length());
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        NewRowInfo newRowInfo = new NewRowInfo();
                        truckNoLocal = finalObject.getString("TRUCK_NO");
                        newRowInfo.setSupplierName(finalObject.getString("SUPLIER"));
                        newRowInfo.setTruckNo(truckNoLocal);
                        newRowInfo.setThickness(finalObject.getDouble("THICKNESS"));
                        newRowInfo.setWidth(finalObject.getDouble("WIDTH"));
                        newRowInfo.setLength(finalObject.getDouble("LENGTH"));
                        newRowInfo.setNoOfRejected(Double.parseDouble(finalObject.getString("REJ")));
                        newRowInfo.setNoOfBundles(finalObject.getDouble("NO_BUNDLES"));
                        newRowInfo.setGrade(finalObject.getString("GRADE"));
                        newRowInfo.setNoOfPieces(finalObject.getDouble("PIECES"));
                        newRowInfo.setDate(finalObject.getString("DATE_OF_ACCEPTANCE"));
                        newRowInfo.setAcceptedPersonName(finalObject.getString("NAME_OF_ACCEPTER"));
                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));
                        newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
                        newRowInfo.setSerial(finalObject.getString("SERIAL"));

                        noOfPiecesSum += (newRowInfo.getNoOfPieces() * newRowInfo.getNoOfBundles());
                        cubicSum += (newRowInfo.getLength() * newRowInfo.getWidth() * newRowInfo.getThickness() * newRowInfo.getNoOfPieces() * newRowInfo.getNoOfBundles());

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
            progressDialog.dismiss();
            if (result != null) {
                Log.e("infoReport", "/GET/" + details.size());
                filters();
//                adapter = new AcceptanceInfoReportAdapter(AcceptanceInfoReport.this, details);
//                listView.setAdapter(adapter);

                bundleNo.setText("" + details.size());
                if (details.size() == 0){
                    cubic.setText("0");
                    noOfPieces.setText("0");
                }else {
                    cubic.setText("" + String.format("%.3f", (cubicSum / 1000000000)));
                    noOfPieces.setText("" + String.format("%.3f", (noOfPiecesSum)));
                }

            } else {
                Toast.makeText(AcceptanceInfoReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // **************************** GET SUPPLIERS  ****************************
    private class JSONTask1 extends AsyncTask<String, String, List<String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<String> doInBackground(String... params) {
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
                suppliersList.add(0, "All");

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("SUPPLIERS");
                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

//                        SupplierInfo supplier = new SupplierInfo();
//                        supplier.setSupplierNo(innerObject.getString("SUPPLIER_NO"));
//                        supplier.setSupplierName(innerObject.getString("SUPPLIER_NAME"));
                        suppliersList.add(innerObject.getString("SUPPLIER_NAME"));

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
            return suppliersList;
        }


        @Override
        protected void onPostExecute(final List<String> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("infoReport", "/GET SUPPLIERS/" + result.size());
                fillSpinnerAdapter();
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AcceptanceInfoReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

//    class JSONTask3 extends AsyncTask<String, String, String> {// EDIT
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//            try {
//                String JsonResponse = null;
//                HttpClient client = new DefaultHttpClient();
//                HttpPost request = new HttpPost();
//                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214
//
//                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                nameValuePairs.add(new BasicNameValuePair("TRUCK_NO", "1"));
////                nameValuePairs.add(new BasicNameValuePair("OBJECT_INFO_MASTER", bundleNumber));
//
//                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));
//
//                HttpResponse response = client.execute(request);
//
//                BufferedReader in = new BufferedReader(new
//                        InputStreamReader(response.getEntity().getContent()));
//
//                StringBuffer sb = new StringBuffer("");
//                String line = "";
//
//                while ((line = in.readLine()) != null) {
//                    sb.append(line);
//                }
//
//                in.close();
//
//                JsonResponse = sb.toString();
//                Log.e("tag", "" + JsonResponse);
//
//                return JsonResponse;
//
//            } catch (Exception e) {
//                e.printStackTrace();
//                return null;
//            }
//        }
//
//        @Override
//        protected void onPostExecute(String s) {
//            super.onPostExecute(s);
//            Log.e("BundleReport", "json 2 " + s);
//            if (s != null) {
//                if (s.contains("PRINT BUNDLE SUCCESS")) {
////                    bundlesTable.removeView(hidedTableRow);
//                    Log.e("BundleReport", "****Success");
//                } else {
//                    Toast.makeText(AcceptanceInfoReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
////                    Log.e("inventoryReport", "****Failed to export data");
//                }
//            } else {
//                Toast.makeText(AcceptanceInfoReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
////                Log.e("inventoryReport", "****Failed to export data Please check internet connection");
//            }
//        }
//    }

    // **************************** DELETE  ****************************
    private class JSONTask4 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            Log.e("size", "" + jsonArray.length());
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("DELETE_ALL_RAWS", "1"));
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_TO_DELETE", jsonArray.toString().trim()));

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
                Log.e("tag delete", "" + e.getMessage().toString());
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("infoReport", "/DELETE/" + s);
            if (s != null) {
                if (s.contains("DELETE ALL RAWS SUCCESS")) {
                    showSnackBar("Deleted Successfully");
                    if (allCheckBox.isChecked()) {
                        allCheckBox.setChecked(false);
                    } else {
                        for (int k = 0; k < selected.size(); k++)
                            selected.get(k).setChecked(false);
                    }
                    selected.clear();

                    progressDialog.show();
                    new JSONTask().execute();
                } else {
                    Toast.makeText(AcceptanceInfoReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(AcceptanceInfoReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
//                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }


}
