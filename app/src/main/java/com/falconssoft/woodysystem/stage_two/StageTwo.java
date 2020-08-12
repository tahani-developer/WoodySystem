package com.falconssoft.woodysystem.stage_two;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.stage_one.AddNewRaw;
import com.falconssoft.woodysystem.stage_one.GenerateBarCode;

public class StageTwo extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout plannedPackingLis, reports;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_two);

        plannedPackingLis = findViewById(R.id.stage2_planned_packingList);
        reports = findViewById(R.id.stage2_reports);

        plannedPackingLis.setOnClickListener(this);
        reports.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        plannedPackingLis.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        reports.setAnimation(animation);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage2_planned_packingList:
                Intent intent = new Intent(this, PlannedPackingList.class);
                startActivity(intent);
//                setSlideAnimation();
                break;
            case R.id.stage2_reports: // report1
                Intent intent2 = new Intent(this, StageTwoReports.class);
                startActivity(intent2);
                break;


        }
    }

}