package com.falconssoft.woodysystem.stage_two;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.SharedClass;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.CustomerInfo;
import com.falconssoft.woodysystem.models.PlannedPL;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.falconssoft.woodysystem.reports.InventoryReportAdapter;

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

public class UnloadPackingList extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler databaseHandler;
    private Calendar myCalendar;
    private Settings generalSettings;
    private Dialog searchDialog , searchDialog2;

    public static int flag2;
    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";

    private UnloadPLAdapter adapter2;
    private SupplierAdapter adapter3;

    private RecyclerView recyclerView , recyclerView2;
    private RecyclerView recycler;
    private EditText paclingList , dest , orderNo;
    private TextView searchCustomer, searchSupplier, noBundles, totalCBM, delete;
    ;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private CustomerAdapter adapter;
    public static String customerName = "", customerNo = "";
    public static String supplierName = "", supplierNo = "";
    private List<CustomerInfo> customers;
    private List<CustomerInfo> arraylist;
    private List<SupplierInfo> arraylist2;
    private List<PlannedPL> PLList = new ArrayList<>();
    private List<PlannedPL> UPLListFiltered = new ArrayList<>();
    private List<SupplierInfo> suppliers;
    TextView export;

    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    private Spinner gradeSpinner;
    private String gradeText = "All";
    private ProgressDialog progressDialog;
    private RelativeLayout containerLayout;
    private SharedClass sharedClass;

    private SimpleDateFormat sdf, dfReport;
    private String today;

    private JSONArray plannedPLListJSON = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unload_planned_packing_list);

        init();

        databaseHandler = new DatabaseHandler(UnloadPackingList.this);
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        sharedClass = new SharedClass(this);
        customers = new ArrayList<>();
        arraylist = new ArrayList<>();
        arraylist2 = new ArrayList<SupplierInfo>();
        suppliers = new ArrayList<>();

        myCalendar = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        today = sdf.format(myCalendar.getTime());
        dfReport = new SimpleDateFormat("yyyyMMdd_hhmmss");

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");

        paclingList.requestFocus();

        gradeList.clear();
        gradeList.add("All");
        gradeList.add("Fresh");
        gradeList.add("KD");
        gradeList.add("Blue Stain");
        gradeList.add("KD Blue Stain");
        gradeList.add("AST");
        gradeList.add("AST Blue Stain");
        gradeList.add("Second Sort");
        gradeList.add("Reject");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradeText = parent.getItemAtPosition(position).toString();
                filters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        paclingList.addTextChangedListener(new watchTextChange(paclingList));
        dest.addTextChangedListener(new watchTextChange(dest));
        orderNo.addTextChangedListener(new watchTextChange(orderNo));

        delete.setOnClickListener(this);
        searchCustomer.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);
        export.setOnClickListener(this);

        new JSONTask2().execute();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
//            case R.id.search:
//
//                if (!TextUtils.isEmpty(searchCustomer.getText().toString()) && !TextUtils.isEmpty(paclingList.getText().toString())) {
//                    new JSONTask2().execute();
//                } else
//                    Toast.makeText(this, "Please select customer and packing list first!", Toast.LENGTH_SHORT).show();
//                break;
            case R.id.delete:
                // if (plannedPLListJSON.length() > 0)
                if (UPLListFiltered.size()>0){
                Dialog passwordDialog = new Dialog(this);
                passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                passwordDialog.setContentView(R.layout.password_dialog);
                passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
                TextView done = passwordDialog.findViewById(R.id.password_dialog_done);
                done.setText(R.string.loaded);

                done.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (password.getText().toString().equals("0200200")) {
                            progressDialog.show();
                            new JSONTask3().execute();
                            passwordDialog.dismiss();
                        } else {
                            sharedClass.showSnackbar(containerLayout, getString(R.string.not_authorized), false);
                            password.setText("");
                        }
                    }
                });
                passwordDialog.show();
                }
                break;
            case R.id.cust:
                customers.clear();
                new JSONTask().execute();

                searchDialog = new Dialog(this);
                searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                searchDialog.setContentView(R.layout.search_customer_dialog);
                searchDialog.setCancelable(true);

                SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
                TextView close = searchDialog.findViewById(R.id.search_supplier_close);

                recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new CustomerAdapter(2, this, customers);
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
                break;

            case R.id.sup:
                suppliers.clear();
                new JSONTask4().execute();

                suppliersDialog();

                break;
            case R.id.export:
                ExportToExcel obj = new ExportToExcel(UnloadPackingList.this);
                obj.exportUnloadPackingList(UPLListFiltered, searchCustomer.getText().toString(), searchSupplier.getText().toString(),  dfReport.format(Calendar.getInstance().getTime()), gradeText, today);

                break;

        }
    }

    void suppliersDialog() {

        searchDialog2 = new Dialog(this);
        searchDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog2.setContentView(R.layout.search_supplier_dialog);
        searchDialog2.setCancelable(true);

        SearchView searchView = searchDialog2.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog2.findViewById(R.id.search_supplier_close);

        recyclerView2 = searchDialog2.findViewById(R.id.search_supplier_recyclerView);
        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        adapter3 = new SupplierAdapter(2, this, suppliers);
        recyclerView2.setAdapter(adapter3);

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

    public void filters() {

        UPLListFiltered.clear();
        for (int i = 0; i < PLList.size(); i++) {

            if (customerName.equals(PLList.get(i).getCustName()) || customerName.equals("All") || customerName.equals("")) {
                if (supplierName.equals(PLList.get(i).getCustName()) || supplierName.equals("All") || supplierName.equals("")) {
                    if (PLList.get(i).getPackingList().startsWith(paclingList.getText().toString()) || paclingList.getText().toString().equals("")) {
                        if (PLList.get(i).getDestination().startsWith(dest.getText().toString()) || dest.getText().toString().equals("")) {
                            if (PLList.get(i).getOrderNo().startsWith(orderNo.getText().toString()) || orderNo.getText().toString().equals("")) {
                                if (PLList.get(i).getGrade().startsWith(gradeText) || gradeText.equals("All")) {


                                    UPLListFiltered.add(PLList.get(i));
                                }
                            }
                        }
                    }
                }
            }
        }

        adapter2 = new UnloadPLAdapter(this, UPLListFiltered);
        recycler.setAdapter(adapter2);

        calculateTotal();
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
        adapter = new CustomerAdapter(2, this, arraylist);
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
        adapter3 = new SupplierAdapter(2, this, arraylist2);
        recyclerView2.setAdapter(adapter3);
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
//            switch (view.getId()) {
//                case R.id.p_list:
                    filters();
//                    break;
//            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 11; i++) {
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
                    break;
                case 3:
                    textView.setText("Destination");
                    break;
                case 4:
                    textView.setText("Order No");
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
                    textView.setText("Pieces");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 8:
                    textView.setText("# Bundles");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 9:
                    textView.setText("Cubic");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 10:
                    textView.setText("");
                    textView.setLayoutParams(textViewParam2);
                    break;
            }
            tableRow.addView(textView);
        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    public void getSearchCustomerInfo(String customerNameLocal, String customerNoLocal) {
        customerName = customerNameLocal;
        customerNo = customerNoLocal;
        searchCustomer.setText(customerName);
        searchCustomer.setError(null);
        searchDialog.dismiss();
        filters();
    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal) {
        supplierName = supplierNameLocal;
        supplierNo = supplierNoLocal;
        searchSupplier.setText(supplierName);
        searchSupplier.setError(null);
        searchDialog2.dismiss();
        filters();
    }

    

    // ************************************** GET CUSTOMERS *******************************
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

                customers.add(new CustomerInfo("0", "All"));
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
                Toast.makeText(UnloadPackingList.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }  // import customers

    // ************************************** SEARCH *******************************
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
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/import.php?FLAG=9"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                //nameValuePairs.add(new BasicNameValuePair("CUSTOMER", searchCustomer.getText().toString()));
                //nameValuePairs.add(new BasicNameValuePair("PACKING_LIST", paclingList.getText().toString()));

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

                PLList.clear();
                UPLListFiltered.clear();

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("PLANNED");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        PlannedPL planned = new PlannedPL();
                        planned.setCustName(innerObject.getString("CUST_NAME"));
                        planned.setSupplier(innerObject.getString("SUPPLIER"));
                        planned.setGrade(innerObject.getString("GRADE"));
                        planned.setPackingList(innerObject.getString("PACKING_LIST"));
                        planned.setDestination(innerObject.getString("DESTINATION"));
                        planned.setOrderNo(innerObject.getString("ORDER_NO"));
                        planned.setNoOfPieces(innerObject.getDouble("PIECES"));
                        //planned.setLength(innerObject.getDouble("LENGTH"));
                        //planned.setWidth(innerObject.getDouble("WIDTH"));
                        //planned.setThickness(innerObject.getDouble("THICKNESS"));
                        planned.setNoOfCopies(innerObject.getInt("COUNT"));

                        float cub = Float.parseFloat(innerObject.getString("CUBIC"));
                        double c = cub * planned.getNoOfCopies() / 1000000000;
                        planned.setCubic(c);

                        Log.e("***1**", "" + cub + " * " + planned.getNoOfCopies() + " / " + "1000000000 = " + (cub * planned.getNoOfCopies() / 1000000000));

                        PLList.add(planned);
                        //UPLListFiltered.add(planned);

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

            if (s != null) {

                if (PLList.size() > 0) {

                    PLList = clustering(PLList);
                    UPLListFiltered.addAll(PLList);

                    if (headerTableLayout.getChildCount() == 0)
                        addTableHeader(headerTableLayout);

                    adapter2.notifyDataSetChanged();

                    calculateTotal();

//                    for (int i = 0; i < PLList.size(); i++) {
//                        plannedPLListJSON.put(PLList.get(i).getJSONObject());
//                    }
                }
            } else {
                Toast.makeText(UnloadPackingList.this, "No data found!", Toast.LENGTH_SHORT).show();
//                Toast.makeText(UnloadPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    List<PlannedPL> clustering(List<PlannedPL> list) {

        List<PlannedPL> tempList = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {

            boolean exist = false;
            for (int k = 0; k < tempList.size(); k++) {
                if (list.get(i).getPackingList().equals(tempList.get(k).getPackingList())) {
                    exist = true;
                    break;
                }
            }

            if (!exist) {

                double cubicSum = list.get(i).getCubic();
                int copiesSum = list.get(i).getNoOfCopies();
                double piecesSum = list.get(i).getNoOfPieces();

                for (int k = i + 1; k < list.size(); k++) {
                    if (list.get(i).getPackingList().equals(list.get(k).getPackingList()) &&
                            list.get(i).getCustName().equals(list.get(k).getCustName()) &&
                            list.get(i).getOrderNo().equals(list.get(k).getOrderNo()) &&
                            list.get(i).getDestination().equals(list.get(k).getDestination())) {

                        cubicSum += list.get(k).getCubic();
                        copiesSum += list.get(k).getNoOfCopies();
                        piecesSum += list.get(k).getNoOfPieces();
                    }
                }

                tempList.add(list.get(i));
                tempList.get(tempList.size() - 1).setCubic(cubicSum);
                tempList.get(tempList.size() - 1).setNoOfCopies(copiesSum);
                tempList.get(tempList.size() - 1).setNoOfPieces(piecesSum);
            }


        }

        return tempList;

    }

    // ************************************** UNLOAD P.LIST *******************************
    private class JSONTask3 extends AsyncTask<String, String, String> {  // save

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


                plannedPLListJSON = new JSONArray();

                for (int i = 0; i < UPLListFiltered.size(); i++) {
                    if (UPLListFiltered.get(i).getIsChecked())
                        plannedPLListJSON.put(UPLListFiltered.get(i).getJSONObject());
                }

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("LOADED_PLANNED_PACKING_LIST", plannedPLListJSON.toString()));
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

            progressDialog.dismiss();
            if (s != null) {
                if (s.contains("PLANNED_PACKING_LIST SUCCESS")) {
                    Toast.makeText(UnloadPackingList.this, "Loaded!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "PLANNED_PACKING_LIST SUCCESS");

                    //tableLayout.removeAllViews();
//                    for (int i = 0; i < UPLListFiltered.size(); i++) {
//                        if (UPLListFiltered.get(i).getIsChecked()) {
//                            // PLList.get(i).setIsChecked(false);
//                            UPLListFiltered.remove(i);
//                        }
//                    }
                    int localSize = UPLListFiltered.size();
                    for (int i = 0; i < localSize; i++) {
                        if (PLList.get(i).getIsChecked()) {
                            // PLList.get(i).setIsChecked(false);
                            Log.e("tracking ", PLList.get(i).getPackingList() + PLList.get(i).getIsChecked());
                            PLList.remove(i);
                            localSize--;
                        }
                    }
                    adapter2.notifyDataSetChanged();

                    searchCustomer.setText("");
                    searchSupplier.setText("");
                    paclingList.setText("");
                    dest.setText("");
                    orderNo.setText("");

                } else {
                    Toast.makeText(UnloadPackingList.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "Failed to export data!");
                }

            } else {
                Toast.makeText(UnloadPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class JSONTask4 extends AsyncTask<String, String, List<SupplierInfo>> {

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

                JSONObject parentObject = new JSONObject(finalJson);

                suppliers.add(new SupplierInfo("0", "All"));
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
                Toast.makeText(UnloadPackingList.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }  // import Suppliers


    void calculateTotal() {
        int sumOfBundles = 0;
        double totalCbm = 0;
        for (int i = 0; i < UPLListFiltered.size(); i++) {
            sumOfBundles += UPLListFiltered.get(i).getNoOfCopies();
            //totalCbm += (UPLListFiltered.get(i).getThickness() * UPLListFiltered.get(i).getWidth() * UPLListFiltered.get(i).getLength() * UPLListFiltered.get(i).getNoOfPieces() * UPLListFiltered.get(i).getNoOfCopies());
            totalCbm += UPLListFiltered.get(i).getCubic();
        }

        noBundles.setText("" + sumOfBundles);
        totalCBM.setText("" + String.format("%.3f", (totalCbm)));
    }

    void init() {

        searchCustomer = findViewById(R.id.cust);
        searchSupplier = findViewById(R.id.sup);
        paclingList = findViewById(R.id.p_list);
        dest = findViewById(R.id.destination);
        orderNo = findViewById(R.id.order_no);
        export = findViewById(R.id.export);

        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        noBundles = findViewById(R.id.no_bundles);
        totalCBM = findViewById(R.id.total_cbm);
        delete = findViewById(R.id.delete);
        gradeSpinner = findViewById(R.id.grade);
        containerLayout = findViewById(R.id.unloadBackingList_coordinator);

        recycler = findViewById(R.id.recycler2);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new UnloadPLAdapter(this, UPLListFiltered);
        recycler.setAdapter(adapter2);

    }
}
