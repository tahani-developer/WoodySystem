package com.falconssoft.woodysystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RelativeLayout relativeLayout;
    private TextView stage1, stage2 ,stage3 ;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
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

//        stage1.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages));
//                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages1));
//                }
//                return false;
//            }
//        });
//
//        stage2.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages));
//                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages2));
//                }
//                return false;
//            }
//        });
//
//        stage3.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View view, MotionEvent event) {
//                if(event.getAction() == MotionEvent.ACTION_UP) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages));
//                } else if(event.getAction() == MotionEvent.ACTION_DOWN) {
//                    relativeLayout.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.stages3));
//                }
//                return false;
//            }
//        });
    }
}
