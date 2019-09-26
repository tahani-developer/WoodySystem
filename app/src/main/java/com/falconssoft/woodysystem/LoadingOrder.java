package com.falconssoft.woodysystem;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;

public class LoadingOrder extends AppCompatActivity {


    GridView items;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage3);


        items = (GridView) findViewById(R.id.items);

//        itemsListAdapter adapter = new itemsListAdapter(LoadingOrder.this, customerList);
//        items.setAdapter(adapter);
    }
}
