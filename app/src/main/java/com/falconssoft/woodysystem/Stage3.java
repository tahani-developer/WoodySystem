package com.falconssoft.woodysystem;

import android.os.Bundle;
import android.content.Intent;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class Stage3 extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout enterInventory, loadingOrder;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage3);

        enterInventory = findViewById(R.id.stage3_enter_inventory);
        loadingOrder = findViewById(R.id.stage3_loading_order);

        enterInventory.setOnClickListener(this);
        loadingOrder.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        enterInventory.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        loadingOrder.setAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage3_enter_inventory:
                Intent intent = new Intent(this, AddToInventory.class);
                startActivity(intent);
                setSlideAnimation();
                break;
            case R.id.stage3_loading_order:
                Intent intent2 = new Intent(this, LoadingOrder.class);
                startActivity(intent2);
                setSlideAnimation();
                break;
        }
    }

    public void setSlideAnimation() {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Stage3.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        setSlideAnimation();
    }

}
