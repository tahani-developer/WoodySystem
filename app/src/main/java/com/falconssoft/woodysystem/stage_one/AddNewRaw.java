package com.falconssoft.woodysystem.stage_one;

import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;

public class AddNewRaw extends AppCompatActivity implements View.OnClickListener {

    private TextView addNewSupplier, searchSupplier;

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
                Dialog addSupplierDialog = new Dialog(this);
                addSupplierDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                addSupplierDialog.setContentView(R.layout.add_supplier_dialog);
                addSupplierDialog.show();

                break;
            case R.id.addNewRaw_search_supplier:
                break;
        }

    }
}
