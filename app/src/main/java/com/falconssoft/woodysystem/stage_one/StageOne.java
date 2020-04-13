package com.falconssoft.woodysystem.stage_one;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.AddToInventory;
import com.falconssoft.woodysystem.LoadingOrder;
import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.ReportsActivity;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;

public class StageOne extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout addRaw, acceptInfo, generateBarcode, reports;
    private Animation animation;
    private Dialog passwordDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_one);

        passwordDialog = new Dialog(this);

        addRaw = findViewById(R.id.stage1_new_raw);
        acceptInfo = findViewById(R.id.stage1_accept_info);
        generateBarcode = findViewById(R.id.stage1_generate_barcode);
        reports = findViewById(R.id.stage1_reports);

        addRaw.setOnClickListener(this);
        acceptInfo.setOnClickListener(this);
        generateBarcode.setOnClickListener(this);
        reports.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        addRaw.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        acceptInfo.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        generateBarcode.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        reports.setAnimation(animation);

        showPasswordDialog();

    }

    void showPasswordDialog() {
//        passwordDialog = new Dialog(this);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.setContentView(R.layout.password_dialog);
        passwordDialog.setCancelable(false);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);
        TextView cancel = passwordDialog.findViewById(R.id.password_dialog_cancel);
        cancel.setVisibility(View.VISIBLE);

        done.setText(getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("301190"))
                    passwordDialog.dismiss();
                else
                    Toast.makeText(StageOne.this, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StageOne.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
        passwordDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage1_new_raw:
                Intent intent = new Intent(this, AddNewRaw.class);
                startActivity(intent);
//                setSlideAnimation();
                break;
            case R.id.stage1_accept_info:
                Intent intent2 = new Intent(this, AcceptRow.class);
                startActivity(intent2);
                break;
            case R.id.stage1_generate_barcode:
                Intent intent3 = new Intent(this, GenerateBarCode.class);
                startActivity(intent3);
                break;
            case R.id.stage1_reports:
                Intent intent4 = new Intent(this, AcceptanceInfoReport.class);//ReportsStageOne
                startActivity(intent4);
                break;
        }
    }


}