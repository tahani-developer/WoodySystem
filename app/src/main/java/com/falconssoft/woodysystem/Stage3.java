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

    private LinearLayout enterInventory, loadingOrder, reports;
    private Animation animation;
//    private WoodPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage3);

//        presenter = new WoodPresenter(this);
        enterInventory = findViewById(R.id.stage3_enter_inventory);
        loadingOrder = findViewById(R.id.stage3_loading_order);
        reports = findViewById(R.id.stage3_reports);

        enterInventory.setOnClickListener(this);
        loadingOrder.setOnClickListener(this);
        reports.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        enterInventory.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        loadingOrder.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        reports.setAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage3_enter_inventory:
//                presenter.getImportData();
                Intent intent = new Intent(this, AddToInventory.class);
                startActivity(intent);
                setSlideAnimation();
                break;
            case R.id.stage3_loading_order:
                Intent intent2 = new Intent(this, LoadingOrder.class);
                startActivity(intent2);
                setSlideAnimation();
                break;
            case R.id.stage3_reports:
                Intent intent3 = new Intent(this, ReportsActivity.class);
                startActivity(intent3);
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
