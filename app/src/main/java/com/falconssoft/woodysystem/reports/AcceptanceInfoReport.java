package com.falconssoft.woodysystem.reports;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;

import java.util.ArrayList;
import java.util.List;

public class AcceptanceInfoReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner suppliersSpinner, gradeSpinner;
    private List<String> suppliersList, locationList, gradeList;
    private ArrayAdapter<String> suppliersAdapter, locationAdapter, gradeAdapter;
    private EditText truckNo, acceptor, ttnNo, fromThickness, toThickness, fromWidth, toWidth, fromLength, toLength;
    private TextView fromDate, toDate, bundleNo, noOfPieces, cubic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acceptance_info_report);

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
}
