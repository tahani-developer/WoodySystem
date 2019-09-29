package com.falconssoft.woodysystem;

import android.os.Bundle;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;

public class Stage3 extends AppCompatActivity {

    CardView enterInventory ;
    CardView loadingOrder ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage3);

        enterInventory = (CardView) findViewById(R.id.enter_inventory);
        loadingOrder = (CardView) findViewById(R.id.loading_order);

        enterInventory.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Stage3.this , AddToInventory.class);
                startActivity(intent);
            }
        });

        loadingOrder.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Stage3.this , LoadingOrder.class);
                startActivity(intent);
            }
        });
    }
}
