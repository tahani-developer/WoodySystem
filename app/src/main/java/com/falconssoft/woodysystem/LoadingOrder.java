package com.falconssoft.woodysystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.support.v7.widget.SearchView;

import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class LoadingOrder extends AppCompatActivity {

    private ImageButton deleteBarcode;
    private GridView items;
    private Button done, barcode;
    View view;
    private SearchView searchViewTh, searchViewW, searchViewL;
    private DatabaseHandler DHandler;
    private List<BundleInfo> bundles, filteredList;
    private String f1 = "", f2 = "", f3 = "", barcodeValue = "";
    private ItemsListAdapter adapter;
    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order);

        items = (GridView) findViewById(R.id.items);
        searchViewTh = (SearchView) findViewById(R.id.mSearchTh);
        searchViewW = (SearchView) findViewById(R.id.mSearchW);
        searchViewL = (SearchView) findViewById(R.id.mSearchL);
        done = (Button) findViewById(R.id.done);
        barcode = (Button) findViewById(R.id.barcode);
        deleteBarcode = (ImageButton) findViewById(R.id.deletebaarcode);
        DHandler = new DatabaseHandler(LoadingOrder.this);
//        bundles = DHandler.getBundleInfo();

        bundles = new ArrayList<>();

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
//                    setSlideAnimation();
                } else {
                    Toast.makeText(LoadingOrder.this, "No item selected !", Toast.LENGTH_LONG).show();
                }
            }
        });

        deleteBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                items.setAdapter(adapter);

            }
        });

        searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f1 = query;
                filteredList = filter(bundles, f1, f2, f3);
                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                items.setAdapter(adapter);

                return false;
            }
        });

        searchViewW.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f2 = query;
                filteredList = filter(bundles, f1, f2, f3);
                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                items.setAdapter(adapter);

                return false;
            }
        });

        searchViewL.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                f3 = query;
                filteredList = filter(bundles, f1, f2, f3);
                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                items.setAdapter(adapter);

                return false;
            }
        });
    }

    List<BundleInfo> filter(List<BundleInfo> list, String s1, String s2, String s3) {
        List<BundleInfo> tempList = new ArrayList<>();
        for (int k = 0; k < list.size(); k++) {
            if (
                    (("" + list.get(k).getThickness()).toUpperCase().startsWith(s1) || s1.equals("")) &&
                            (("" + list.get(k).getWidth()).toUpperCase().startsWith(s2) || s2.equals("")) &&
                            (("" + list.get(k).getLength()).toUpperCase().startsWith(s3) || s3.equals("")))
                tempList.add(list.get(k));
        }

        return tempList;
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

        int no = 0;

        if (!barcodeValue.equals("cancelled")) {
            for (int k = 0; k < bundles.size(); k++) {
                if ((bundles.get(k).getBundleNo()).equals(Bundul)) {
                    no = k;
                    items.setSelection(no);
                    items.requestFocusFromTouch();
                    items.setSelection(no);

                    break;
                }
            }

        } else {
            ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
            items.setAdapter(adapter);
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
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();

                barcodeValue = Result.getContents();
//                String[] arrayString = barcodeValue.split(" ");

//Log.e("barcode_value ",""+barcodeValue+"\n"+"th ="+arrayString[0]+"\n"+"w ="+arrayString[1]+"\n"+"l ="
//        +arrayString[2]+"\n"+"grad ="+arrayString[3]);
//                searchByBundleNo(barcodeValue);
                searchByBundleNo(barcodeValue);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(LoadingOrder.this, Stage3.class);
        startActivity(intent);
        finish();
    }

    private class JSONTask extends AsyncTask<String, String, List<BundleInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
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
                Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_INFO");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        if (innerObject.getInt("ORDERED") == 0
                                && innerObject.getString("LOCATION").equals(DHandler.getSettings().getStore())
                                && ((innerObject.getString("BACKING_LIST").equals(""))
                                || (innerObject.getString("BACKING_LIST").equals("null")))) {

                                BundleInfo bundleInfo = new BundleInfo();
                                bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                                bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                                bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                                bundleInfo.setGrade(innerObject.getString("GRADE"));
                                bundleInfo.setNoOfPieces(innerObject.getInt("PIECES"));
                                bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                                bundleInfo.setLocation(innerObject.getString("LOCATION"));
                                bundleInfo.setArea(innerObject.getString("AREA"));
                                bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                                bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                                bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                                bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                                bundleInfo.setChecked(false);

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

            if (result != null) {
                Log.e("result", "*****************" + result.size());
                adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                items.setAdapter(adapter);
            } else {
                Toast.makeText(LoadingOrder.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
