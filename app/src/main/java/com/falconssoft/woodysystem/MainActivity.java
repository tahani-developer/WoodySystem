package com.falconssoft.woodysystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    TextView stage1 ;
    TextView stage2 ;
    TextView stage3 ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stage1 = (TextView) findViewById(R.id.s1);
        stage2 = (TextView) findViewById(R.id.s2);
        stage3 = (TextView) findViewById(R.id.s3);

        stage1.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this , Stage3.class);
//                startActivity(intent);
            }
        });

        stage2.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this , Stage3.class);
//                startActivity(intent);
            }
        });

        stage3.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this , Stage3.class);
                startActivity(intent);
            }
        });
    }
}
