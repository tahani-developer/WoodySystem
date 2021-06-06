package com.falconssoft.woodysystem;

import android.os.Bundle;
import android.content.Intent;

import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stage3 extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout enterInventory, loadingOrder,  ordersReport, inventoryReport,reports,transferInventory;
    private Animation animation;
    private WoodPresenter presenter;
    private DatabaseHandler databaseHandler;
//    private List<String> list = new ArrayList<>();
    private Settings settings;
    private long mLastClickTime = 0,mLastClickTime2 = 0, mLastClickTime3 = 0;
    public static int flagOpenJ=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stage3);

//        presenter = new WoodPresenter(this);
//        databaseHandler = new DatabaseHandler(this);
        enterInventory = findViewById(R.id.stage3_enter_inventory);
        loadingOrder = findViewById(R.id.stage3_loading_order);
        reports = findViewById(R.id.stage3_reports);
        ordersReport = findViewById(R.id.reports_orders);
        inventoryReport = findViewById(R.id.reports_inventory);
        transferInventory=findViewById(R.id.transfer_inventory);

        ordersReport.setOnClickListener(this);
        inventoryReport.setOnClickListener(this);

        enterInventory.setOnClickListener(this);
        loadingOrder.setOnClickListener(this);
        reports.setOnClickListener(this);
        transferInventory.setOnClickListener(this);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        enterInventory.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        loadingOrder.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        ordersReport.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        inventoryReport.setAnimation(animation);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        transferInventory.setAnimation(animation);

//        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
//        reports.setAnimation(animation);

//        list = databaseHandler.getBundleNo();
//        for (int i =0; i< list.size();i++){
//            String serialBefore = list.get(i);
//            String serial = serialBefore.substring(serialBefore.lastIndexOf(".") + 1);
////            Log.e("stage 3 ", serial);
//            databaseHandler.updateBundlesSerial(list.get(i), serial);
//        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stage3_enter_inventory:
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    break;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(this, AddToInventory.class);
                intent.putExtra("flag" , "0");
                startActivity(intent);
                break;
            case R.id.stage3_loading_order:
                if (SystemClock.elapsedRealtime() - mLastClickTime2 < 1000) {
                    break;
                }
                flagOpenJ=0;
                mLastClickTime2 = SystemClock.elapsedRealtime();
                Intent intent2 = new Intent(this, LoadingOrder.class);
                startActivity(intent2);
                break;
            case R.id.stage3_reports:
                if (SystemClock.elapsedRealtime() - mLastClickTime3 < 1000) {
                    break;
                }
                mLastClickTime3 = SystemClock.elapsedRealtime();
                Intent intent3 = new Intent(this, ReportsActivity.class);
                startActivity(intent3);
                break;
            case R.id.reports_orders:
                Intent intent22=new Intent(this, LoadingOrderReport.class);
                startActivity(intent22);
//                setSlideAnimation();
                break;
            case R.id.reports_inventory:
                Intent intent33=new Intent(this, InventoryReport.class);
                startActivity(intent33);
//                setSlideAnimation();
                break;

            case R.id.transfer_inventory:
                Intent intent34=new Intent(this, TransferBundle.class);
                startActivity(intent34);
//                setSlideAnimation();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


}
