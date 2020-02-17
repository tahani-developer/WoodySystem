package com.falconssoft.woodysystem.stage_one;

import android.app.Dialog;
import android.content.Intent;
import android.opengl.ETC1;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.ArrayList;
import java.util.List;

public class AddNewRaw extends AppCompatActivity implements View.OnClickListener {

    private TextView addNewSupplier, searchSupplier, addButton, acceptRowButton;
    private EditText thickness, width, length, noOfPieces, noOfBundles, noOfRejected;
    private Spinner gradeSpinner;
    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter;
    private List<SupplierInfo> supplierInfoList = new ArrayList<>();
    private String gradeText = "Fresh", supplierName = "";
    private LinearLayout headerLayout, acceptRowLayout;
    private Button doneAcceptRow, backAcceptRow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_raw);

        addNewSupplier = findViewById(R.id.addNewRaw_add_supplier);
        searchSupplier = findViewById(R.id.addNewRaw_search_supplier);
        thickness = findViewById(R.id.addNewRaw_thickness);
        width = findViewById(R.id.addNewRaw_width);
        length = findViewById(R.id.addNewRaw_length);
        noOfPieces = findViewById(R.id.addNewRaw_no_of_pieces);
        noOfBundles = findViewById(R.id.addNewRaw_no_of_bundles);
        noOfRejected = findViewById(R.id.addNewRaw_no_of_rejected);
        gradeSpinner = findViewById(R.id.addNewRaw_grade);
        addButton = findViewById(R.id.addNewRaw_add_button);
        acceptRowButton = findViewById(R.id.addNewRaw_acceptRaw_button);
        headerLayout = findViewById(R.id.addNewRaw_linearLayoutHeader);
        acceptRowLayout = findViewById(R.id.addNewRaw_acceptRow_linear);
        doneAcceptRow = findViewById(R.id.addNewRaw_acceptRow_done);
        backAcceptRow = findViewById(R.id.addNewRaw_acceptRow_back);

        headerLayout.setVisibility(View.VISIBLE);
        acceptRowLayout.setVisibility(View.GONE);

        gradeList.add("Fresh");
        gradeList.add("KD");
        gradeList.add("Blue Stain");
        gradeList.add("KD Blue Stain");
        gradeList.add("AST");
        gradeList.add("AST Blue Stain");
        gradeList.add("Second Sort");
        gradeList.add("Reject");
        gradeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
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

        backAcceptRow.setOnClickListener(this);
        doneAcceptRow.setOnClickListener(this);
        acceptRowButton.setOnClickListener(this);
        addNewSupplier.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);
        addButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case
                R.id.addNewRaw_acceptRow_back:
                headerLayout.setVisibility(View.VISIBLE);
                acceptRowLayout.setVisibility(View.GONE);
                break;
            case R.id.addNewRaw_acceptRow_done:
                break;
            case R.id.addNewRaw_acceptRaw_button:
                headerLayout.setVisibility(View.GONE);
                acceptRowLayout.setVisibility(View.VISIBLE);
                break;
            case R.id.addNewRaw_add_button:
                String thicknessLocal = thickness.getText().toString();
                String widthLocal = width.getText().toString();
                String lengthLocal = length.getText().toString();
                String noOfPiecesLocal = noOfPieces.getText().toString();
                String rejectedPiecesLocal = noOfRejected.getText().toString();
                String bundleNoLocal = noOfBundles.getText().toString();

                if (!TextUtils.isEmpty(supplierName)) {
                    if (!TextUtils.isEmpty(thicknessLocal))
                        if (!TextUtils.isEmpty(widthLocal))
                            if (!TextUtils.isEmpty(lengthLocal))
                                if (!TextUtils.isEmpty(noOfPiecesLocal))
                                    if (!TextUtils.isEmpty(rejectedPiecesLocal))
                                        if (!TextUtils.isEmpty(bundleNoLocal)) {

                                        } else {
                                            noOfBundles.setError("Required!");
                                        }
                                    else {
                                        noOfRejected.setError("Required!");
                                    }
                                else {
                                    noOfPieces.setError("Required!");
                                }
                            else {
                                length.setError("Required!");
                            }
                        else {
                            width.setError("Required!");
                        }
                    else {
                        thickness.setError("Required!");
                    }
                }else {
                    searchSupplier.setError("Please Select First!");
                }

                break;
            case R.id.addNewRaw_add_supplier:
                Intent intent = new Intent(AddNewRaw.this, AddNewSupplier.class);
                startActivity(intent);
                break;
            case R.id.addNewRaw_search_supplier:
                Dialog dialog = new Dialog(this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.search_supplier_dialog);

                SearchView searchView = dialog.findViewById(R.id.search_supplier_searchView);
                RecyclerView recyclerView = dialog.findViewById(R.id.search_supplier_recyclerView);
                SuppliersAdapter adapter = new SuppliersAdapter(this, supplierInfoList);
                recyclerView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        adapter.filter(newText);
                        return false;
                    }
                });
                dialog.show();
                break;
        }

    }

    void chooseSpinnersContent() {
        String gradeString;
        switch (gradeText) {
            case "Fresh":
                gradeString = "FR";
                break;
            case "Blue Stain":
                gradeString = "BS";
                break;
            case "Reject":
                gradeString = "RJ";
                break;
            case "KD":
                gradeString = "KD";
                break;
            case "KD Blue Stain":
                gradeString = "KDBS";
                break;
            case "Second Sort":
                gradeString = "SS";
                break;
            case "AST":
                gradeString = "AST";
                break;
            case "AST Blue Stain":
                gradeString = "ASTBS";
                break;
        }

    }

}
