package com.falconssoft.woodysystem.stage_two;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;

public class StageTwoReports extends AppCompatActivity implements View.OnClickListener {

    private TextView unloadPackingLis, loadedPackingLis, plannedUnplannedList;
    private Animation animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_two_reports);

        unloadPackingLis = findViewById(R.id.stage2_unload_packingList);
        loadedPackingLis = findViewById(R.id.stage2_loaded_packingList);
        plannedUnplannedList = findViewById(R.id.stage2_planned_unplanned_packingList);

        unloadPackingLis.setOnClickListener(this);
        loadedPackingLis.setOnClickListener(this);
        plannedUnplannedList.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        unloadPackingLis.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        loadedPackingLis.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        plannedUnplannedList.setAnimation(animation);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage2_unload_packingList: // report1
                Intent intent2 = new Intent(this, UnloadPackingList.class);
                startActivity(intent2);
                break;
            case R.id.stage2_loaded_packingList: // report2
                Intent intent3 = new Intent(this, LoadPackingList.class);
                startActivity(intent3);
                break;
            case R.id.stage2_planned_unplanned_packingList: // report3
                Intent intent4 = new Intent(this, PlannedUnplanned.class);
                startActivity(intent4);
                break;

        }
    }
}