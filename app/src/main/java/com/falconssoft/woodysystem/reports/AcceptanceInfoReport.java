package com.falconssoft.woodysystem.reports;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.SettingsFile;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;

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

public class AcceptanceInfoReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private Settings generalSettings;
    private DatabaseHandler databaseHandler;
    private AcceptanceInfoReportAdapter adapter;
    private static List<NewRowInfo> master, details;
    private ListView listView;
    private Spinner suppliersSpinner, gradeSpinner;
    private List<String> suppliersList, locationList, gradeList;
    private ArrayAdapter<String> suppliersAdapter, locationAdapter, gradeAdapter;
    private EditText truckNo, acceptor, ttnNo, fromThickness, toThickness, fromWidth, toWidth, fromLength, toLength;
    private TextView fromDate, toDate, bundleNo, noOfPieces, cubic;
    private double noOfPiecesSum, cubicSum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_info_report);

        databaseHandler = new DatabaseHandler(this);
        generalSettings = databaseHandler.getSettings();
        master = new ArrayList<>();
        details = new ArrayList<>();

        listView = findViewById(R.id.acceptanceInfo_list);
        truckNo = findViewById(R.id.acceptanceInfo_report_truckNo);
        acceptor = findViewById(R.id.acceptanceInfo_report_acceptor);
        ttnNo = findViewById(R.id.acceptanceInfo_report_ttn);
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
        suppliersSpinner = findViewById(R.id.acceptanceInfo_report_supplier);
//        locationSpinner = findViewById(R.id.acceptanceInfo_report_location);
        gradeSpinner = findViewById(R.id.acceptanceInfo_report_grade);

        adapter = new AcceptanceInfoReportAdapter( details);
        listView.setAdapter(adapter);
        new JSONTask().execute();

        suppliersList = new ArrayList<>();
        locationList = new ArrayList<>();
        gradeList = new ArrayList<>();

    }

    void fillSpinnerAdapter(Spinner gradeSpinner, Spinner suppliersSpinner) {
        gradeList.clear();
        locationList.clear();
        suppliersList.clear();

        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeList.add("KD Blue Stain");
        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(this);

//        suppliersList.add("Zone 1");
//        suppliersList.add("Zone 2");
//        suppliersList.add("Zone 3");
//        suppliersList.add("Zone 4");
//        areaList.add("Zone 5");
//        areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
//        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        areaSpinner.setAdapter(areaAdapter);
//        areaSpinner.setOnItemSelectedListener(this);

        locationList.add("Amman");
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add("Rudniya Sawmill");
        locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        locationSpinner.setAdapter(locationAdapter);
//        locationSpinner.setOnItemSelectedListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private class JSONTask extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.214/woody/import.php?FLAG=5
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
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        NewRowInfo newRowInfo = new NewRowInfo();
                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
                        newRowInfo.setDate(finalObject.getString("DATE_OF_ACCEPTANCE"));
                        newRowInfo.setAcceptedPersonName(finalObject.getString("NAME_OF_ACCEPTER"));
                        newRowInfo.setLocationOfAcceptance(finalObject.getString("LOCATION_OF_ACCEPTANCE"));
                        newRowInfo.setTtnNo(finalObject.getString("TTN_NO"));
                        newRowInfo.setTotalRejectedNo(finalObject.getString("REJECTED"));

                        master.add(newRowInfo);
                    }
                } catch (JSONException e) {
                    Log.e("Import Data1", e.getMessage());
                }

                try {
                    JSONArray parentArray = parentObject.getJSONArray("RAW_INFO_DETAILS");
                    details.clear();
                    noOfPiecesSum = 0;
                    cubicSum = 0;
                    for (int i = 0; i < parentArray.length(); i++) {
                        JSONObject finalObject = parentArray.getJSONObject(i);

                        NewRowInfo newRowInfo = new NewRowInfo();
                        newRowInfo.setSupplierName(finalObject.getString("SUPLIER"));
                        newRowInfo.setTruckNo(finalObject.getString("TRUCK_NO"));
                        newRowInfo.setThickness(finalObject.getDouble("THICKNESS"));
                        newRowInfo.setWidth(finalObject.getDouble("WIDTH"));
                        newRowInfo.setLength(finalObject.getDouble("LENGTH"));
                        newRowInfo.setNoOfRejected(finalObject.getInt("REJECTED"));
                        newRowInfo.setNoOfBundles(finalObject.getDouble("NO_BUNDLES"));
                        newRowInfo.setGrade(finalObject.getString("GRADE"));
                        newRowInfo.setNoOfPieces(finalObject.getInt("PIECES"));
                        noOfPiecesSum += newRowInfo.getNoOfPieces();
                        cubicSum += (newRowInfo.getLength() * newRowInfo.getWidth() * newRowInfo.getThickness() * newRowInfo.getNoOfPieces());


//                        String pic = finalObject.getString("PART1") + finalObject.getString("PART2") +
//                                finalObject.getString("PART3") + finalObject.getString("PART4") +
//                                finalObject.getString("PART5") + finalObject.getString("PART6") +
//                                finalObject.getString("PART7") + finalObject.getString("PART8");
//
//                        pic = pic.replaceAll("null", "");
//
//                        newRowInfo.setPicture(pic);

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

            if (result != null) {
                Log.e("result", "*****************" + master.size());
                adapter = new AcceptanceInfoReportAdapter( details);
                listView.setAdapter(adapter);

                bundleNo.setText("" + details.size());
                cubic.setText("" + String.format("%.3f", (cubicSum / 1000000000)));
                noOfPieces.setText("" + String.format("%.3f", (noOfPiecesSum)));

            } else {
                Toast.makeText(AcceptanceInfoReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
