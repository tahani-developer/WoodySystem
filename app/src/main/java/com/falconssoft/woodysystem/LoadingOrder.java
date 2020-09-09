package com.falconssoft.woodysystem;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

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
import java.util.ArrayList;
import java.util.List;

public class LoadingOrder extends AppCompatActivity {

    private ImageButton deleteBarcode;
    private GridView items;
    private Button done, barcode, addBundle;
    private EditText searchViewTh, searchViewW, searchViewL;
    private DatabaseHandler DHandler;
    public static List<BundleInfo> bundles, filteredList, selectedBundle;
    private String f1 = "", f2 = "", f3 = "", barcodeValue = "", loc;
    public static ItemsListAdapter adapter;
    private Activity activity;
    public static TextView searchBar;
    private int no = 0;

    static ListView listView2;
    static HorizontalListView listView;
    private ItemsListAdapter5 adapter2;
    private final String STATE_VISIBILITY = "state-visibility";
    private boolean mState = false;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order);

        items = (GridView) findViewById(R.id.items);
        searchViewTh = findViewById(R.id.mSearchTh);
        searchViewW = findViewById(R.id.mSearchW);
        searchViewL = findViewById(R.id.mSearchL);
        done = (Button) findViewById(R.id.done);
        barcode = (Button) findViewById(R.id.barcode);
        addBundle = (Button) findViewById(R.id.add);
        deleteBarcode = (ImageButton) findViewById(R.id.deletebaarcode);
        listView2 = findViewById(R.id.verticalListView);
        listView = findViewById(R.id.listview);

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");

        DHandler = new DatabaseHandler(LoadingOrder.this);
        loc = DHandler.getSettings().getStore();
//        bundles = DHandler.getBundleInfo();
        searchBar = findViewById(R.id.searchBar);

        bundles = new ArrayList<>();
        selectedBundle = new ArrayList<>();


        new JSONTask().execute();

        filteredList = new ArrayList<>();


        activity = this;

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("SCAN");
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();

//                Intent intent=new Intent(LoadingOrder.this,QrReader.class);
//                startActivity(intent);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listContainsItems()) {
                    Intent intent = new Intent(LoadingOrder.this, LoadingOrder2.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoadingOrder.this, "No item selected !", Toast.LENGTH_LONG).show();
                }
            }
        });

        deleteBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filter(1);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles, true);
//                items.setAdapter(adapter);

            }
        });

        addBundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoadingOrder.this, AddToInventory.class);
                intent.putExtra("flag", "1");
                startActivity(intent);
            }
        });

        searchViewTh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f1 = String.valueOf(s);
                filter(1);
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f1 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchBar.getText().toString().equals("1")) {
                    searchByBundleNo(barcodeValue);
                } else if (searchBar.getText().toString().equals("2")) {
                    Intent i = new Intent(LoadingOrder.this, LoadingOrder.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchViewW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f2 = String.valueOf(s);
                filter(1);
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewW.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f2 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });

        searchViewL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f3 = String.valueOf(s);
                filter(1);
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewL.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f3 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });
    }

    void filter(int flag) {
        List<BundleInfo> tempList = new ArrayList<>();
        if (flag == 0) {
            tempList.addAll(bundles);
        } else {

            for (int k = 0; k < bundles.size(); k++) {
                if (
                        (("" + bundles.get(k).getThickness()).toUpperCase().startsWith(f1) || f1.equals("")) &&
                                (("" + bundles.get(k).getWidth()).toUpperCase().startsWith(f2) || f2.equals("")) &&
                                (("" + bundles.get(k).getLength()).toUpperCase().startsWith(f3) || f3.equals(""))) {
                    bundles.get(k).setFoucoseColor("0");
                    tempList.add(bundles.get(k));
                }
            }

        }
        ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, tempList, false);
        items.setAdapter(adapter);
//        return tempList;
    }

    boolean listContainsItems() {
        ItemsListAdapter obj = new ItemsListAdapter();
        List<BundleInfo> bundles = obj.getSelectedItems();

        boolean test = false;
        for (int i = 0; i < bundles.size(); i++) {
            if (bundles.get(i).getChecked()) {
                test = true;
                break;
            }
        }
        return test;
    }

    void searchByBundleNo(String Bundul) {

        Log.e("searchByBundleNo ", "" + barcodeValue + "\n" + "th =" + Bundul);

        no = 0;

        if (!barcodeValue.equals("cancelled")) {
            for (int k = 0; k < bundles.size(); k++) {
                if ((bundles.get(k).getBundleNo()).equals(Bundul)) {
                    no = k;
//                    try {
//                        Log.e("searchByBundleNo_13 ", "" + barcodeValue + "\n" + "th =" + no );
//                        Thread.sleep(120);
////                        items.setSelection(no);
//                        items.requestFocusFromTouch();
//                        items.setSelection(no);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


                    items.clearFocus();
//                    items.post(new Runnable() {
//                        @Override
//                        public void run() {
                            bundles.get(no).setFoucoseColor("1");
                            items.setAdapter(adapter);
                            filter(0);
                            items.setSelection(no);
                            items.requestFocusFromTouch();
                            items.setSelection(no);

//                    items.smoothScrollToPosition(no);

//                        }
//                    });


                    break;
                }
            }

        } else {
            filter(0);
//            ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//            items.setAdapter(adapter);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Result != null) {
            if (Result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                barcodeValue = "cancelled";
                searchBar.setText("0");
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();

                barcodeValue = Result.getContents();
//                String[] arrayString = barcodeValue.split(" ");

//Log.e("barcode_value ",""+barcodeValue+"\n"+"th ="+arrayString[0]+"\n"+"w ="+arrayString[1]+"\n"+"l ="
//        +arrayString[2]+"\n"+"grad ="+arrayString[3]);
//                searchByBundleNo(barcodeValue);
                searchBar.setText("1");
//                searchByBundleNo(barcodeValue);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(LoadingOrder.this, Stage3.class);
//        startActivity(intent);
        finish();
    }


    private class JSONTask extends AsyncTask<String, String, List<BundleInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<BundleInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + DHandler.getSettings().getIpAddress() + "/import.php?FLAG=3");

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
                // Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_INFO");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        if (innerObject.getInt("ORDERED") == 0
                                && innerObject.getString("LOCATION").equals(loc)) {

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
                            bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                            bundleInfo.setCustomer(innerObject.getString("CUSTOMER"));
                            bundleInfo.setChecked(false);
                            bundleInfo.setFoucoseColor("0");

                            bundles.add(bundleInfo);
                        }
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
            return bundles;
        }


        @Override
        protected void onPostExecute(final List<BundleInfo> result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            if (result != null) {
                Log.e("result", "*****************" + result.size());
                filter(0);
//                adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//                items.setAdapter(adapter);

                adapter2 = new ItemsListAdapter5(LoadingOrder.this, selectedBundle);
                listView2.setAdapter(adapter2);
                listView.setAdapter(adapter2);

            } else {
                Toast.makeText(LoadingOrder.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_VISIBILITY, mState);

//        outState.putSerializable("selectedBundle", (Serializable) selectedBundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restore state members from saved instance
        mState = savedInstanceState.getBoolean(STATE_VISIBILITY);

        ItemsListAdapter obj = new ItemsListAdapter();
        bundles = obj.getSelectedItems();
        filter(0);
//        adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//        items.setAdapter(adapter);


        selectedBundle = obj.getSelectedItems();
        adapter2 = new ItemsListAdapter5(LoadingOrder.this, selectedBundle);
        listView2.setAdapter(adapter2);
        listView.setAdapter(adapter2);

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void notifyAdapter(BundleInfo bundle, Context context) {

//        if (bundle.getChecked()) {
//            selectedBundle.add(bundle);
//        } else {
//
//            for (int i = 0; i < selectedBundle.size(); i++) {
//                if (bundle.getBundleNo().equals(selectedBundle.get(i).getBundleNo())) {
//                    selectedBundle.remove(i);
//                    break;
//                }
//            }
//        }

        ItemsListAdapter obj = new ItemsListAdapter();
        selectedBundle = obj.getSelectedItems();
        adapter2 = new ItemsListAdapter5(context, selectedBundle);
        listView2.setAdapter(adapter2);
        listView.setAdapter(adapter2);


    }


}
