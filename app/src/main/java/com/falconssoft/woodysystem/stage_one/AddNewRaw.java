package com.falconssoft.woodysystem.stage_one;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.LoadingOrder2;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AddNewRaw extends AppCompatActivity implements View.OnClickListener {

    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";
    private Settings generalSettings;
    private WoodPresenter presenter;
    private TextView addNewSupplier, searchSupplier, addButton, acceptRowButton, mainInfoButton, acceptanceDate;
    private EditText thickness, width, length, noOfPieces, noOfBundles, noOfRejected, truckNo, acceptor, acceptanceLocation, ttnNo, totalRejected, totalBundles;
    private Spinner gradeSpinner;
    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    //    public List<SupplierInfo> supplierInfoList = new ArrayList<>();
    private List<NewRowInfo> newRowList = new ArrayList<>();
    private String gradeText = "Fresh";
    public static String supplierName = "";
    private LinearLayout headerLayout, acceptRowLayout;
    private Button doneAcceptRow;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private Dialog searchDialog;

    private DatabaseHandler databaseHandler;
    private List<SupplierInfo> suppliers;
    private JSONObject masterData;
    private JSONArray jsonArray;
    private RelativeLayout coordinatorLayout;
    private Snackbar snackbar;
    private SuppliersAdapter adapter;
    private RecyclerView recyclerView;
    private Calendar myCalendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_raw);

        databaseHandler = new DatabaseHandler(AddNewRaw.this);
        suppliers = new ArrayList<>();
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        presenter = new WoodPresenter(this);

        coordinatorLayout = findViewById(R.id.addNewRow_coordinator);
        addNewSupplier = findViewById(R.id.addNewRaw_add_supplier);
        searchSupplier = findViewById(R.id.addNewRaw_search_supplier);
        thickness = findViewById(R.id.addNewRaw_thickness);
        width = findViewById(R.id.addNewRaw_width);
        length = findViewById(R.id.addNewRaw_length);
        noOfPieces = findViewById(R.id.addNewRaw_no_of_pieces);
        noOfBundles = findViewById(R.id.addNewRaw_no_of_bundles);
        noOfRejected = findViewById(R.id.addNewRaw_no_of_rejected);
        gradeSpinner = findViewById(R.id.addNewRaw_grade);
        addButton = findViewById(R.id.addNewRaw_add_button);
        acceptRowButton = findViewById(R.id.addNewRaw_acceptRaw_button);
        headerLayout = findViewById(R.id.addNewRaw_linearLayoutHeader);
        acceptRowLayout = findViewById(R.id.addNewRaw_acceptRow_linear);
        mainInfoButton = findViewById(R.id.addNewRaw_acceptRow_back);
        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        truckNo = findViewById(R.id.addNewRaw_truckNo);
        acceptanceDate = findViewById(R.id.addNewRaw_acceptance_date);
        acceptor = findViewById(R.id.addNewRaw_acceptor);
        acceptanceLocation = findViewById(R.id.addNewRaw_acceptance_location);
        ttnNo = findViewById(R.id.addNewRaw_ttn_no);
        totalRejected = findViewById(R.id.addNewRaw_total_rejected);
        totalBundles = findViewById(R.id.addNewRaw_total_bundles);
        doneAcceptRow = findViewById(R.id.addNewRaw_acceptRow_done);

        thickness.requestFocus();
        headerLayout.setVisibility(View.VISIBLE);
        acceptRowLayout.setVisibility(View.GONE);

        gradeList.clear();
        gradeList.add("Fresh");
        gradeList.add("KD");
        gradeList.add("Blue Stain");
        gradeList.add("KD Blue Stain");
        gradeList.add("AST");
        gradeList.add("AST Blue Stain");
        gradeList.add("Second Sort");
        gradeList.add("Reject");
        gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (parent.getId()) {
//                    case R.id.addNewRaw_grade:
                gradeText = parent.getItemAtPosition(position).toString();
//                        break;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mainInfoButton.setOnClickListener(this);
        doneAcceptRow.setOnClickListener(this);
        acceptRowButton.setOnClickListener(this);
        addNewSupplier.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);
        addButton.setOnClickListener(this);
        acceptanceDate.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewRaw_acceptRow_back:
                acceptRowLayout.setVisibility(View.GONE);
                acceptRowButton.setBackgroundResource(R.drawable.frame_shape_2);
                mainInfoButton.setBackgroundResource(R.drawable.frame_shape_3);

                Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                headerLayout.setVisibility(View.VISIBLE);
                headerLayout.startAnimation(animation1);
                thickness.requestFocus();
                break;
            case R.id.addNewRaw_acceptRow_done:
                doneButtonMethod();
                break;
            case R.id.addNewRaw_acceptance_date:
                new DatePickerDialog(AddNewRaw.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.addNewRaw_acceptRaw_button:
                headerLayout.setVisibility(View.GONE);
                acceptRowButton.setBackgroundResource(R.drawable.frame_shape_3);
                mainInfoButton.setBackgroundResource(R.drawable.frame_shape_2);

                Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
                acceptRowLayout.setVisibility(View.VISIBLE);
                acceptRowLayout.startAnimation(animation);
                truckNo.requestFocus();
                break;
            case R.id.addNewRaw_add_button:
                addButtonMethod();

                break;
            case R.id.addNewRaw_add_supplier:
                Intent intent = new Intent(AddNewRaw.this, AddNewSupplier.class);
                startActivity(intent);
                break;
            case R.id.addNewRaw_search_supplier:
                suppliers.clear();
                new JSONTask().execute();

                searchDialog = new Dialog(this);
                searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                searchDialog.setContentView(R.layout.search_supplier_dialog);

                SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
                 recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                 adapter = new SuppliersAdapter(this, suppliers);
                recyclerView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
//                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });
                searchDialog.show();
                break;
        }

    }

    void addButtonMethod() {
        String thicknessLocal = thickness.getText().toString();
        String widthLocal = width.getText().toString();
        String lengthLocal = length.getText().toString();
        String noOfPiecesLocal = noOfPieces.getText().toString();
        String noOfRejectedLocal = noOfRejected.getText().toString();
        String noOfBundlesLocal = noOfBundles.getText().toString();

        if (!TextUtils.isEmpty(supplierName)) {
            if (!TextUtils.isEmpty(thicknessLocal))
                if (!TextUtils.isEmpty(widthLocal))
                    if (!TextUtils.isEmpty(lengthLocal))
                        if (!TextUtils.isEmpty(noOfPiecesLocal))
                            if (!TextUtils.isEmpty(noOfRejectedLocal))
                                if (!TextUtils.isEmpty(noOfBundlesLocal)) {
                                    NewRowInfo rowInfo = new NewRowInfo();
                                    rowInfo.setSupplierName(supplierName);
                                    rowInfo.setThickness(Double.parseDouble(thicknessLocal));
                                    rowInfo.setWidth(Double.parseDouble(widthLocal));
                                    rowInfo.setLength(Double.parseDouble(lengthLocal));
                                    rowInfo.setNoOfPieces(Double.parseDouble(noOfPiecesLocal));
                                    rowInfo.setNoOfRejected(Double.parseDouble(noOfRejectedLocal));
                                    rowInfo.setNoOfBundles(Double.parseDouble(noOfBundlesLocal));
                                    rowInfo.setGrade(gradeText);

                                    newRowList.add(rowInfo);
                                    if (headerTableLayout.getChildCount() == 0)
                                        addTableHeader(headerTableLayout);
                                    tableRow = new TableRow(this);
                                    fillTableRow(tableRow, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal);
                                    tableLayout.addView(tableRow);

                                    thickness.setText("");
                                    width.setText("");
                                    length.setText("");
                                    noOfPieces.setText("");
                                    noOfRejected.setText("");
                                    noOfBundles.setText("");
                                    gradeSpinner.setSelection(gradeList.indexOf("Fresh"));
                                    gradeText = "Fresh";
                                    thickness.requestFocus();

                                } else {
                                    noOfBundles.setError("Required!");
                                }
                            else {
                                noOfRejected.setError("Required!");
                            }
                        else {
                            noOfPieces.setError("Required!");
                        }
                    else {
                        length.setError("Required!");
                    }
                else {
                    width.setError("Required!");
                }
            else {
                thickness.setError("Required!");
            }
        } else {
            searchSupplier.setError("Please Select First!");
        }
    }

    void doneButtonMethod() {
        String truckNoLocal = truckNo.getText().toString();
        String acceptorLocal = acceptor.getText().toString();
        String ttnNoLocal = ttnNo.getText().toString();
        String totalBundelsLocal = totalBundles.getText().toString();
        String acceptanceDateLocal = acceptanceDate.getText().toString();
        String locationLocal = acceptanceLocation.getText().toString();
        String totalRejectedLocal = totalRejected.getText().toString();

        Log.e("newRowList", " size" + newRowList.size());
        if (newRowList.size() > 0) {
            if (!TextUtils.isEmpty(truckNoLocal)) {
                if (!TextUtils.isEmpty(acceptorLocal))
                    if (!TextUtils.isEmpty(ttnNoLocal))
                        if (!TextUtils.isEmpty(totalBundelsLocal))
                            if (!TextUtils.isEmpty(acceptanceDateLocal))
                                if (!TextUtils.isEmpty(locationLocal))
                                    if (!TextUtils.isEmpty(totalRejectedLocal)) {

                                        jsonArray = new JSONArray();

                                        for (int i = 0; i < newRowList.size(); i++) {
                                            newRowList.get(i).setTruckNo(truckNoLocal);
                                            newRowList.get(i).setAcceptedPersonName(acceptorLocal);
                                            newRowList.get(i).setTtnNo(ttnNoLocal);
                                            newRowList.get(i).setNetBundles(totalBundelsLocal);
                                            newRowList.get(i).setDate(acceptanceDateLocal);
                                            newRowList.get(i).setLocationOfAcceptance(locationLocal);
                                            newRowList.get(i).setTotalRejectedNo(totalRejectedLocal);

                                            JSONObject object = newRowList.get(i).getJsonData();
                                            jsonArray.put(object);

                                        }

                                        masterData = new JSONObject();
                                        Log.e("data", "" + newRowList.get(0).getTruckNo());

                                        masterData = newRowList.get(0).getJsonDataMaster();
                                        new JSONTask1().execute();

                                    } else {
                                        totalRejected.setError("Required!");
                                    }
                                else {
                                    acceptanceLocation.setError("Required!");
                                }
                            else {
                                acceptanceDate.setError("Required!");
                            }
                        else {
                            totalBundles.setError("Required!");
                        }
                    else {
                        ttnNo.setError("Required!");
                    }
                else {
                    acceptor.setError("Required!");
                }
            } else {
                truckNo.setError("Required");
            }
        } else {
            Toast.makeText(this, "Please add rows firs!", Toast.LENGTH_SHORT).show();
        }
    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        supplierName = supplierNameLocal;
        searchSupplier.setText(supplierName);
        searchDialog.dismiss();

    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 8; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//                    param.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(param);
                    textView.setText("Supplier");
                    break;
                case 1:
                    textView.setText("Thic");
                    break;
                case 2:
                    textView.setText("Width");
                    break;
                case 3:
                    textView.setText("Length");
                    break;
                case 4:
                    textView.setText("Pieces");
                    break;
                case 5:
                    textView.setText("Rejected");
                    break;
                case 6:
                    textView.setText("Bundles");
                    break;
                case 7:
                    textView.setText("Grade");
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void fillTableRow(TableRow tableRow, String thicknessText, String widthText, String lengthText, String noOfPiecesText
            , String noOfRejectedText, String noBundleText) {
        for (int i = 0; i < 8; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.light_orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
            textViewParam.setMargins(1, 5, 1, 1);
            textView.setPadding(0, 10, 0, 10);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
//                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//                    param.setMargins(1, 5, 1, 1);
//                    textView.setPadding(0, 10, 0, 10);
//                    textView.setLayoutParams(param);
                    textView.setText(supplierName);
                    break;
                case 1:
                    textView.setText(thicknessText);
                    break;
                case 2:
                    textView.setText(widthText);
                    break;
                case 3:
                    textView.setText(lengthText);
                    break;
                case 4:
                    textView.setText(noOfPiecesText);
                    break;
                case 5:
                    textView.setText(noOfRejectedText);
                    break;
                case 6:
                    textView.setText(noBundleText);
                    break;
                case 7:
                    textView.setText(gradeText);
                    break;
            }
            tableRow.addView(textView);
        }

    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                acceptanceDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        return date;
    }

    void chooseSpinnersContent() {
        String gradeString;
        switch (gradeText) {
            case "Fresh":
                gradeString = "FR";
                break;
            case "Blue Stain":
                gradeString = "BS";
                break;
            case "Reject":
                gradeString = "RJ";
                break;
            case "KD":
                gradeString = "KD";
                break;
            case "KD Blue Stain":
                gradeString = "KDBS";
                break;
            case "Second Sort":
                gradeString = "SS";
                break;
            case "AST":
                gradeString = "AST";
                break;
            case "AST Blue Stain":
                gradeString = "ASTBS";
                break;
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        linearLayoutView.getVisibility();
        outState.putBoolean(STATE_VISIBILITY, mState);

        List<TableRow> tableRows = new ArrayList<>();
        int rowcount = tableLayout.getChildCount();
        for (int i = 0; i < rowcount; i++) {
            TableRow row = (TableRow) tableLayout.getChildAt(i);
            tableRows.add(row);
        }
        outState.putSerializable("table", (Serializable) tableRows);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restore state members from saved instance
        mState = savedInstanceState.getBoolean(STATE_VISIBILITY);
        thickness.requestFocus();
//        linearLayoutView.setVisibility(mState ? View.VISIBLE : View.GONE);
//        presenter.getImportData();
        List<TableRow> tableRows = (List<TableRow>) savedInstanceState.getSerializable("table");
        if (tableRows.size()>0)
            addTableHeader(headerTableLayout);

        for (int i = 0; i < tableRows.size(); i++) {
            if (tableRows.get(i).getParent() != null) {
                ((ViewGroup) tableRows.get(i).getParent()).removeView(tableRows.get(i)); // <- fix
            }
            tableLayout.addView(tableRows.get(i));
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    private class JSONTask extends AsyncTask<String, String, List<SupplierInfo>> {

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
            return suppliers;
        }


        @Override
        protected void onPostExecute(final List<SupplierInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + result.size());
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AddNewRaw.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class JSONTask1 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO", "1"));// list
//                for (int i=0 ;i< newRowList.size();i++) {
//                    nameValuePairs.add(new BasicNameValuePair("ROW_INFO_DETAILS", newRowList.get(i).toString()));
//                }
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_DETAILS", jsonArray.toString().trim()));// list
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_MASTER", masterData.toString().trim())); // json object
                Log.e("addNewRow/", "" + masterData.toString().trim());

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
            Log.e("tag of row info", s);
            if (s != null) {
                if (s.contains("RAW_INFO SUCCESS")) {

                    snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">Added Successfully</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
                    View snackbarLayout = snackbar.getView();
                    TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(android.support.design.R.id.snackbar_text);
                    textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
                    snackbar.show();

                    truckNo.setText("");
                    acceptor.setText("");
                    ttnNo.setText("");
                    totalBundles.setText("");
                    acceptanceDate.setText("");
                    acceptanceLocation.setText("");
                    totalRejected.setText("");
                    supplierName = "";
                    searchSupplier.setText("");
                    tableLayout.removeAllViews();

                    acceptRowLayout.setVisibility(View.GONE);
                    headerLayout.setVisibility(View.VISIBLE);
                    Log.e("tag", "****Success");
                } else {
//                    presenter.setSerialNo("");
//                    SettingsFile.serialNumber = "";
                    Log.e("tag", "****Failed to export data");
//                    Toast.makeText(AddToInventory.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
                }
            } else {
//                presenter.setSerialNo("");
//                SettingsFile.serialNumber = "";
                Log.e("tag", "****Failed to export data Please check internet connection");
//                Toast.makeText(AddToInventory.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }
}


