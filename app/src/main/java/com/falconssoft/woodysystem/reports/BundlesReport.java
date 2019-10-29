package com.falconssoft.woodysystem.reports;

import android.content.Intent;
import android.net.sip.SipSession;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.ReportsActivity;
import com.falconssoft.woodysystem.Stage3;
import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class BundlesReport extends AppCompatActivity {

    private TableLayout bundlesTable;
    private DatabaseHandler databaseHandler;
    private List<BundleInfo> bundleInfos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundles_report);

        bundlesTable = findViewById(R.id.addToInventory_table);
        databaseHandler = new DatabaseHandler(this);
        fillTable();

    }

    void fillTable() {
        bundleInfos = databaseHandler.getBundleInfo();
//        TableRow tableRowBasic = new TableRow(this);
//        tableRowBasic = fillTableRows(tableRowBasic
//                , "Bundle#"
//                , "Length"
//                , "Width"
//                , "Thick"
//                , "Grade"
//                , "#Pieces"
//                , "Location"
//                , "Area");
//        bundlesTable.addView(tableRowBasic);

        for (int m = 0; m < bundleInfos.size(); m++) {
            TableRow tableRow = new TableRow(this);
            tableRow = fillTableRows(tableRow
                    , bundleInfos.get(m).getBundleNo()
                    , "" + bundleInfos.get(m).getLength()
                    , "" + bundleInfos.get(m).getWidth()
                    , "" + bundleInfos.get(m).getThickness()
                    , bundleInfos.get(m).getGrade()
                    , "" + bundleInfos.get(m).getNoOfPieces()
                    , bundleInfos.get(m).getLocation()
                    , bundleInfos.get(m).getArea());
            bundlesTable.addView(tableRow);

            tableRow.getVirtualChildAt(8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(BundlesReport.this, "tested", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

    TableRow fillTableRows(TableRow tableRow, String bundlNo, String length, String width, String thic, String grade, String noOfPieces, String location, String area) {
        for (int i = 0; i < 9; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.light_orange);
            TableRow.LayoutParams textViewParam;
//            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
//            textView.setLayoutParams(textViewParam);
            switch (i) {
                case 0:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(bundlNo);
                    break;
                case 1:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(length);
                    break;
                case 2:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(width);
                    break;
                case 3:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(thic);
                    break;
                case 4:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(grade);
                    break;
                case 5:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(noOfPieces);
                    break;
                case 6:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1.5f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(location);
                    break;
                case 7:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText(area);
                    break;
                case 8:
                    textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f);
                    textViewParam.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(textViewParam);
                    textView.setText("Print");
                    textView.setTextColor(ContextCompat.getColor(this, R.color.white));
                    textView.setBackgroundResource(R.color.orange);
                    break;
            }
            tableRow.addView(textView);
        }
        return tableRow;
    }

    public void setSlideAnimation() {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BundlesReport.this, ReportsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        setSlideAnimation();
    }

}
