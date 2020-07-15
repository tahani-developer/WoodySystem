package com.falconssoft.woodysystem.stage_two;

import android.app.Dialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
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

public class UnloadPackingList extends AppCompatActivity implements View.OnClickListener {

    private DatabaseHandler databaseHandler;
    private Calendar myCalendar;
    private Settings generalSettings;
    private Dialog searchDialog;

    private boolean mState = false;
    private final String STATE_VISIBILITY = "state-visibility";

    private UnloadPLAdapter adapter2;

    private RecyclerView recyclerView;
    private RecyclerView recycler;
    private EditText paclingList;
    private TextView searchCustomer, search, unload, noBundles, totalCBM, delete;
    ;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private CustomerAdapter adapter;
    public static String customerName = "", customerNo = "";
    private List<CustomerInfo> customers;
    private List<CustomerInfo> arraylist;
    private List<PlannedPL> PLList = new ArrayList<>();
    private List<PlannedPL> PLListFiltered = new ArrayList<>();

    SimpleDateFormat sdf;
    String today;

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
        customers = new ArrayList<>();
        arraylist = new ArrayList<>();

        myCalendar = Calendar.getInstance();

        String myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        today = sdf.format(myCalendar.getTime());

        paclingList.requestFocus();

        paclingList.addTextChangedListener(new watchTextChange(paclingList));

        search.setOnClickListener(this);
        unload.setOnClickListener(this);
        delete.setOnClickListener(this);
        searchCustomer.setOnClickListener(this);

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
                    new JSONTask3().execute();
//                else
//                    Toast.makeText(this, "Please choose customer and packing list first!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.cust:
                customers.clear();
                new JSONTask().execute();

                searchDialog = new Dialog(this);
                searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                searchDialog.setContentView(R.layout.search_customer_dialog);
                searchDialog.setCancelable(false);

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
        }
    }

    public void filters() {

        PLListFiltered.clear();
        for (int i = 0; i < PLList.size(); i++) {

            if (customerName.equals(PLList.get(i).getCustName()) || customerName.equals("All")) {
                if (PLList.get(i).getPackingList().startsWith(paclingList.getText().toString()) || paclingList.getText().toString().equals("")) {

                    PLListFiltered.add(PLList.get(i));

                }
            }

        }

        adapter2 = new UnloadPLAdapter(this, PLListFiltered);
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

    public void getSearchSupplierInfo(String supplierName, String supplierNo) {
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
                case R.id.p_list:
                    filters();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        for (int i = 0; i < 10; i++) {
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
                    textView.setText("Pieces");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 7:
                    textView.setText("# Bundles");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 8:
                    textView.setText("Cubic");
                    textView.setLayoutParams(textViewParam3);
                    break;
                case 9:
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

                customers.add(new CustomerInfo("0" , "All"));
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
                PLListFiltered.clear();

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("PLANNED");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        PlannedPL planned = new PlannedPL();
                        planned.setCustName(innerObject.getString("CUST_NAME"));
                        planned.setSupplier(innerObject.getString("SUPPLIER"));
                        planned.setPackingList(innerObject.getString("PACKING_LIST"));
                        planned.setDestination(innerObject.getString("DESTINATION"));
                        planned.setOrderNo(innerObject.getString("ORDER_NO"));
                        planned.setNoOfPieces(innerObject.getDouble("PIECES"));
                        planned.setLength(innerObject.getDouble("LENGTH"));
                        planned.setWidth(innerObject.getDouble("WIDTH"));
                        planned.setThickness(innerObject.getDouble("THICKNESS"));
                        planned.setNoOfCopies(innerObject.getInt("COUNT"));


                        PLList.add(planned);
                        PLListFiltered.add(planned);

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

                for (int i = 0; i < PLListFiltered.size(); i++) {
                    if (PLListFiltered.get(i).getIsChecked())
                        plannedPLListJSON.put(PLListFiltered.get(i).getJSONObject());
                }

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("UNLOAD_PLANNED_PACKING_LIST", plannedPLListJSON.toString()));
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
                    Toast.makeText(UnloadPackingList.this, "Loaded!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "PLANNED_PACKING_LIST SUCCESS");

                    //tableLayout.removeAllViews();
                    for (int i = 0; i < PLListFiltered.size(); i++) {
                        if (PLList.get(i).getIsChecked()) {
                           // PLList.get(i).setIsChecked(false);
                            PLListFiltered.remove(i);
                        }
                    }
                    adapter2.notifyDataSetChanged();

                    searchCustomer.setText("");
                    paclingList.setText("");

                } else {
                    Toast.makeText(UnloadPackingList.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "Failed to export data!");
                }

            } else {
                Toast.makeText(UnloadPackingList.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


    void calculateTotal() {
        int sumOfBundles = 0;
        double totalCbm = 0;
        for (int i = 0; i < PLListFiltered.size(); i++) {
            sumOfBundles += (PLListFiltered.get(i).getNoOfPieces() * PLListFiltered.get(i).getNoOfCopies());
            totalCbm += (PLListFiltered.get(i).getThickness() * PLListFiltered.get(i).getWidth() * PLListFiltered.get(i).getLength() * PLListFiltered.get(i).getNoOfPieces() * PLListFiltered.get(i).getNoOfCopies());
        }

        noBundles.setText("" + sumOfBundles);
        totalCBM.setText("" + String.format("%.3f", (totalCbm / 1000000000)));
    }

    void init() {

        searchCustomer = findViewById(R.id.cust);
        paclingList = findViewById(R.id.p_list);

        search = findViewById(R.id.search);
        unload = findViewById(R.id.unload);
        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        noBundles = findViewById(R.id.no_bundles);
        totalCBM = findViewById(R.id.total_cbm);
        delete = findViewById(R.id.delete);

        recycler = findViewById(R.id.recycler);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        adapter2 = new UnloadPLAdapter(this, PLListFiltered);
        recycler.setAdapter(adapter2);

    }
}
