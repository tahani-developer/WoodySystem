package com.falconssoft.woodysystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.falconssoft.woodysystem.reports.BundlesReport;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bundlesReport, ordersReport, inventoryReport;
    private ScaleAnimation scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        bundlesReport = findViewById(R.id.reports_bundles);
        ordersReport = findViewById(R.id.reports_orders);
        inventoryReport = findViewById(R.id.reports_inventory);

        bundlesReport.setOnClickListener(this);
        ordersReport.setOnClickListener(this);
        inventoryReport.setOnClickListener(this);

        callAnimation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reports_bundles:
                Intent intent=new Intent(ReportsActivity.this, BundlesReport.class);
                startActivity(intent);
//                setSlideAnimation();
                break;
            case R.id.reports_orders:
                Intent intent2=new Intent(ReportsActivity.this, LoadingOrderReport.class);
                startActivity(intent2);
//                setSlideAnimation();
                break;
            case R.id.reports_inventory:
                Intent intent3=new Intent(ReportsActivity.this, InventoryReport.class);
                startActivity(intent3);
//                setSlideAnimation();
                break;
        }

    }

    public void callAnimation(){
        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.INFINITE, .8f, ScaleAnimation.RELATIVE_TO_SELF, .8f);
        scale.setStartOffset(400);
        scale.setDuration(400);
        scale.setInterpolator(new OvershootInterpolator());
        bundlesReport.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(400);
        scale.setDuration(500);
        scale.setInterpolator(new OvershootInterpolator());
        ordersReport.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(400);
        scale.setDuration(600);
        scale.setInterpolator(new OvershootInterpolator());
        inventoryReport.startAnimation(scale);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
//        Intent intent = new Intent(ReportsActivity.this , Stage3.class);
//        startActivity(intent);
    }

//    public void setSlideAnimation() {
//        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
//    }

//    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(ReportsActivity.this, Stage3.class);
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
