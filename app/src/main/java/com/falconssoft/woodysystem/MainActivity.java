package com.falconssoft.woodysystem;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.falconssoft.woodysystem.stage_one.StageOne;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private RelativeLayout relativeLayout;
    private TextView stage1, stage2, stage3;
    private ScaleAnimation scale;
    private ImageView pictureOne, pictureTwo, pictureThree;
//    private Animation animation;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        relativeLayout = (RelativeLayout) findViewById(R.id.relative);
        stage1 = (TextView) findViewById(R.id.s1);
        stage2 = (TextView) findViewById(R.id.s2);
        stage3 = (TextView) findViewById(R.id.s3);

       callAnimation();

        stage1.setOnClickListener(this);
        stage2.setOnClickListener(this);
        stage3.setOnClickListener(this);

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

    public void callAnimation(){
        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.INFINITE, .8f, ScaleAnimation.RELATIVE_TO_SELF, .8f);
        scale.setStartOffset(300);
        scale.setDuration(700);
        scale.setInterpolator(new OvershootInterpolator());
        stage1.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(300);
        scale.setDuration(600);
        scale.setInterpolator(new OvershootInterpolator());
        stage2.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RESTART, .7f, ScaleAnimation.RELATIVE_TO_SELF, .7f);
        scale.setStartOffset(300);
        scale.setDuration(800);
        scale.setInterpolator(new OvershootInterpolator());
        stage3.startAnimation(scale);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.s1:
                Intent intent1 = new Intent(MainActivity.this , StageOne.class);
                startActivity(intent1);
                break;
            case R.id.s2:
//                Intent intent = new Intent(MainActivity.this , Stage3.class);
//                startActivity(intent);
                break;
            case R.id.s3:
                Intent intent2 = new Intent(MainActivity.this, Stage3.class);
                startActivity(intent2);
//                setSlideAnimation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
       finish();
    }

    //    public void setSlideAnimation() {
//        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
//    }

//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
//        startActivity(intent);
//        setSlideAnimation();
//        finish();
//    }

//    @Override
//    public void finish() {
//        super.finish();
//        setSlideAnimation();
//    }

}
