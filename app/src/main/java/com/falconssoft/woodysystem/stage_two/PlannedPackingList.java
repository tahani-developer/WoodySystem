package com.falconssoft.woodysystem.stage_two;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
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

public class PlannedPackingList extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler databaseHandler;
    private Calendar myCalendar;
    private Settings generalSettings;
    private Dialog searchDialog;
    Dialog dialog;

    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";

    public static int flag ;
    private RecyclerView recyclerView;
    private EditText thickness, width, length, noOfPieces, paclingList, destination, orderNo;
    private TextView searchCustomer, addButton, saveButton, checkButton, addCust;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private CustomerAdapter adapter;
    public static String customerName = "", customerNo = "" ,customerName2 = "", customerNo2 = "";
    private List<CustomerInfo> customers;
    private List<CustomerInfo> arraylist;
    private JSONObject plannedPLJObject;
    TextView customerD;

    TableLayout tableLayout2;
    private String custLocal, packLiastLocal, destinationLocal, orderNoLocal, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal;
    private String custL, packLiastL, destinationL, orderNoL, thicknessL, widthL, lengthL, noOfPiecesL;
    TextView piecesD, lengthD, widthD, thickD;
    SimpleDateFormat sdf;
    String today;
    TableRow tableRowToBeEdit;
    int flagIsChanged = 0;
    boolean check = false;

    private List<PlannedPL> PlannedPLList = new ArrayList<>();
    private List<BundleInfo> bundleInfosList = new ArrayList<>();
    private List<BundleInfo> bundleInfosList2 = new ArrayList<>();
    private JSONArray plannedPLListJSON = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planned_packing_list);

        init();

        databaseHandler = new DatabaseHandler(PlannedPackingList.this);
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        customers = new ArrayList<>();
        arraylist = new ArrayList<>();

        myCalendar = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        today = sdf.format(myCalendar.getTime());

        paclingList.requestFocus();

        addButton.setOnClickListener(this);
        checkButton.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        addCust.setOnClickListener(this);
        searchCustomer.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.add_button:
                check = false;
                addButtonMethod();
                break;
            case R.id.check_button:
                if (plannedPLListJSON.length() > 0)
                    new JSONTask2().execute();
                else
                    Toast.makeText(this, "Please add rows first!", Toast.LENGTH_SHORT).show();
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
        }
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

    void addButtonMethod() {
        packLiastLocal = paclingList.getText().toString();
        destinationLocal = destination.getText().toString();
        orderNoLocal = orderNo.getText().toString();
        thicknessLocal = thickness.getText().toString();
        widthLocal = width.getText().toString();
        lengthLocal = length.getText().toString();
        noOfPiecesLocal = noOfPieces.getText().toString();

        if (!TextUtils.isEmpty(customerName)) {
            if (!TextUtils.isEmpty(packLiastLocal)) {
                if (!TextUtils.isEmpty(destinationLocal)) {
                    if (!TextUtils.isEmpty(orderNoLocal)) {
                        if (!TextUtils.isEmpty(thicknessLocal)) {
                            if (!TextUtils.isEmpty(widthLocal)) {
                                if (!TextUtils.isEmpty(lengthLocal)) {
                                    if (!TextUtils.isEmpty(noOfPiecesLocal)) {


                                        searchCustomer.setError(null);
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
                                        packingList.setCustNo(customerNo);
                                        packingList.setPackingList(packLiastLocal);
                                        packingList.setDestination(destinationLocal);
                                        packingList.setOrderNo(orderNoLocal);
                                        packingList.setExist(true);

                                        if (!(packingList == null)) {
                                            PlannedPLList.add(packingList);
                                            plannedPLListJSON.put(packingList.getJSONObject());
                                        }

                                        if (headerTableLayout.getChildCount() == 0)
                                            addTableHeader(headerTableLayout);

                                        fillTableRow(packingList, PlannedPLList.size() - 1);

                                        thickness.setText("");
                                        width.setText("");
                                        length.setText("");
                                        noOfPieces.setText("");
                                        //searchCustomer.setText("");
                                        //paclingList.setText("");
                                        //destination.setText("");

                                        paclingList.requestFocus();

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
        } else {
            searchCustomer.setError("Please Select First!");
        }


    }

    void saveButtonMethod() {

        boolean isOk = true;
        for (int i = 0; i < PlannedPLList.size(); i++) {
            if (!PlannedPLList.get(i).getExist()) {
                isOk = false;
                break;
            }
        }

        if (isOk) {
            plannedPLListJSON = new JSONArray();
            for (int i = 0; i < PlannedPLList.size(); i++) {
                plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
            }
            new JSONTask4().execute();
        } else
            Toast.makeText(PlannedPackingList.this, "Some bundle does not exists, please edit!", Toast.LENGTH_SHORT).show();
    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    textView.setText("Customer");
                    break;
                case 1:
                    textView.setText("PL #");
                    break;
                case 2:
                    textView.setText("Destination");
                    break;
                case 3:
                    textView.setText("Order No");
                    break;
                case 4:
                    textView.setText("Thickness");
                    break;
                case 5:
                    textView.setText("Width");
                    break;
                case 6:
                    textView.setText("Length");
                    break;
                case 7:
                    textView.setText("Pieces");
                    break;
                case 8:
                    textView.setText("Is Exist");
                    break;
                case 9:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 0.25f);
                    textView.setLayoutParams(textViewParam);
                    textView.setText("");
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void fillTableRow(PlannedPL packingList, int index) {

        tableRow = new TableRow(this);

        for (int i = 0; i < 10; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.light_orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, 40, 1f);
            textViewParam.setMargins(1, 5, 1, 1);
            textView.setPadding(0, 7, 0, 7);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    textView.setText(packingList.getCustName());
                    break;
                case 1:
                    textView.setText(packingList.getPackingList());
                    break;
                case 2:
                    textView.setText(packingList.getDestination());
                    break;
                case 3:
                    textView.setText(packingList.getOrderNo());
                    break;
                case 4:
                    textView.setText("" + (int) packingList.getThickness());
                    break;
                case 5:
                    textView.setText("" + (int) packingList.getWidth());
                    break;
                case 6:
                    textView.setText("" + (int) packingList.getLength());
                    break;
                case 7:
                    textView.setText("" + (int) packingList.getNoOfPieces());
                    break;
                case 8:
                    textView.setText("");
                    break;
                case 9:
                    TableRow.LayoutParams textViewParam2 = new TableRow.LayoutParams(0, 30, 0.25f);
                    textViewParam2.setMargins(1, 10, 1, 1);
                    textView.setLayoutParams(textViewParam2);
                    textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_edit_24dp));
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            editItemDialog(packingList, index);
                            tableRowToBeEdit = tableRow;
                        }
                    });
                    break;
            }
            tableRow.addView(textView);

            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closedResultsDialog(packingList, index);
                    tableRowToBeEdit = tableRow;
                }
            });
        }

        tableLayout.addView(tableRow);

    }

    void fillTableRow2() {

        int index = 0;
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

            tableRow = new TableRow(this);

            for (int i = 0; i < 4; i++) {
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
                        textView.setText("" + bundleInfosList2.get(k).getThickness());
                        break;
                    case 1:
                        textView.setText("" + bundleInfosList2.get(k).getWidth());
                        break;
                    case 2:
                        textView.setText("" + bundleInfosList2.get(k).getLength());
                        break;
                    case 3:
                        textView.setText("" + bundleInfosList2.get(k).getNoOfPieces());
                        break;
                }

                if (i == index)
                    textView.setTextColor(ContextCompat.getColor(this, R.color.preview));

                tableRow.addView(textView);


                int s = k;
                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        piecesD.setText("" + bundleInfosList2.get(s).getNoOfPieces());
                        lengthD.setText("" + bundleInfosList2.get(s).getLength());
                        widthD.setText("" + bundleInfosList2.get(s).getWidth());
                        thickD.setText("" + bundleInfosList2.get(s).getThickness());
                        flagIsChanged = 1;
                    }
                });
            }


            tableLayout2.addView(tableRow);

        }

    }

    void customersDialog(){

        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_customer_dialog);
        searchDialog.setCancelable(false);

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

    public void closedResultsDialog(PlannedPL plannedPL, int index) {

        flagIsChanged = 0;
        Dialog dialog = new Dialog(PlannedPackingList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.closed_results_planned_item);

//        TextView customer = dialog.findViewById(R.id.cust);
//        TextView pl = dialog.findViewById(R.id.p_list);
//        TextView dest = dialog.findViewById(R.id.destination);
        piecesD = dialog.findViewById(R.id.no_pieces);
        lengthD = dialog.findViewById(R.id.length);
        widthD = dialog.findViewById(R.id.width);
        thickD = dialog.findViewById(R.id.thickness);
        tableLayout2 = dialog.findViewById(R.id.table);
        TextView save = dialog.findViewById(R.id.save);

        piecesD.setText("" + (int) plannedPL.getNoOfPieces());
        lengthD.setText("" + (int) plannedPL.getLength());
        widthD.setText("" + (int) plannedPL.getWidth());
        thickD.setText("" + (int) plannedPL.getThickness());

        thickD.setTextColor(ContextCompat.getColor(this, R.color.black));
        widthD.setTextColor(ContextCompat.getColor(this, R.color.black));
        lengthD.setTextColor(ContextCompat.getColor(this, R.color.black));
        piecesD.setTextColor(ContextCompat.getColor(this, R.color.black));

        plannedPLJObject = plannedPL.getJSONObject();
        new JSONTask3().execute();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView t1 = (TextView) tableRowToBeEdit.getChildAt(3);
                TextView t2 = (TextView) tableRowToBeEdit.getChildAt(4);
                TextView t3 = (TextView) tableRowToBeEdit.getChildAt(5);
                TextView t4 = (TextView) tableRowToBeEdit.getChildAt(6);
                TextView t5 = (TextView) tableRowToBeEdit.getChildAt(7);

                t1.setText(Integer.parseInt(piecesD.getText().toString()));
                t2.setText(Integer.parseInt(lengthD.getText().toString()));
                t3.setText(Integer.parseInt(widthD.getText().toString()));
                t4.setText(Integer.parseInt(thickD.getText().toString()));

                PlannedPLList.get(index).setNoOfPieces(Double.parseDouble(piecesD.getText().toString()));
                PlannedPLList.get(index).setLength(Double.parseDouble(lengthD.getText().toString()));
                PlannedPLList.get(index).setWidth(Double.parseDouble(widthD.getText().toString()));
                PlannedPLList.get(index).setThickness(Double.parseDouble(thickD.getText().toString()));


                if (flagIsChanged == 1) {
                    t5.setText("Exist");
                    t5.setTextColor(ContextCompat.getColor(PlannedPackingList.this, R.color.colorPrimary));
                    PlannedPLList.get(index).setExist(true);
                }

                plannedPLListJSON = new JSONArray();
                for (int i = 0; i < PlannedPLList.size(); i++) {
                    plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
                }

                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public void editItemDialog(PlannedPL plannedPL, int index) {

        flagIsChanged = 0;
        Dialog dialog = new Dialog(PlannedPackingList.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.edit_planned_item);

        customerD = dialog.findViewById(R.id.cust);
        EditText plD = dialog.findViewById(R.id.p_list);
        EditText destD = dialog.findViewById(R.id.destination);
        EditText orderNoD = dialog.findViewById(R.id.order_no);
        EditText piecesD = dialog.findViewById(R.id.no_pieces);
        EditText lengthD = dialog.findViewById(R.id.length);
        EditText widthD = dialog.findViewById(R.id.width);
        EditText thickD = dialog.findViewById(R.id.thickness);
        Button save = dialog.findViewById(R.id.save);

        customerD.setText(plannedPL.getCustName());
        plD.setText(plannedPL.getPackingList());
        destD.setText(plannedPL.getDestination());
        orderNoD.setText(plannedPL.getOrderNo());
        piecesD.setText("" + (int) plannedPL.getNoOfPieces());
        lengthD.setText("" + (int) plannedPL.getLength());
        widthD.setText("" + (int) plannedPL.getWidth());
        thickD.setText("" + (int) plannedPL.getThickness());


        customerD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = 2;
                customersDialog();
            }
        });
        //plannedPLJObject = plannedPL.getJSONObject();
        // new JSONTask3().execute();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                packLiastL = plD.getText().toString();
                destinationL = destD.getText().toString();
                orderNoL = orderNoD.getText().toString();
                thicknessL = thickD.getText().toString();
                widthL = widthD.getText().toString();
                lengthL= lengthD.getText().toString();
                noOfPiecesL = piecesD.getText().toString();

                if (!TextUtils.isEmpty(customerName)) {                // ***************** edit this
                    if (!TextUtils.isEmpty(packLiastL)) {
                        if (!TextUtils.isEmpty(destinationL)) {
                            if (!TextUtils.isEmpty(orderNoL)) {
                                if (!TextUtils.isEmpty(thicknessL)) {
                                    if (!TextUtils.isEmpty(widthL)) {
                                        if (!TextUtils.isEmpty(lengthL)) {
                                            if (!TextUtils.isEmpty(noOfPiecesL)) {


                                                TextView t1 = (TextView) tableRowToBeEdit.getChildAt(4);
                                                TextView t2 = (TextView) tableRowToBeEdit.getChildAt(5);
                                                TextView t3 = (TextView) tableRowToBeEdit.getChildAt(6);
                                                TextView t4 = (TextView) tableRowToBeEdit.getChildAt(7);

                                                TextView t6 = (TextView) tableRowToBeEdit.getChildAt(0);
                                                TextView t7 = (TextView) tableRowToBeEdit.getChildAt(1);
                                                TextView t8 = (TextView) tableRowToBeEdit.getChildAt(2);
                                                TextView t9 = (TextView) tableRowToBeEdit.getChildAt(3);


                                                t1.setText(thickD.getText().toString());
                                                t2.setText(widthD.getText().toString());
                                                t3.setText(lengthD.getText().toString());
                                                t4.setText(piecesD.getText().toString());
                                                t6.setText(customerD.getText().toString());
                                                t7.setText(plD.getText().toString());
                                                t8.setText(destD.getText().toString());
                                                t9.setText(orderNoD.getText().toString());

                                                PlannedPLList.get(index).setNoOfPieces(Double.parseDouble(piecesD.getText().toString()));
                                                PlannedPLList.get(index).setLength(Double.parseDouble(lengthD.getText().toString()));
                                                PlannedPLList.get(index).setWidth(Double.parseDouble(widthD.getText().toString()));
                                                PlannedPLList.get(index).setThickness(Double.parseDouble(thickD.getText().toString()));
                                                PlannedPLList.get(index).setCustName(customerD.getText().toString());
                                                PlannedPLList.get(index).setPackingList(plD.getText().toString());
                                                PlannedPLList.get(index).setDestination(destD.getText().toString());
                                                PlannedPLList.get(index).setOrderNo(orderNoD.getText().toString());

                                                plannedPLListJSON = new JSONArray();
                                                for (int i = 0; i < PlannedPLList.size(); i++) {
                                                    plannedPLListJSON.put(PlannedPLList.get(i).getJSONObject());
                                                }

                                                dialog.dismiss();

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
                } else {
                    searchCustomer.setError("Please Select First!");
                }
            }
        });

        dialog.show();

    }

    public void getSearchCustomerInfo(String customerNameLocal, String customerNoLocal) {
        if(flag == 1) {
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

    /*
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

        mState = savedInstanceState.getBoolean(STATE_VISIBILITY);
        tableLayout.setVisibility(mState ? View.VISIBLE : View.GONE);

        // Restore state members from saved instance
        paclingList.requestFocus();

        List<TableRow> tableRows = (List<TableRow>) savedInstanceState.getSerializable("table");
        for (int i = 0; i < tableRows.size(); i++) {
            if (tableRows.get(i).getParent() != null) {
                ((ViewGroup) tableRows.get(i).getParent()).removeView(tableRows.get(i)); // <- fix
            }
            tableLayout.addView(tableRows.get(i));
        }

        Log.e("tag1", "");
//        if(PlannedPLList.size() != 0){
//
//            Log.e("tag2", "");
//            addTableHeader(headerTableLayout);
//
//            for (int i = 0 ; i< PlannedPLList.size() ; i++) {
//                fillTableRow(PlannedPLList.get(i), i);
//            }
//        }
        super.onRestoreInstanceState(savedInstanceState);
    }


     */

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
                            bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                            bundleInfo.setLocation(innerObject.getString("LOCATION"));
                            bundleInfo.setArea(innerObject.getString("AREA"));
                            bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                            bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                            bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                            bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                            bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));

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
                JSONArray array = object.getJSONArray("COMPARABLE_RESULTS");

                bundleInfosList2.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject innerObject = array.getJSONObject(i);

                    BundleInfo bundleInfo = new BundleInfo();
                    bundleInfo.setThickness(innerObject.getInt("THICKNESS"));
                    bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                    bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                    //bundleInfo.setGrade(innerObject.getString("GRADE"));
                    bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                    //bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                    //bundleInfo.setLocation(innerObject.getString("LOCATION"));
                    //bundleInfo.setArea(innerObject.getString("AREA"));
                    //bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                    //bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                    //bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                    //bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                    bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));

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

//                JSONObject object = new JSONObject(JsonResponse);
//
//                for (int i = 0; i < object.length(); i++) {
//                    JSONArray array = object.getJSONArray("result" + (i + 1));
//                    JSONObject innerObject = array.getJSONObject(0);
//
//                    BundleInfo bundleInfo = new BundleInfo();
//                    bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
//                    bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
//                    bundleInfo.setLength(innerObject.getDouble("LENGTH"));
//                    bundleInfo.setGrade(innerObject.getString("GRADE"));
//                    bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
//                    bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
//                    bundleInfo.setLocation(innerObject.getString("LOCATION"));
//                    bundleInfo.setArea(innerObject.getString("AREA"));
//                    bundleInfo.setBarcode(innerObject.getString("BARCODE"));
//                    bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
//                    bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
//                    bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
//                    bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
//
//                    bundleInfosList.add(bundleInfo);
//
//                }

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
                    tableLayout.removeAllViews();
                    check = false;
                    plannedPLListJSON = new JSONArray();
                    PlannedPLList.clear();
                    paclingList.setText("");
                    destination.setText("");
                    orderNo.setText("");
                    thickness.setText("");
                    width.setText("");
                    length.setText("");
                    noOfPieces.setText("");
                    searchCustomer.setText("");

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

    void compare() {

        if (bundleInfosList.size() == 0) {
            for (int i = 0; i < PlannedPLList.size(); i++) {
                PlannedPLList.get(i).setExist(false);
            }
        }

        for (int i = 0; i < PlannedPLList.size(); i++) {
            for (int k = 0; k < bundleInfosList.size(); k++) {

                if (PlannedPLList.get(i).getThickness() == bundleInfosList.get(k).getThickness() &&
                        PlannedPLList.get(i).getWidth() == bundleInfosList.get(k).getWidth() &&
                        PlannedPLList.get(i).getLength() == bundleInfosList.get(k).getLength() &&
                        PlannedPLList.get(i).getNoOfPieces() == bundleInfosList.get(k).getNoOfPieces()) {

                    PlannedPLList.get(i).setExist(true);
                } else {
                    PlannedPLList.get(i).setExist(false);
                }

                Log.e("****out", "***" + PlannedPLList.get(i).getThickness() + "==" + bundleInfosList.get(k).getThickness() + "&&" +
                        PlannedPLList.get(i).getWidth() + "==" + bundleInfosList.get(k).getWidth() + "&&" +
                        PlannedPLList.get(i).getLength() + "==" + bundleInfosList.get(k).getLength() + "&&" +
                        PlannedPLList.get(i).getNoOfPieces() + "==" + bundleInfosList.get(k).getNoOfPieces());

            }
        }

//        TableRow tableRow = (TableRow) headerTableLayout.getChildAt(0);
//        TextView textView = (TextView) tableRow.getChildAt(7);
//        textView.setVisibility(View.VISIBLE);

        for (int i = 0; i < PlannedPLList.size(); i++) {

            TableRow tableRow2 = (TableRow) tableLayout.getChildAt(i);
            TextView textView2 = (TextView) tableRow2.getChildAt(8);

            if (PlannedPLList.get(i).getExist()) {
                textView2.setText("Exist");
                textView2.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
            } else {
                textView2.setText("Not Exist");
                textView2.setTextColor(ContextCompat.getColor(this, R.color.preview));
            }

        }

    }

    void init() {

        searchCustomer = findViewById(R.id.cust);
        paclingList = findViewById(R.id.p_list);
        destination = findViewById(R.id.destination);
        orderNo = findViewById(R.id.order_no);
        addButton = findViewById(R.id.add_button);
        checkButton = findViewById(R.id.check_button);
        saveButton = findViewById(R.id.save_button);
        addCust = findViewById(R.id.add_cust);
        thickness = findViewById(R.id.thickness);
        width = findViewById(R.id.width);
        length = findViewById(R.id.length);
        noOfPieces = findViewById(R.id.pieces);
        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);

        noOfPieces.requestFocus();
    }
}
