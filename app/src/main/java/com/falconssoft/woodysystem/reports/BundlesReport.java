package com.falconssoft.woodysystem.reports;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.sip.SipSession;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.ReportsActivity;
import com.falconssoft.woodysystem.SettingsFile;
import com.falconssoft.woodysystem.Stage3;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class BundlesReport extends AppCompatActivity {

    private TableLayout bundlesTable;
    private DatabaseHandler databaseHandler;
    private List<BundleInfo> bundleInfos = new ArrayList<>();
    private Animation animation;
    private TextView textView;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bundles_report);

        textView = findViewById(R.id.loading_order_report_tv);
        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        bundlesTable = findViewById(R.id.addToInventory_table);
        databaseHandler = new DatabaseHandler(this);
        fillTable();

    }

    void fillTable() {
        bundleInfos = databaseHandler.getAllBundleInfo(SettingsFile.store);
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

            TableRow finalTableRow = tableRow;
            tableRow.getVirtualChildAt(8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintHelper photoPrinter = new PrintHelper(BundlesReport.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    TextView text=(TextView) finalTableRow.getChildAt(0);
                    Bitmap bitmap=writeBarcode(text.getText().toString());
                    photoPrinter.printBitmap("invoice.jpg", bitmap);
                    Toast.makeText(BundlesReport.this, "tested+"+text.getText().toString(), Toast.LENGTH_SHORT).show();
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

    public Bitmap writeBarcode(String data) {
        final Dialog dialog = new Dialog(BundlesReport.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_dialog);
        TextView close = (TextView) dialog.findViewById(R.id.close);
        ImageView iv = (ImageView) dialog.findViewById(R.id.iv);
        // barcode data
        String barcode_data = data;



        Bitmap bitmap = null;//  AZTEC -->QR

        try {
            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 1100, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }


//        dialog.show();

        return bitmap;

    }

    Bitmap encodeAsBitmap(String contents, BarcodeFormat format, int img_width, int img_height) throws WriterException {
        String contentsToEncode = contents;
        if (contentsToEncode == null) {
            return null;
        }
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix result;
        try {
            result = writer.encode(contentsToEncode, format, img_width, img_height, hints);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int width = result.getWidth();
        int height = result.getHeight();
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; y++) {
            int offset = y * width;
            for (int x = 0; x < width; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(width, height,
                Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }


}
