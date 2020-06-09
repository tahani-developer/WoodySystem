package com.falconssoft.woodysystem;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.falconssoft.woodysystem.models.Orders;

import java.util.List;

import static com.falconssoft.woodysystem.reports.LoadingOrderReport.EDIT_BUNDLES;
import static com.falconssoft.woodysystem.reports.LoadingOrderReport.EDIT_LOADING_ORDER_FLAG;
import static com.falconssoft.woodysystem.reports.LoadingOrderReport.EDIT_ORDER;

public class EditLoadingReport extends AppCompatActivity {

    private EditText orderNo, truckNo, containerNo, destenation;
    private ListView listView;
    private EditLoadingAdapter adapter;
    private Orders orders;
    private List<Orders>  bundles;
    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_loading_report);

        orderNo = findViewById(R.id.editLoadingOrder_orderNo);
        truckNo = findViewById(R.id.editLoadingOrder_truckNo);
        containerNo = findViewById(R.id.editLoadingOrder_containerNo);
        destenation = findViewById(R.id.editLoadingOrder_destination);
        listView = findViewById(R.id.editLoadingOrder_listview);

        flag = getIntent().getIntExtra(EDIT_LOADING_ORDER_FLAG, 20);
        orders = (Orders) getIntent().getSerializableExtra(EDIT_ORDER);
        bundles = (List<Orders>) getIntent().getSerializableExtra(EDIT_BUNDLES);

        Log.e("edit", "" + orders.getPlacingNo()+ "######" + bundles.size());


    }
}
