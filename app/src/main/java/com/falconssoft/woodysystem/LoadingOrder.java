package com.falconssoft.woodysystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;

import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class LoadingOrder extends AppCompatActivity {


    GridView items;
    Button done;

    DatabaseHandler DHandler ;
    List<BundleInfo> bundles ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order);


        items = (GridView) findViewById(R.id.items);

        done = (Button) findViewById(R.id.done);


        DHandler = new DatabaseHandler(LoadingOrder.this);
        bundles = DHandler.getBundleInfo();

        ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
        items.setAdapter(adapter);

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoadingOrder.this , LoadingOrder2.class);
                startActivity(intent);
            }
        });
    }
}
