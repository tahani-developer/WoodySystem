package com.falconssoft.woodysystem.stage_one;

import android.app.Dialog;
import android.content.Intent;
import android.opengl.ETC1;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.ArrayList;
import java.util.List;

public class AddNewRaw extends AppCompatActivity implements View.OnClickListener {

    private TextView addNewSupplier, searchSupplier;
    private EditText thickness, width, length, noOfPieces, noOfBundles ;
    private List<SupplierInfo> supplierInfoList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_raw);

        addNewSupplier = findViewById(R.id.addNewRaw_add_supplier);
        searchSupplier = findViewById(R.id.addNewRaw_search_supplier);

        addNewSupplier.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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
}
