package com.falconssoft.woodysystem.stage_one;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
//    private TableLayout tableLayout;
    private EditText supName;
    private TextView add;
    private List<SupplierInfo> suppliers;
    private JSONArray jsonArray;


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
        adapter = new AddSupplierAdapter(suppliers);
        recyclerView.setAdapter(adapter);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!supName.getText().toString().equals("")) {

                    jsonArray = new JSONArray();
                    SupplierInfo supplierInfo = new SupplierInfo();
                    supplierInfo.setSupplierNo("" + (suppliers.size() + 1));
                    supplierInfo.setSupplierName(supName.getText().toString());

                    suppliers.add(supplierInfo);
                    jsonArray.put(supplierInfo.getJSONObject());
                    new JSONTask2().execute();
                }
            }
        });

    }

//    void fillTable() {
//
//        tableLayout.removeAllViews();
//        for (int k = 0; k < suppliers.size(); k++) {
//            TableRow tableRow = new TableRow(this);
//            for (int i = 0; i < 2; i++) {
//                TextView textView = new TextView(this);
//                textView.setBackgroundResource(R.color.light_orange);
//                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//                textViewParam.setMargins(1, 1, 0, 0);
//                textView.setTextSize(15);
//                textView.setTextColor(ContextCompat.getColor(this, R.color.black));
//                textView.setLayoutParams(textViewParam);
//                switch (i) {
//                    case 0:
//                        textView.setText(suppliers.get(k).getSupplierNo());
//                        break;
//                    case 1:
//                        textView.setText(suppliers.get(k).getSupplierName());
//                        break;
//                }
//                tableRow.addView(textView);
//            }
//            tableLayout.addView(tableRow);
//        }
//    }

//    void addSupp() {
//
//        TableRow tableRow = new TableRow(this);
//        for (int i = 0; i < 2; i++) {
//            TextView textView = new TextView(this);
//            textView.setBackgroundResource(R.color.light_orange);
//            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//            textViewParam.setMargins(1, 1, 0, 0);
//            textView.setTextSize(15);
//            textView.setTextColor(ContextCompat.getColor(this, R.color.black));
//            textView.setLayoutParams(textViewParam);
//            switch (i) {
//                case 0:
//                    textView.setText("" + (suppliers.size()));
//                    break;
//                case 1:
//                    textView.setText(supName.getText().toString());
//                    break;
//            }
//            tableRow.addView(textView);
//        }
//        tableLayout.addView(tableRow);
//
//    }

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


}
