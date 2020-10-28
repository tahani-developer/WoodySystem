package com.falconssoft.woodysystem.stage_one;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
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
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class AddNewSupplier extends AppCompatActivity {

    private DatabaseHandler DHandler;
    private RecyclerView recyclerView;
    private AddSupplierAdapter adapter;
    public static final String BACK_FLAG = "BACK_FLAG";
    //    private TableLayout tableLayout;
    private EditText supName;
    private TextView add;
    private List<SupplierInfo> suppliers;
    private JSONArray jsonArray;
    private int i;
    private static String supNameDel = "12";
    private static String supNoDel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_supplier);

        jsonArray = new JSONArray();
        DHandler = new DatabaseHandler(AddNewSupplier.this);
        suppliers = new ArrayList<>();
        supName = findViewById(R.id.editText);
        add = findViewById(R.id.textView15);
        recyclerView = findViewById(R.id.addSupplier_recyclerView);
//        tableLayout = findViewById(R.id.addNewRaw_table);

        new JSONTask().execute();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddSupplierAdapter(AddNewSupplier.this, suppliers);
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(supName.getText().toString())) {

                    jsonArray = new JSONArray();
                    SupplierInfo supplierInfo = new SupplierInfo();
                    try {
                        int max = Integer.parseInt(suppliers.get(0).getSupplierNo());
                        for (int i = 1; i < suppliers.size(); i++)
                            if (Integer.parseInt(suppliers.get(i).getSupplierNo()) > max)
                                max = Integer.parseInt(suppliers.get(i).getSupplierNo());
                        supplierInfo.setSupplierNo("" + (max + 1));
                    } catch (Exception e) {
                        supplierInfo.setSupplierNo("" + (suppliers.size() + 1));
                    }
//                    supplierInfo.setSupplierNo("" + (suppliers.size() + 1));
                    supplierInfo.setSupplierName(supName.getText().toString());

                    suppliers.add(supplierInfo);
                    jsonArray.put(supplierInfo.getJSONObject());
                    new JSONTask2().execute();
                }
            }
        });

    }

    void deleteSupplier(int index) {
        i = index;
        supNameDel = suppliers.get(index).getSupplierName();
        Log.e("****", supNameDel);
        supNoDel = suppliers.get(index).getSupplierNo();
        new JSONTask3().execute();
    }

//    @Override
//    public void onBackPressed() {
////        super.onBackPressed();
//        Intent intent = new Intent(AddNewSupplier.this, AddNewRaw.class);
//        startActivity(intent);
//        finish();
//    }

    // *************************************** GET SUPPLIERS ***************************************
    private class JSONTask extends AsyncTask<String, String, List<SupplierInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SupplierInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {//          http://10.0.0.214/woody/import.php?FLAG=4
                URL url = new URL("http://" + DHandler.getSettings().getIpAddress() + "/import.php?FLAG=4");

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
//                fillTable();
                adapter.notifyDataSetChanged();
            } else {
                Toast.makeText(AddNewSupplier.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // *************************************** ADD SUPPLIERS ***************************************
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
                request.setURI(new URI("http://" + DHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("SUPPLIERS", jsonArray.toString().trim()));

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
                if (s.contains("SUPPLIERS SUCCESS")) {
                    adapter.notifyDataSetChanged();
//                    addSupp();

                    supName.setText("");
                    Log.e("tag", "****Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }

    // *************************************** DELETE SUPPLIER ***************************************
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
                request.setURI(new URI("http://" + DHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("DELETE_SUPPLIERS", "1"));
                nameValuePairs.add(new BasicNameValuePair("DELETE_SUPPLIER_NAME", supNameDel));
                nameValuePairs.add(new BasicNameValuePair("DELETE_SUPPLIER_NO", supNoDel));
                Log.e("****in", supNameDel);
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
                if (s.contains("DELETE_SUPPLIER SUCCESS")) {

                    suppliers.remove(i);
                    adapter.notifyDataSetChanged();


                    Toast.makeText(AddNewSupplier.this, "Supplier Deleted!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "DELETE_SUPPLIER SUCCESS");
                } else {
                    Toast.makeText(AddNewSupplier.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                    Log.e("tag", "Failed to export data!");
                }

            } else {
                Toast.makeText(AddNewSupplier.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
