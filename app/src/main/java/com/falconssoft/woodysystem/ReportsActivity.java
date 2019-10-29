package com.falconssoft.woodysystem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.falconssoft.woodysystem.reports.BundlesReport;

public class ReportsActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView bundlesReport, ordersReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        bundlesReport = findViewById(R.id.reports_bundles);
        ordersReport = findViewById(R.id.reports_orders);

        bundlesReport.setOnClickListener(this);
        ordersReport.setOnClickListener(this);

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
