package com.falconssoft.woodysystem.stage_two;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.CustomerInfo;
import com.falconssoft.woodysystem.models.PlannedPL;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class PlannedPackingList extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private DatabaseHandler databaseHandler;
    private Calendar myCalendar;
    private Settings generalSettings;
    private Dialog searchDialog, searchDialog2;
    Dialog dialog;

    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";

    public static int flag, flag2;
    private RecyclerView recyclerView;
    private EditText thickness, width, length, noOfPieces, paclingList, destination, orderNo;
    private TextView searchCustomer, searchSupplier, addButton, saveButton, checkButton, addCust, addSupp, noBundles, totalCBM;
    private TableLayout headerTableLayout;
    TableRow tableHeader;
    private TableRow tableRow;
    private CustomerAdapter adapter;
    private SupplierAdapter adapter3;
    private PlannedListAdapter adapter2;
    public static String customerName = "null", customerNo = "0", customerName2 = "null", customerNo2 = "0", selectedGrade = "";
    public static String supplierName = "", supplierNo = "", supplierName2 = "", supplierNo2 = "";
    private static List<CustomerInfo> customers;
    private List<CustomerInfo> arraylist;
    private static List<SupplierInfo> suppliers;
    private List<SupplierInfo> arraylist2;
    private JSONObject plannedPLJObject;
    TextView customerD, supplierD;
    private RecyclerView recycler;

    TableLayout tableLayout2;
    private String custLocal, packLiastLocal, destinationLocal, orderNoLocal, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal;
    private String custL, packLiastL, destinationL, orderNoL, thicknessL, widthL, lengthL, noOfPiecesL;
    TextView piecesD, lengthD, widthD, thickD;
    SimpleDateFormat sdf;
    String today;
    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    private Spinner gradeSpinner;
    private static String gradeText = "Fresh";
    //TableRow tableRowToBeEdit;
    int flagIsChanged = 0;
    public static int noOfExist, noOfBundles;
    boolean check = false, isCheckForCopiesEdit = false, isCheckForAnyEdit = false, isUsedClosedResults = false;
    int ind;

    public static List<PlannedPL> PlannedPLList = new ArrayList<>();
    public static List<PlannedPL> oldList = new ArrayList<>();
    private List<BundleInfo> bundleInfosList = new ArrayList<>();
    private List<BundleInfo> bundleInfosList2 = new ArrayList<>();
    private JSONArray plannedPLListJSON = new JSONArray();
    private JSONArray plannedPLListJSONDELETE = new JSONArray();
    private List<PlannedPL> currentPackingListBundles = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_packing_list);

        PlannedPLList = new ArrayList<>();
        init();

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);
        databaseHandler = new DatabaseHandler(PlannedPackingList.this);
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        customers = new ArrayList<>();
        arraylist = new ArrayList<>();
        arraylist2 = new ArrayList<>();
        suppliers = new ArrayList<>();

        myCalendar = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        today = sdf.format(myCalendar.getTime());

        paclingList.requestFocus();

        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeList.add("KD Blue Stain");
        gradeList.add("KD Reject");
        gradeList.add("Second Sort");
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
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        addCust.setOnClickListener(this);
        addSupp.setOnClickListener(this);
        searchCustomer.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);

        new JSONTask6().execute();

        paclingList.addTextChangedListener(new watchTextChange(paclingList));
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_button:
                check = false;
                addButtonMethod();
                break;
            case R.id.check_button:
//                isCheckForCopiesEdit = false;
                if (isCheckForCopiesEdit) {
                    saveButtonMethod();
                } else
                    checkBundlesExistence();
                break;
            case R.id.save_button:
                if (PlannedPLList.size() > 0)
                    if (check)
                        saveButtonMethod();
                    else
                        Toast.makeText(this, "Please check if exist first!", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "No rows added!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.add_cust:
                Intent intent = new Intent(PlannedPackingList.this, AddNewCustomer.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.cust:
                flag = 1;
                customers.clear();
                new JSONTask().execute();

                customersDialog();
                break;
            case R.id.add_supplier:
                Intent intent2 = new Intent(PlannedPackingList.this, AddNewSupplier.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;
            case R.id.supplier:
                flag2 = 1;
                suppliers.clear();
                new JSONTask5().execute();

                suppliersDialog();
                break;
        }
    }

    void checkBundlesExistence() {
        // if (!isUsedClosedResults) {
        plannedPLListJSON = new JSONArray();
        for (int i = 0; i < PlannedPLList.size(); i++) {
            for (int k = 0; k < PlannedPLList.get(i).getNoOfCopies(); k++) {
                plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
//                Log.e("monitor2", "getNoOfCopies:"
//                        + PlannedPLList.get(i).getNoOfCopies() + ":getNoOfExixt:" + PlannedPLList.get(i).getNoOfExixt());

            }
        }

        if (plannedPLListJSON.length() > 0) {
            new JSONTask2().execute();
            bundleInfosList.clear();
        }

//        } else {
//            showSaveFirstDialog();
//        }
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
            currentPackingListBundles.clear();
            if (!isCheckForAnyEdit)
                if (!isUsedClosedResults)
                    if (!isCheckForCopiesEdit)
                        getOldPList(paclingList.getText().toString());
                    else
                        showConfirmEditDialog();
                else
                    showConfirmEditDialog();
            else
                showConfirmEditDialog();

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    void showConfirmEditDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.edit_confirm_message);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveButtonMethod();
            }
        });

        builder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                new JSONTask6().execute();
            }
        });
        builder.setCancelable(false);
        builder.show();

    }

    void getOldPList(String packingL) {
        PlannedPLList.clear();
        for (int i = 0; i < oldList.size(); i++) {
            if (packingL.equals(oldList.get(i).getPackingList())) {
                currentPackingListBundles.add(oldList.get(i));
                oldList.get(i).setIndex(i);
                PlannedPL mediator = new PlannedPL(); // this object to fill old and planned with index
                mediator.setIndex(oldList.get(i).getIndex());
                mediator.setNoOfCopies(oldList.get(i).getNoOfCopies());
                mediator.setNoOfExixt(oldList.get(i).getNoOfExixt());
                mediator.setLoaded(oldList.get(i).getLoaded());
                mediator.setCustName(oldList.get(i).getCustName());
                mediator.setCustNo(oldList.get(i).getCustNo());
                mediator.setPackingList(oldList.get(i).getPackingList());
                mediator.setDestination(oldList.get(i).getDestination());
                mediator.setOrderNo(oldList.get(i).getOrderNo());
                mediator.setSupplier(oldList.get(i).getSupplier());
                mediator.setGrade(oldList.get(i).getGrade());
                mediator.setNoOfPieces(oldList.get(i).getNoOfPieces());
                mediator.setLength(oldList.get(i).getLength());
                mediator.setWidth(oldList.get(i).getWidth());
                mediator.setThickness(oldList.get(i).getThickness());
                mediator.setDate(oldList.get(i).getDate());
                mediator.setNoOfCopies(oldList.get(i).getNoOfCopies());
                mediator.setLoaded(oldList.get(i).getLoaded());
                mediator.setHide(oldList.get(i).getHide());
                mediator.setExist(oldList.get(i).getExist());
                mediator.setIsOld(oldList.get(i).getIsOld());
                PlannedPLList.add(mediator);

                searchCustomer.setText(oldList.get(i).getCustName());
                searchSupplier.setText(oldList.get(i).getSupplier());
                customerName = searchCustomer.getText().toString();
                customerNo = oldList.get(i).getCustNo();
                supplierName = searchSupplier.getText().toString();
                destination.setText(oldList.get(i).getDestination());
                orderNo.setText(oldList.get(i).getOrderNo());
                gradeSpinner.setSelection(gradeList.indexOf(oldList.get(i).getGrade()));
            }
        }

        if (headerTableLayout.getChildCount() == 0)
            addTableHeader(headerTableLayout);

        adapter2.notifyDataSetChanged();
        calculateTotal();


        //***** no of unplanned
        plannedPLListJSON = new JSONArray();
        for (int i = 0; i < PlannedPLList.size(); i++) {
            for (int k = 0; k < PlannedPLList.get(i).getNoOfCopies(); k++) {
                plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
            }
        }

        if (plannedPLListJSON.length() > 0)
            new JSONTask2().execute();

        checkBundlesExistence();
    }

    public void filter(String charText) { // by Name
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(customers);
        } else {
            for (CustomerInfo customerInfo : customers) {//for (SupplierInfo supplierInfo : arraylist){
                if (customerInfo.getCustName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraylist.add(customerInfo);
                }
            }
        }
        adapter = new CustomerAdapter(1, this, arraylist);
        recyclerView.setAdapter(adapter);
    }

    public void filter2(String charText) { // by Name
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist2.clear();
        if (charText.length() == 0) {
            arraylist2.addAll(suppliers);
        } else {
            for (SupplierInfo supplierInfo : suppliers) {//for (SupplierInfo supplierInfo : arraylist){
                if (supplierInfo.getSupplierName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraylist2.add(supplierInfo);
                }
            }
        }
        adapter3 = new SupplierAdapter(1, this, arraylist2);
        recyclerView.setAdapter(adapter3);
    }

    void addButtonMethod() {
        packLiastLocal = paclingList.getText().toString();
        destinationLocal = destination.getText().toString();
        orderNoLocal = orderNo.getText().toString();
        thicknessLocal = thickness.getText().toString();
        widthLocal = width.getText().toString();
        lengthLocal = length.getText().toString();
        noOfPiecesLocal = noOfPieces.getText().toString();

        if (!TextUtils.isEmpty(customerName)) {
            //if (!TextUtils.isEmpty(supplierName)) {
            if (!TextUtils.isEmpty(packLiastLocal)) {
                if (!TextUtils.isEmpty(destinationLocal)) {
                    if (!TextUtils.isEmpty(orderNoLocal)) {
                        if (!TextUtils.isEmpty(thicknessLocal)) {
                            if (!TextUtils.isEmpty(widthLocal)) {
                                if (!TextUtils.isEmpty(lengthLocal)) {
                                    if (!TextUtils.isEmpty(noOfPiecesLocal)) {
                                        if (!TextUtils.isEmpty(gradeText)) {

                                            BundleInfo bundleInfo = new BundleInfo();
                                            bundleInfo.setNoOfPieces(Double.parseDouble(noOfPiecesLocal));
                                            bundleInfo.setLength(Double.parseDouble(lengthLocal));
                                            bundleInfo.setWidth(Double.parseDouble(widthLocal));
                                            bundleInfo.setThickness(Double.parseDouble(thicknessLocal));
                                            bundleInfo.setGrade(gradeText);
                                            int a = addedBefore(bundleInfo, -1);

                                            if (a != -1)
                                                Toast.makeText(PlannedPackingList.this, "This item is added before ! Please change its copies", Toast.LENGTH_LONG).show();
                                            else {
                                                searchCustomer.setError(null);
                                                searchSupplier.setError(null);
                                                paclingList.setError(null);
                                                destination.setError(null);
                                                orderNo.setError(null);

                                                thicknessLocal = formatDecimalValue(thicknessLocal);
                                                widthLocal = formatDecimalValue(widthLocal);
                                                lengthLocal = formatDecimalValue(lengthLocal);
                                                noOfPiecesLocal = formatDecimalValue(noOfPiecesLocal);

                                                thicknessLocal = isContainValueAfterDot(thicknessLocal);
                                                widthLocal = isContainValueAfterDot(widthLocal);
                                                lengthLocal = isContainValueAfterDot(lengthLocal);
                                                noOfPiecesLocal = isContainValueAfterDot(noOfPiecesLocal);


                                                PlannedPL packingList = new PlannedPL();
                                                packingList.setDate(today);
                                                packingList.setThickness(Double.parseDouble(thicknessLocal));
                                                packingList.setWidth(Double.parseDouble(widthLocal));
                                                packingList.setLength(Double.parseDouble(lengthLocal));
                                                packingList.setNoOfPieces(Double.parseDouble(noOfPiecesLocal));
                                                packingList.setCustName(customerName);
                                                packingList.setSupplier(supplierName);
                                                packingList.setCustNo(customerNo);
                                                packingList.setPackingList(packLiastLocal);
                                                packingList.setDestination(destinationLocal);
                                                packingList.setOrderNo(orderNoLocal);
                                                packingList.setGrade(gradeText);
                                                packingList.setExist("null");
                                                packingList.setNoOfCopies(1);
                                                packingList.setLoaded(0);
                                                packingList.setIsOld(0);

                                                if (!(packingList == null)) {
                                                    PlannedPLList.add(packingList);
                                                }

                                                if (headerTableLayout.getChildCount() == 0)
                                                    addTableHeader(headerTableLayout);

                                                adapter2.notifyDataSetChanged();

                                                thickness.setText("");
                                                width.setText("");
                                                length.setText("");
                                                noOfPieces.setText("");

                                                thickness.requestFocus();

                                                calculateTotal();
                                            }

                                        }
                                    } else {
                                        noOfPieces.setError("Required!");
                                    }
                                } else {
                                    length.setError("Required!");
                                }
                            } else {
                                width.setError("Required!");
                            }
                        } else {
                            thickness.setError("Required!");
                        }
                    } else {
                        orderNo.setError("Required!");
                    }
                } else {
                    destination.setError("Required!");
                }
            } else {
                paclingList.setError("Required!");
            }
//            } else {
//                searchSupplier.setError("Please Select First!");
//            }
        } else {
            searchCustomer.setError("Please Select First!");
        }


    }

    void saveButtonMethod() {

        boolean isOk = true;
        for (int i = 0; i < PlannedPLList.size(); i++) {
            if (PlannedPLList.get(i).getExist().equals("Not Exist") || PlannedPLList.get(i).getExist().equals("null")) {
                isOk = false;
                break;
            }
        }

        if (isOk) {
            plannedPLListJSON = new JSONArray();
            for (int i = 0; i < PlannedPLList.size(); i++) {
                for (int k = 0; k < PlannedPLList.get(i).getNoOfCopies(); k++) {
                    plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
                }
            }
            Log.e("*****", "sizeofplannedbefore" + PlannedPLList.size());

            new JSONTask4().execute();
        } else
            Toast.makeText(PlannedPackingList.this, "Some bundle does not exists, please edit!", Toast.LENGTH_SHORT).show();
    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 16; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
            TableRow.LayoutParams textViewParam2 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.45f);
            TableRow.LayoutParams textViewParam3 = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.7f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    textView.setLayoutParams(textViewParam2);
                    textView.setText("#");
                    break;
                case 1:
                    textView.setText("Customer");
                    break;
                case 2:
                    textView.setText("PL #");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 3:
                    textView.setText("Dest");
                    break;
                case 4:
                    textView.setText("Order #");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 5:
                    textView.setText("Supplier");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 6:
                    textView.setText("Grade");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 7:
                    textView.setText("Thick");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 8:
                    textView.setText("Width");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 9:
                    textView.setText("Length");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 10:
                    textView.setText("Pieces");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 11:
                    textView.setText("#Bundl");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 12:
                    textView.setText("Is Exist");
                    textView.setLayoutParams(textViewParam);
                    break;
                case 13:
                    textView.setLayoutParams(textViewParam2);
                    textView.setText("E");
                    break;
                case 14:
                    textView.setLayoutParams(textViewParam2);
                    textView.setText("D");
                    break;
                case 15:
                    textView.setLayoutParams(textViewParam2);
                    textView.setText("C");
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void fillTableRow2() {
        // fillTableRow2 => fill closed result table
        // bundleInfosList2 => this list have closed results
        int index = -1;
        if (bundleInfosList2.get(0).getThickness() != Double.parseDouble(thickD.getText().toString())) {
            thickD.setTextColor(ContextCompat.getColor(this, R.color.preview));
            index = 0;
        }

        if (bundleInfosList2.get(0).getWidth() != Double.parseDouble(widthD.getText().toString())) {
            widthD.setTextColor(ContextCompat.getColor(this, R.color.preview));
            index = 1;
        }

        if (bundleInfosList2.get(0).getLength() != Double.parseDouble(lengthD.getText().toString())) {
            lengthD.setTextColor(ContextCompat.getColor(this, R.color.preview));
            index = 2;
        }

        if (bundleInfosList2.get(0).getNoOfPieces() != Double.parseDouble(piecesD.getText().toString())) {
            piecesD.setTextColor(ContextCompat.getColor(this, R.color.preview));
            index = 3;
        }


        TableRow tableRow;
        for (int k = 0; k < bundleInfosList2.size(); k++) {

            int noOfE = bundleInfosList2.get(k).getNoOfExist();

            int a = addedBefore(bundleInfosList2.get(k), -1);
            if (a != -1)
                noOfE -= a;

            tableRow = new TableRow(this);

            for (int i = 0; i < 6; i++) {
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.color.light_orange);
                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                textViewParam.setMargins(1, 1, 1, 1);
                textView.setPadding(0, 5, 0, 5);
                textView.setTextSize(15);
                textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
                textView.setLayoutParams(textViewParam);
                switch (i) {
                    case 0:
                        textView.setText("" + (int) bundleInfosList2.get(k).getThickness());
                        break;
                    case 1:
                        textView.setText("" + (int) bundleInfosList2.get(k).getWidth());
                        break;
                    case 2:
                        textView.setText("" + (int) bundleInfosList2.get(k).getLength());
                        break;
                    case 3:
                        textView.setText("" + (int) bundleInfosList2.get(k).getNoOfPieces() + "(" + noOfE + ")");
                        break;
                    case 4:
                        textView.setText(bundleInfosList2.get(k).getGrade());
                        break;
                    case 5:
                        textView.setText(bundleInfosList2.get(k).getLocation());
                        break;
                }

                if (i == index)
                    textView.setTextColor(ContextCompat.getColor(this, R.color.preview));

                tableRow.addView(textView);


                int s = k;
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (a != -1) {
                            Toast.makeText(PlannedPackingList.this, "This item is added before !", Toast.LENGTH_LONG).show();
                        } else {
                            piecesD.setText("" + (int) bundleInfosList2.get(s).getNoOfPieces());
                            lengthD.setText("" + (int) bundleInfosList2.get(s).getLength());
                            widthD.setText("" + (int) bundleInfosList2.get(s).getWidth());
                            thickD.setText("" + (int) bundleInfosList2.get(s).getThickness());
                            flagIsChanged = 1;
                            noOfExist = bundleInfosList2.get(s).getNoOfExist();
                            //noOfBundles = bundleInfosList2.get(s).get();

                        }
                    }
                });
            }


            if (noOfE != 0)
                tableLayout2.addView(tableRow);

        }

    }

    int addedBefore(BundleInfo bundleInfo, int index) {
        if (index == -1) {
            for (int i = 0; i < PlannedPLList.size(); i++) {
//                Log.e("checksamedata", "" + (PlannedPLList.get(i).getLoaded() == 0) +
//                        (!PlannedPLList.get(i).getExist().equals("Planned")) +
//                        (bundleInfo.getGrade().equals(PlannedPLList.get(i).getGrade())) +
//                        (bundleInfo.getThickness() == PlannedPLList.get(i).getThickness()) +
//                        (bundleInfo.getWidth() == PlannedPLList.get(i).getWidth()) +
//                        (bundleInfo.getLength() == PlannedPLList.get(i).getLength()) +
//                        (bundleInfo.getNoOfPieces() == PlannedPLList.get(i).getNoOfPieces()));
//                !PlannedPLList.get(i).getExist().equals("Planned") &&
                if (PlannedPLList.get(i).getLoaded() == 0 &&
                        bundleInfo.getGrade().equals(PlannedPLList.get(i).getGrade()) &&
                        bundleInfo.getThickness() == PlannedPLList.get(i).getThickness() &&
                        bundleInfo.getWidth() == PlannedPLList.get(i).getWidth() &&
                        bundleInfo.getLength() == PlannedPLList.get(i).getLength() &&
                        bundleInfo.getNoOfPieces() == PlannedPLList.get(i).getNoOfPieces()) { // add if it is loaded or not

                    //Log.e("****", "in");
                    return PlannedPLList.get(i).getNoOfCopies();
                }
            }
        } else {
            for (int i = 0; i < PlannedPLList.size(); i++) {
                Log.e("checksamedata", "" + (PlannedPLList.get(i).getLoaded() == 0) +
                        (!PlannedPLList.get(i).getExist().equals("Planned")) +
                        (bundleInfo.getGrade().equals(PlannedPLList.get(i).getGrade())) +
                        (bundleInfo.getThickness() == PlannedPLList.get(i).getThickness()) +
                        (bundleInfo.getWidth() == PlannedPLList.get(i).getWidth()) +
                        (bundleInfo.getLength() == PlannedPLList.get(i).getLength()) +
                        (bundleInfo.getNoOfPieces() == PlannedPLList.get(i).getNoOfPieces()));
                //!PlannedPLList.get(i).getExist().equals("Planned") &&
                if (PlannedPLList.get(i).getLoaded() == 0 &&
                        bundleInfo.getGrade().equals(PlannedPLList.get(i).getGrade()) &&
                        bundleInfo.getThickness() == PlannedPLList.get(i).getThickness() &&
                        bundleInfo.getWidth() == PlannedPLList.get(i).getWidth() &&
                        bundleInfo.getLength() == PlannedPLList.get(i).getLength() &&
                        bundleInfo.getNoOfPieces() == PlannedPLList.get(i).getNoOfPieces() &&
                        index != i) { // add if it is loaded or not

                    //Log.e("****", "in");
                    return PlannedPLList.get(i).getNoOfCopies();
                }
            }

        }
        return -1;
    }

    void customersDialog() {

        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_customer_dialog);
        searchDialog.setCancelable(true);

        SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog.findViewById(R.id.search_supplier_close);

        recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CustomerAdapter(1, this, customers);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                adapter.notifyDataSetChanged();
                return false;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog.dismiss();
                //isCamera = false;
            }
        });
        searchDialog.show();

    }

    void suppliersDialog() {

        searchDialog2 = new Dialog(this);
        searchDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog2.setContentView(R.layout.search_supplier_dialog);
        searchDialog2.setCancelable(true);

        SearchView searchView = searchDialog2.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog2.findViewById(R.id.search_supplier_close);

        recyclerView = searchDialog2.findViewById(R.id.search_supplier_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new SupplierAdapter(1, this, suppliers);
        recyclerView.setAdapter(adapter3);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter2(newText);
                adapter3.notifyDataSetChanged();
                return false;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog2.dismiss();
                //isCamera = false;
            }
        });
        searchDialog2.show();

    }

    public void closedResultsDialog(int index) {

        flagIsChanged = 0;
        Dialog dialog = new Dialog(PlannedPackingList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.closed_results_planned_item);

        piecesD = dialog.findViewById(R.id.no_pieces);
        lengthD = dialog.findViewById(R.id.length);
        widthD = dialog.findViewById(R.id.width);
        thickD = dialog.findViewById(R.id.thickness);
        tableLayout2 = dialog.findViewById(R.id.table);
        TextView save = dialog.findViewById(R.id.save);

        piecesD.setText("" + (int) PlannedPLList.get(index).getNoOfPieces());
        lengthD.setText("" + (int) PlannedPLList.get(index).getLength());
        widthD.setText("" + (int) PlannedPLList.get(index).getWidth());
        thickD.setText("" + (int) PlannedPLList.get(index).getThickness());

        thickD.setTextColor(ContextCompat.getColor(this, R.color.black));
        widthD.setTextColor(ContextCompat.getColor(this, R.color.black));
        lengthD.setTextColor(ContextCompat.getColor(this, R.color.black));
        piecesD.setTextColor(ContextCompat.getColor(this, R.color.black));

        plannedPLJObject = PlannedPLList.get(index).getJSONObject();
        Log.e("tagPlanned", " COMPARE_CONTENT " + plannedPLJObject.toString());
        new JSONTask3().execute();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (flagIsChanged == 1) {
                    isUsedClosedResults = true;
                    PlannedPLList.get(index).setNoOfPieces(Double.parseDouble(piecesD.getText().toString()));
                    PlannedPLList.get(index).setLength(Double.parseDouble(lengthD.getText().toString()));
                    PlannedPLList.get(index).setWidth(Double.parseDouble(widthD.getText().toString()));
                    PlannedPLList.get(index).setThickness(Double.parseDouble(thickD.getText().toString()));
                    PlannedPLList.get(index).setExist("Exist");
                    PlannedPLList.get(index).setNoOfExixt(noOfExist - 1);// minus one because one for the copies
                    PlannedPLList.get(index).setNoOfCopies(1);
                    adapter2.notifyDataSetChanged();

                    calculateTotal();
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void editItemDialog(int index) {

        flagIsChanged = 0;

        Dialog dialog = new Dialog(PlannedPackingList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_planned_item);

        customerD = dialog.findViewById(R.id.cust);
        supplierD = dialog.findViewById(R.id.supplier);
        EditText plD = dialog.findViewById(R.id.p_list);
        EditText destD = dialog.findViewById(R.id.destination);
        EditText orderNoD = dialog.findViewById(R.id.order_no);
        EditText piecesD = dialog.findViewById(R.id.no_pieces);
        EditText lengthD = dialog.findViewById(R.id.length);
        EditText widthD = dialog.findViewById(R.id.width);
        EditText thickD = dialog.findViewById(R.id.thickness);
        Button save = dialog.findViewById(R.id.save);
        Spinner gradeSpinner2 = dialog.findViewById(R.id.grade);


        ArrayList<String> gradeList2 = new ArrayList<>();
        ArrayAdapter<String> gradeAdapter;

//        final String[] gradeText = {PlannedPLList.get(index).getGrade()};
        gradeList2.clear();
        gradeList2.add("Fresh");
        gradeList2.add("BS");
        gradeList2.add("Reject");
        gradeList2.add("KD");
        gradeList2.add("KD Blue Stain");
        gradeList2.add("KD Reject");
        gradeList2.add("Second Sort");

        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList2);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner2.setAdapter(gradeAdapter);
        gradeSpinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (parent.getId()) {
//                    case R.id.addNewRaw_grade:
                selectedGrade = parent.getItemAtPosition(position).toString();
//                        break;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        gradeSpinner2.setSelection(gradeList.indexOf(PlannedPLList.get(index).getGrade()));
        selectedGrade = gradeList.get(gradeList.indexOf(PlannedPLList.get(index).getGrade()));
        customerD.setText(PlannedPLList.get(index).getCustName());
        customerName2 = PlannedPLList.get(index).getCustName();
        supplierD.setText(PlannedPLList.get(index).getSupplier());
        plD.setText(PlannedPLList.get(index).getPackingList());
        destD.setText(PlannedPLList.get(index).getDestination());
        orderNoD.setText(PlannedPLList.get(index).getOrderNo());
        piecesD.setText("" + (int) PlannedPLList.get(index).getNoOfPieces());
        lengthD.setText("" + (int) PlannedPLList.get(index).getLength());
        widthD.setText("" + (int) PlannedPLList.get(index).getWidth());
        thickD.setText("" + (int) PlannedPLList.get(index).getThickness());


        customerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                if (customers.size() == 0)
                    new JSONTask().execute();
                customersDialog();
            }
        });

        supplierD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag2 = 2;
                if (suppliers.size() == 0)
                    new JSONTask5().execute();
                suppliersDialog();
            }
        });
        //plannedPLJObject = plannedPL.getJSONObject();
        // new JSONTask3().execute();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("save", "1");
                packLiastL = plD.getText().toString();
                destinationL = destD.getText().toString();
                orderNoL = orderNoD.getText().toString();
                thicknessL = thickD.getText().toString();
                widthL = widthD.getText().toString();
                lengthL = lengthD.getText().toString();
                noOfPiecesL = piecesD.getText().toString();

                if (!TextUtils.isEmpty(customerName2)) {                // ***************** edit this
                    //if (!TextUtils.isEmpty(supplierName)) {
                    if (!TextUtils.isEmpty(packLiastL)) {
                        if (!TextUtils.isEmpty(destinationL)) {
                            if (!TextUtils.isEmpty(orderNoL)) {
                                if (!TextUtils.isEmpty(thicknessL)) {
                                    if (!TextUtils.isEmpty(widthL)) {
                                        if (!TextUtils.isEmpty(lengthL)) {
                                            if (!TextUtils.isEmpty(noOfPiecesL)) {
                                                if (!TextUtils.isEmpty(selectedGrade)) {
                                                    Log.e("save", "2");
                                                    BundleInfo bundleInfo = new BundleInfo();
                                                    bundleInfo.setNoOfPieces(Double.parseDouble(piecesD.getText().toString()));
                                                    bundleInfo.setLength(Double.parseDouble(lengthD.getText().toString()));
                                                    bundleInfo.setWidth(Double.parseDouble(widthD.getText().toString()));
                                                    bundleInfo.setThickness(Double.parseDouble(thickD.getText().toString()));
                                                    bundleInfo.setGrade(selectedGrade);
                                                    int a = addedBefore(bundleInfo, index);

                                                    if (a != -1)
                                                        Toast.makeText(PlannedPackingList.this, "This item is added before ! Please change its copies", Toast.LENGTH_LONG).show();
                                                    else {
                                                        isCheckForAnyEdit = true;
                                                        String getGradeAsString = "" + selectedGrade;
/*
                                                        Log.e("comparePlannedPLList", "getNoOfPieces:" + PlannedPLList.get(index).getNoOfPieces()
                                                                + "getLength:" + PlannedPLList.get(index).getLength()
                                                                + "getWidth:" + PlannedPLList.get(index).getWidth()
                                                                + "getThickness:" + PlannedPLList.get(index).getThickness()
                                                                + "getGrade:" + PlannedPLList.get(index).getGrade());

                                                        Log.e("comparedialog", "getNoOfPieces:" + Double.parseDouble(piecesD.getText().toString())
                                                                + "getLength:" + Double.parseDouble(lengthD.getText().toString())
                                                                + "getWidth:" + Double.parseDouble(widthD.getText().toString())
                                                                + "getThickness:" + Double.parseDouble(thickD.getText().toString())
                                                                + "getGrade:" + selectedGrade);

                                                        Log.e("compareresult", "" + (PlannedPLList.get(index).getNoOfPieces() == Double.parseDouble(piecesD.getText().toString())));
                                                        Log.e("compareresult", "" + (PlannedPLList.get(index).getLength() == Double.parseDouble(lengthD.getText().toString())));
                                                        Log.e("compareresult", "" + (PlannedPLList.get(index).getWidth() == Double.parseDouble(widthD.getText().toString())));
                                                        Log.e("compareresult", "" + (PlannedPLList.get(index).getThickness() == Double.parseDouble(thickD.getText().toString())));
                                                        Log.e("compareresult", "" + (PlannedPLList.get(index).getGrade().equals(selectedGrade)));
*/
                                                        if (PlannedPLList.get(index).getNoOfPieces() == Double.parseDouble(piecesD.getText().toString()) &&
                                                                PlannedPLList.get(index).getLength() == Double.parseDouble(lengthD.getText().toString()) &&
                                                                PlannedPLList.get(index).getWidth() == Double.parseDouble(widthD.getText().toString()) &&
                                                                PlannedPLList.get(index).getThickness() == Double.parseDouble(thickD.getText().toString()) &&
                                                                PlannedPLList.get(index).getGrade().equals(getGradeAsString)) {
                                                            // do nothing
                                                            Log.e("save", "4");
                                                        } else {
                                                            PlannedPLList.get(index).setExist("null");
                                                            Log.e("save", "5");
                                                        }

                                                        PlannedPLList.get(index).setNoOfPieces(Double.parseDouble(piecesD.getText().toString()));
                                                        PlannedPLList.get(index).setLength(Double.parseDouble(lengthD.getText().toString()));
                                                        PlannedPLList.get(index).setWidth(Double.parseDouble(widthD.getText().toString()));
                                                        PlannedPLList.get(index).setThickness(Double.parseDouble(thickD.getText().toString()));
                                                        PlannedPLList.get(index).setCustName(customerD.getText().toString());
                                                        PlannedPLList.get(index).setSupplier(supplierD.getText().toString());
                                                        PlannedPLList.get(index).setPackingList(plD.getText().toString());
                                                        PlannedPLList.get(index).setDestination(destD.getText().toString());
                                                        PlannedPLList.get(index).setOrderNo(orderNoD.getText().toString());
                                                        PlannedPLList.get(index).setGrade(selectedGrade);

                                                        adapter2.notifyDataSetChanged();

                                                        //compare();
                                                        Log.e("selectedGrade", selectedGrade);
                                                        calculateTotal();

                                                        dialog.dismiss();
                                                    }
                                                }
                                            } else {
                                                noOfPieces.setError("Required!");
                                            }
                                        } else {
                                            length.setError("Required!");
                                        }
                                    } else {
                                        width.setError("Required!");
                                    }
                                } else {
                                    thickness.setError("Required!");
                                }
                            } else {
                                orderNo.setError("Required!");
                            }
                        } else {
                            destination.setError("Required!");
                        }
                    } else {
                        paclingList.setError("Required!");
                    }
//                    } else {
//                        supplierD.setError("Please Select First!");
//                    }
                } else {
                    customerD.setError("Please Select First!");
                }
            }
        });

        dialog.show();

    }

    public void deleteItemDialog(int index) {

        if (isCheckForAnyEdit || isUsedClosedResults || isCheckForCopiesEdit) {
            showSaveFirstDialog();
        } else {
            Dialog dialog = new Dialog(PlannedPackingList.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_of_copy_planned_item);

            EditText copies = dialog.findViewById(R.id.copies);
            Button save = dialog.findViewById(R.id.save);
            save.setText("Delete");

            copies.setText("" + PlannedPLList.get(index).getNoOfCopies());

            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String copy = copies.getText().toString();

                    if (!TextUtils.isEmpty(copy) && Integer.parseInt(copy) > 0) {
                        if (Integer.parseInt(copy) <= PlannedPLList.get(index).getNoOfCopies()) {                // ***************** edit this
                            plannedPLListJSONDELETE = new JSONArray();
                            for (int i = 0; i < Integer.parseInt(copy); i++) {
                                plannedPLListJSONDELETE.put(PlannedPLList.get(index).getJSONObject());
                            }

                            ind = index;
                            if (PlannedPLList.get(index).getIsOld() == 1) {
                                progressDialog.show();
                                new JSONTask7().execute();
                            } else {

                                PlannedPLList.remove(index);
                                adapter2.notifyDataSetChanged();

                                calculateTotal();
                            }
                            dialog.dismiss();
                        } else {
                            if (PlannedPLList.get(index).getNoOfCopies() == 1)
                                copies.setError("There is " + PlannedPLList.get(index).getNoOfCopies() + " bundle!");
                            else
                                copies.setError("There are " + PlannedPLList.get(index).getNoOfCopies() + " bundles!");
                        }
                    } else {
                        copies.setError("Please enter number of bundles!");
                    }
                }
            });

            dialog.show();

//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Are you want delete  (" + PlannedPLList.get(index).getNoOfCopies() + ")  of selected bundles? ");
//            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    plannedPLListJSONDELETE = new JSONArray();
//                    for (int i = 0; i < PlannedPLList.get(index).getNoOfCopies(); i++) {
//                        plannedPLListJSONDELETE.put(PlannedPLList.get(index).getJSONObject());
//                    }
//
//                    ind = index;
//                    if (PlannedPLList.get(index).getIsOld() == 1) {
//                        progressDialog.show();
//                        new JSONTask7().execute();
//                    } else {
//
//                        PlannedPLList.remove(index);
//                        adapter2.notifyDataSetChanged();
//
//                        calculateTotal();
//                    }
//                }
//            });
//
//            builder.show();
        }

    }

    void showSaveFirstDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please save the changes first!");
        builder.setCancelable(false);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    public void editNoOfItemsDialog(int index) {

        Dialog dialog = new Dialog(PlannedPackingList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.no_of_copy_planned_item);

        EditText copies = dialog.findViewById(R.id.copies);
        Button save = dialog.findViewById(R.id.save);

        copies.setText("" + PlannedPLList.get(index).getNoOfCopies());

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String copy = copies.getText().toString();

                if (!TextUtils.isEmpty(copy) && Integer.parseInt(copy) > 0) {                // ***************** edit this
                    isCheckForCopiesEdit = true;
                    if (PlannedPLList.get(index).getExist().contains("Planned") || PlannedPLList.get(index).getExist().contains("Exist")) {
                        Log.e("****", "in");
                        if (Integer.parseInt(copy) <= (PlannedPLList.get(index).getNoOfExixt() + PlannedPLList.get(index).getNoOfCopies())) {

                            PlannedPLList.get(index).setNoOfExixt(
                                    (PlannedPLList.get(index).getNoOfExixt() + PlannedPLList.get(index).getNoOfCopies()) - Integer.parseInt(copy));
                            PlannedPLList.get(index).setNoOfCopies(Integer.parseInt(copy));
                            Log.e("monitor", "getNoOfCopies:" + PlannedPLList.get(index).getNoOfCopies() + ":getNoOfExixt:" + PlannedPLList.get(index).getNoOfExixt());
                            adapter2.notifyDataSetChanged();
                            calculateTotal();

                            dialog.dismiss();
                        } else {
                            copies.setError("Exist(" + (PlannedPLList.get(index).getNoOfExixt() + PlannedPLList.get(index).getNoOfCopies()) + ")!");
                        }


                    } else {
                        if (Integer.parseInt(copy) <= PlannedPLList.get(index).getNoOfExixt()) {

                            PlannedPLList.get(index).setNoOfCopies(Integer.parseInt(copy));
                            adapter2.notifyDataSetChanged();
                            calculateTotal();

                            dialog.dismiss();
                        } else {
                            copies.setError("Exist(" + PlannedPLList.get(index).getNoOfExixt() + ")!");
                        }
                    }
                } else {
                    copies.setError("Please Enter No Of Copies!");
                }
            }
        });

        dialog.show();

    }

    public void getSearchCustomerInfo(String customerNameLocal, String customerNoLocal) {
        if (flag == 1) {
            customerName = customerNameLocal;
            customerNo = customerNoLocal;
            searchCustomer.setText(customerName);
            searchCustomer.setError(null);
        } else {
            customerName2 = customerNameLocal;
            customerNo2 = customerNoLocal;
            customerD.setText(customerName2);
            customerD.setError(null);
        }
        searchDialog.dismiss();
    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        if (flag2 == 1) {
            supplierName = supplierNameLocal;
            supplierNo = supplierNoLocal;
            searchSupplier.setText(supplierName);
            searchSupplier.setError(null);
        } else {
            supplierName2 = supplierNameLocal;
            supplierNo = supplierNoLocal;
            supplierD.setText(supplierName2);
            supplierD.setError(null);
        }
        searchDialog2.dismiss();
    }

    String formatDecimalValue(String value) {
        String charOne = String.valueOf(value.charAt(0));
        String charEnd = String.valueOf(value.charAt(value.length() - 1));

        if (charOne.equals("."))
            return ("0" + value);
        else if (charEnd.equals(".")) {
            value = value.substring(0, value.length() - 1);
            return (value);
        }
        return value;
    }

    String isContainValueAfterDot(String string) {
        if (string.contains(".")) {
            String isConten = "";
            String afterDot = string.substring(string.indexOf(".") + 1, string.length());
            Log.e("afterDot1", "" + afterDot + "      " + string);
            if (!(Integer.parseInt(afterDot) > 0)) {
                Log.e("afterDot2", "" + string.substring(0, string.indexOf(".")));
                isConten = string.substring(0, string.indexOf("."));

            } else {
                isConten = string;
            }
            return isConten;
        } else
            return string;

    }

    // ********************************* FIND customer ***************************
    private class JSONTask extends AsyncTask<String, String, List<CustomerInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<CustomerInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + databaseHandler.getSettings().getIpAddress() + "/import.php?FLAG=7");

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

                customers.add(new CustomerInfo("0", "null"));
                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("CUSTOMERS");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        CustomerInfo customer = new CustomerInfo();
                        customer.setCustNo(innerObject.getString("CUSTOMERS_NO"));
                        customer.setCustName(innerObject.getString("CUSTOMERS_NAME"));

                        customers.add(customer);

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
            return customers;
        }


        @Override
        protected void onPostExecute(final List<CustomerInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + result.size());
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(PlannedPackingList.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }  // import customers

    // ********************************* FIND SAME DATA ***************************
    private class JSONTask2 extends AsyncTask<String, String, String> {  // check

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("CHECK_CONTENT", plannedPLListJSON.toString()));
                nameValuePairs.add(new BasicNameValuePair("LOCATION", databaseHandler.getSettings().getStore().toString()));

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

                JSONObject object = new JSONObject(JsonResponse);

                bundleInfosList.clear();
//                String ff=JsonResponse.r((JsonResponse.indexOf("result")+5));
                Log.e("tag___", "" + JsonResponse.indexOf("result"));

                int index = plannedPLListJSON.length();

                for (int i = 0; i < object.length(); i++) {
                    for (int f = 0; f < index; f++) {
                        try {

                            JSONArray array = object.getJSONArray("result" + (f + 1));

                            JSONObject innerObject = array.getJSONObject(0);

                            BundleInfo bundleInfo = new BundleInfo();
                            bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                            bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                            bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                            bundleInfo.setGrade(innerObject.getString("GRADE"));
                            bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                            //bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                            //bundleInfo.setLocation(innerObject.getString("LOCATION"));
                            //bundleInfo.setArea(innerObject.getString("AREA"));
                            //bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                            //bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                            //bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                            //bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                            bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                            bundleInfo.setNoOfExist(innerObject.getInt("COUNT"));
                            Log.e("monitor3", ":getNoOfExixt:" + innerObject.getInt("COUNT"));

                            bundleInfosList.add(bundleInfo);

                        } catch (Exception e) {

                        }
                    }
                }
//                }
                Log.e("tag2", "" + bundleInfosList.size());
                Log.e("tag3", "" + object.length());
                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            check = true;
            compare();

        }
    }

    // ********************************* get compared DATA ***************************
    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("COMPARE_CONTENT", plannedPLJObject.toString()));
                nameValuePairs.add(new BasicNameValuePair("LOCATION", databaseHandler.getSettings().getStore().toString()));

//                Log.e("tagPlanned", " COMPARE_CONTENT " +plannedPLJObject.toString());
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
                Log.e("tag", " COMPARE_CONTENT " + JsonResponse);

                JSONObject object = new JSONObject(JsonResponse);
                JSONArray array = object.getJSONArray("COMPARABLE_RESULTS");

                bundleInfosList2.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject innerObject = array.getJSONObject(i);

                    BundleInfo bundleInfo = new BundleInfo();
                    bundleInfo.setThickness(innerObject.getInt("THICKNESS"));
                    bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                    bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                    bundleInfo.setGrade(innerObject.getString("GRADE"));
                    bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                    //bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                    bundleInfo.setLocation(innerObject.getString("LOCATION"));
                    //bundleInfo.setArea(innerObject.getString("AREA"));
                    //bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                    //bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                    //bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                    //bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                    bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                    bundleInfo.setNoOfExist(innerObject.getInt("COUNT"));


                    bundleInfosList2.add(bundleInfo);

                }

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

                fillTableRow2();

            } else {
                //Toast.makeText(PlannedPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ********************************* SAVE NEW PACKING LIST ***************************
    private class JSONTask4 extends AsyncTask<String, String, String> {  // save

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));


                Log.e("******", "sizeofplanned   " + plannedPLListJSON.length());
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("SAVE_PLANNED_PACKING_LIST", plannedPLListJSON.toString()));
                nameValuePairs.add(new BasicNameValuePair("LOCATION", databaseHandler.getSettings().getStore()));

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
                if (s.contains("PLANNED_PACKING_LIST SUCCESS")) {

                    PlannedPLList.clear();
                    adapter2.notifyDataSetChanged();
                    check = false;
                    plannedPLListJSON = new JSONArray();
                    PlannedPLList.clear();
                    isCheckForAnyEdit = false;
                    isUsedClosedResults = false;
                    if (!isCheckForCopiesEdit)
                        paclingList.setText("");
                    destination.setText("");
                    orderNo.setText("");
                    thickness.setText("");
                    width.setText("");
                    length.setText("");
                    noOfPieces.setText("");
                    searchCustomer.setText("");
                    searchSupplier.setText("");
                    gradeSpinner.setSelection(gradeList.indexOf("Fresh"));

                    new JSONTask6().execute();

                    Toast.makeText(PlannedPackingList.this, "Saved!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "PLANNED_PACKING_LIST SUCCESS");
                } else {
                    Toast.makeText(PlannedPackingList.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "Failed to export data!");
                }

            } else {
                Toast.makeText(PlannedPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ********************************* get Suppliers ***************************
    private class JSONTask5 extends AsyncTask<String, String, List<SupplierInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SupplierInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + databaseHandler.getSettings().getIpAddress() + "/import.php?FLAG=10");

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

                suppliers.add(new SupplierInfo("0", "null"));
                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("SUPPLIERS");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        SupplierInfo sup = new SupplierInfo();
                        sup.setSupplierNo(innerObject.getString("SUPPLIER_NO"));
                        sup.setSupplierName(innerObject.getString("SUPPLIER_NAME"));

                        suppliers.add(sup);

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
                adapter3.notifyDataSetChanged();
            } else {
                Toast.makeText(PlannedPackingList.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }  // import Suppliers

    // ********************************* get old planned ***************************
    private class JSONTask6 extends AsyncTask<String, String, String> {  // old planned

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/import.php?FLAG=11"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);

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

                JSONObject parentObject = new JSONObject(JsonResponse);

                oldList.clear();

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("OLD_PLANNED");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        PlannedPL planned = new PlannedPL();
                        planned.setCustName(innerObject.getString("CUST_NAME"));
                        planned.setCustNo(innerObject.getString("CUST_NO"));
                        planned.setPackingList(innerObject.getString("PACKING_LIST"));
                        planned.setDestination(innerObject.getString("DESTINATION"));
                        planned.setOrderNo(innerObject.getString("ORDER_NO"));
                        planned.setSupplier(innerObject.getString("SUPPLIER"));
                        planned.setGrade(innerObject.getString("GRADE"));
                        planned.setNoOfPieces(innerObject.getDouble("PIECES"));
                        planned.setLength(innerObject.getDouble("LENGTH"));
                        planned.setWidth(innerObject.getDouble("WIDTH"));
                        planned.setThickness(innerObject.getDouble("THICKNESS"));
                        planned.setDate(innerObject.getString("DATE_"));
                        planned.setNoOfCopies(innerObject.getInt("COUNT"));
                        planned.setLoaded(innerObject.getInt("LOADED"));
                        planned.setHide(innerObject.getInt("HIDE"));
                        planned.setExist("Planned");
                        planned.setIsOld(1);

                        oldList.add(planned);

                    }

                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
                }
                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            currentPackingListBundles.clear();
            if (s != null) {

                isCheckForCopiesEdit = false;
                isCheckForAnyEdit = false;
                isUsedClosedResults = false;
                if (oldList.size() > 0) {
                    customerNo = oldList.get(0).getCustNo();
                    if (!TextUtils.isEmpty(paclingList.getText().toString()))
                        getOldPList(paclingList.getText().toString());

//                    for (int i = 0; i < PLList.size(); i++) {
//                        plannedPLListJSON.put(PLList.get(i).getJSONObject());
//                    }
                }
            } else {
                Toast.makeText(PlannedPackingList.this, "No data found!", Toast.LENGTH_SHORT).show();
//                Toast.makeText(UnloadPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ********************************* delete ***************************
    private class JSONTask7 extends AsyncTask<String, String, String> {  // delete

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("DELETE_PLANNED_PACKING_LIST", plannedPLListJSONDELETE.toString()));
                nameValuePairs.add(new BasicNameValuePair("LOCATION", databaseHandler.getSettings().getStore()));

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
            progressDialog.dismiss();
            if (s != null) {
                if (s.contains("DELETE SUCCESS")) {

//                    PlannedPLList.remove(ind);
//                    adapter2.notifyDataSetChanged();
                    new JSONTask6().execute();

                    calculateTotal();

                    Toast.makeText(PlannedPackingList.this, "Deleted!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "DELETE SUCCESS");
                } else {
                    Toast.makeText(PlannedPackingList.this, "Failed to delete!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "Failed to export data!");
                }

            } else {
                Toast.makeText(PlannedPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void compare() {

        if (bundleInfosList.size() == 0) {
            for (int i = 0; i < PlannedPLList.size(); i++) {
                if (!PlannedPLList.get(i).getExist().equals("Planned"))
                    PlannedPLList.get(i).setExist("Not Exist");
            }
        }

        for (int i = 0; i < PlannedPLList.size(); i++) {
            if (!PlannedPLList.get(i).getExist().equals("Planned")) {

               /* Log.e("comparePlannedPLList", "getNoOfPieces:" + PlannedPLList.get(i).getNoOfPieces()
                        + "getLength:" + PlannedPLList.get(i).getLength()
                        + "getWidth:" + PlannedPLList.get(i).getWidth()
                        + "getThickness:" + PlannedPLList.get(i).getThickness()
                        + "getGrade:" + PlannedPLList.get(i).getGrade());*/

                for (int k = 0; k < bundleInfosList.size(); k++) {

                   /* Log.e("comparebundleInfosList", "getNoOfPieces:" + bundleInfosList.get(k).getNoOfPieces()
                            + "getLength:" + bundleInfosList.get(k).getLength()
                            + "getWidth:" + bundleInfosList.get(k).getWidth()
                            + "getThickness:" + bundleInfosList.get(k).getThickness()
                            + "getGrade:" + bundleInfosList.get(k).getGrade());*/

                    if (PlannedPLList.get(i).getThickness() == bundleInfosList.get(k).getThickness() &&
                            PlannedPLList.get(i).getWidth() == bundleInfosList.get(k).getWidth() &&
                            PlannedPLList.get(i).getLength() == bundleInfosList.get(k).getLength() &&
                            PlannedPLList.get(i).getNoOfPieces() == bundleInfosList.get(k).getNoOfPieces() &&
                            PlannedPLList.get(i).getGrade().equals(bundleInfosList.get(k).getGrade())) {

                        PlannedPLList.get(i).setExist("Exist");
                        PlannedPLList.get(i).setNoOfExixt(bundleInfosList.get(k).getNoOfExist() - 1);

                        break;
                    } else {
                        if (checkInOldPackingList(PlannedPLList.get(i))) {
                            PlannedPLList.get(i).setExist("Planned");
                            PlannedPLList.get(i).setNoOfExixt(0);
                            Log.e("checkdifference4", PlannedPLList.get(i).getExist());
                            k = bundleInfosList.size();
                        } else {
                            PlannedPLList.get(i).setExist("Not Exist");
                            PlannedPLList.get(i).setNoOfExixt(0);
                            Log.e("checkdifference4", PlannedPLList.get(i).getExist());
                        }
                    }

//                    Log.e("****out1", "***" + PlannedPLList.get(i).getThickness() + "==" + bundleInfosList.get(k).getThickness() + "&&" +
//                            PlannedPLList.get(i).getWidth() + "==" + bundleInfosList.get(k).getWidth() + "&&" +
//                            PlannedPLList.get(i).getLength() + "==" + bundleInfosList.get(k).getLength() + "&&" +
//                            PlannedPLList.get(i).getNoOfPieces() + "==" + bundleInfosList.get(k).getNoOfPieces());

                }
            } else {

                for (int k = 0; k < bundleInfosList.size(); k++) {

                    if (PlannedPLList.get(i).getHide() != 1) {
                        if (PlannedPLList.get(i).getThickness() == bundleInfosList.get(k).getThickness() &&
                                PlannedPLList.get(i).getWidth() == bundleInfosList.get(k).getWidth() &&
                                PlannedPLList.get(i).getLength() == bundleInfosList.get(k).getLength() &&
                                PlannedPLList.get(i).getNoOfPieces() == bundleInfosList.get(k).getNoOfPieces() &&
                                PlannedPLList.get(i).getGrade().equals(bundleInfosList.get(k).getGrade())) {

                            //PlannedPLList.get(i).setExist("Exist");
                            PlannedPLList.get(i).setNoOfExixt(bundleInfosList.get(k).getNoOfExist());//bundleInfosList.get(k).getNoOfExist() -

                            break;
                        } else {
                            //PlannedPLList.get(i).setExist("Not Exist");
                            PlannedPLList.get(i).setNoOfExixt(0);
                        }
                    } else {

                        PlannedPLList.get(i).setExist("Hide");
                    }

                    Log.e("****out2", "***" + PlannedPLList.get(i).getThickness() + "==" + bundleInfosList.get(k).getThickness() + "&&" +
                            PlannedPLList.get(i).getWidth() + "==" + bundleInfosList.get(k).getWidth() + "&&" +
                            PlannedPLList.get(i).getLength() + "==" + bundleInfosList.get(k).getLength() + "&&" +
                            PlannedPLList.get(i).getNoOfPieces() + "==" + bundleInfosList.get(k).getNoOfPieces());

                }

            }
        }

        adapter2.notifyDataSetChanged();


    }

    boolean checkInOldPackingList(PlannedPL raw) {
//        for (int i = 0; i < oldList.size(); i++)
//            if (raw.getThickness() == oldList.get(i).getThickness() &&
//                    raw.getWidth() == oldList.get(i).getWidth() &&
//                    raw.getLength() == oldList.get(i).getLength() &&
//                    raw.getNoOfPieces() == oldList.get(i).getNoOfPieces() &&
//                    raw.getGrade().equals(oldList.get(i).getGrade())) {
//                return true;
//            }


//        for (int i = 0; i < oldList.size(); i++)

        Log.e("comparePlannedPLList", "  Grade:" + raw.getGrade() + oldList.get(raw.getIndex()).getGrade()
                + "  getNoOfPieces:" + (raw.getNoOfPieces() == oldList.get(raw.getIndex()).getNoOfPieces())
                + "  getLength:" + (raw.getLength() == oldList.get(raw.getIndex()).getLength())
                + "  getWidth:" + (raw.getWidth() == oldList.get(raw.getIndex()).getWidth())
                + "  getThickness:" + (raw.getThickness() == oldList.get(raw.getIndex()).getThickness())
                + "  getGrade:" + (raw.getGrade() == (oldList.get(raw.getIndex()).getGrade())));

        if (raw.getThickness() == oldList.get(raw.getIndex()).getThickness() &&
                raw.getWidth() == oldList.get(raw.getIndex()).getWidth() &&
                raw.getLength() == oldList.get(raw.getIndex()).getLength() &&
                raw.getNoOfPieces() == oldList.get(raw.getIndex()).getNoOfPieces() &&
                raw.getGrade().equals(oldList.get(raw.getIndex()).getGrade())) {
            return true;
        }

        return false;
    }

    void calculateTotal() {
        int sumOfBundles = 0;
        double totalCbm = 0;
        for (int i = 0; i < PlannedPLList.size(); i++) {
            sumOfBundles += PlannedPLList.get(i).getNoOfCopies();
            totalCbm += (PlannedPLList.get(i).getThickness() * PlannedPLList.get(i).getWidth() * PlannedPLList.get(i).getLength() * PlannedPLList.get(i).getNoOfPieces() * PlannedPLList.get(i).getNoOfCopies());
        }

        noBundles.setText("" + sumOfBundles);
        totalCBM.setText("" + String.format("%.3f", (totalCbm / 1000000000)));
    }

    void init() {

        searchCustomer = findViewById(R.id.cust);
        searchSupplier = findViewById(R.id.supplier);
        paclingList = findViewById(R.id.p_list);
        destination = findViewById(R.id.destination);
        orderNo = findViewById(R.id.order_no);
        addButton = findViewById(R.id.add_button);
        checkButton = findViewById(R.id.check_button);
        saveButton = findViewById(R.id.save_button);
        addCust = findViewById(R.id.add_cust);
        addSupp = findViewById(R.id.add_supplier);
        thickness = findViewById(R.id.thickness);
        width = findViewById(R.id.width);
        length = findViewById(R.id.length);
        noOfPieces = findViewById(R.id.pieces);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        gradeSpinner = findViewById(R.id.grade);
        noBundles = findViewById(R.id.no_bundles);
        totalCBM = findViewById(R.id.total_cbm);
        tableHeader = findViewById(R.id.table_header);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new PlannedListAdapter(this, PlannedPLList);
        recycler.setAdapter(adapter2);

        noOfPieces.requestFocus();
    }
}
