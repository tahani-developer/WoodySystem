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

import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.AcceptanceSupplierReport;

public class StageOne extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout addRaw,  generateBarcode, report2, report1,reportSupplier;//acceptInfo
    private Animation animation;
    private Dialog passwordDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_one);

        addRaw = findViewById(R.id.stage1_new_raw);
//        acceptInfo = findViewById(R.id.stage1_accept_info);
        report1 = findViewById(R.id.stage1_accept_info);
//        generateBarcode = findViewById(R.id.stage1_generate_barcode);
        report2 = findViewById(R.id.stage1_reports);
        reportSupplier= findViewById(R.id.stage1_accept_supplier_info);

        addRaw.setOnClickListener(this);
        report1.setOnClickListener(this);
//        generateBarcode.setOnClickListener(this);
        report2.setOnClickListener(this);
        reportSupplier.setOnClickListener(this);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        addRaw.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        report1.setAnimation(animation);

//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//        generateBarcode.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        report2.setAnimation(animation);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        reportSupplier.setAnimation(animation);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage1_new_raw:

                showPasswordDialog();
//                Intent intent = new Intent(this, AddNewRaw.class);
//                startActivity(intent);
//                setSlideAnimation();
                break;
            case R.id.stage1_accept_info: // report1
                Intent intent2 = new Intent(this, AcceptanceReport.class);//AcceptRow
                intent2.putExtra("supplier","0");
                startActivity(intent2);
                break;
            case  R.id.stage1_accept_supplier_info:
                Intent intent5 = new Intent(this, AcceptanceSupplierReport.class);//AcceptRow
              //  intent5.putExtra("supplier","1");
                startActivity(intent5);
                break;
            case R.id.stage1_generate_barcode:
                Intent intent3 = new Intent(this, GenerateBarCode.class);
                startActivity(intent3);
                break;
            case R.id.stage1_reports: //report 2
                Intent intent4 = new Intent(this, AcceptanceInfoReport.class);//ReportsStageOne
                startActivity(intent4);
                break;
        }
    }

    void showPasswordDialog() {
        passwordDialog = new Dialog(this);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("3030100")) {
                    passwordDialog.dismiss();
                    Intent intent = new Intent(StageOne.this, AddNewRaw.class);
                    startActivity(intent);
                } else
                    Toast.makeText(StageOne.this, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }



}