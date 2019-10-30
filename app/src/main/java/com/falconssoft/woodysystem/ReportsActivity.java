package com.falconssoft.woodysystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.TextView;

import com.falconssoft.woodysystem.reports.BundlesReport;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bundlesReport, ordersReport, report1,report2,report3,report4;
    private ScaleAnimation scale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        bundlesReport = findViewById(R.id.reports_bundles);
        ordersReport = findViewById(R.id.reports_orders);
        report1 = findViewById(R.id.reports_textView9);
        report2 = findViewById(R.id.reports_textView10);
        report3 = findViewById(R.id.reports_textView11);
        report4 = findViewById(R.id.reports_textView12);

        bundlesReport.setOnClickListener(this);
        ordersReport.setOnClickListener(this);

        callAnimation();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reports_bundles:
                Intent intent=new Intent(ReportsActivity.this, BundlesReport.class);
                startActivity(intent);
                setSlideAnimation();
                break;
            case R.id.reports_orders:
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
        report1.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(400);
        scale.setDuration(700);
        scale.setInterpolator(new OvershootInterpolator());
        report2.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(400);
        scale.setDuration(800);
        scale.setInterpolator(new OvershootInterpolator());
        report3.startAnimation(scale);

        scale = new ScaleAnimation(0, 1, 0, 1, ScaleAnimation.RELATIVE_TO_SELF, .5f, ScaleAnimation.RELATIVE_TO_SELF, .5f);
        scale.setStartOffset(400);
        scale.setDuration(900);
        scale.setInterpolator(new OvershootInterpolator());
        report4.startAnimation(scale);

    }

    public void setSlideAnimation() {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(ReportsActivity.this, Stage3.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        setSlideAnimation();
    }

}
