package com.falconssoft.woodysystem.stage_one;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;

public class ReportsStageOne extends AppCompatActivity {

    private TextView reportOne, reportTwo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports_stage_one);

        reportOne = findViewById(R.id.reportOne);
        reportTwo = findViewById(R.id.reportTwo);

        reportOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent4 = new Intent(ReportsStageOne.this, AcceptanceInfoReport.class);
                startActivity(intent4);

            }
        });

        reportTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportsStageOne.this, AcceptanceReport.class);
                startActivity(intent);
            }
        });

    }
}
