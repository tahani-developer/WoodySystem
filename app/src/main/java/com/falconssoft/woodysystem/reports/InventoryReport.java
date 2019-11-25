package com.falconssoft.woodysystem.reports;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.LoadingOrderReport;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.ReportsActivity;
import com.falconssoft.woodysystem.SettingsFile;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.EnumMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class InventoryReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private TableLayout bundlesTable;
    private DatabaseHandler databaseHandler;
    public static List<BundleInfo> bundleInfoServer = new ArrayList<>();
    private List<String> locationList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private WoodPresenter woodPresenter;
    private Animation animation;
    private TextView textView, noOfBundles, noOfPieces;
    private EditText dateFrom, dateTo;
    private Spinner location, area;
    private ArrayAdapter<String> locationAdapter;
    private ArrayAdapter<String> areaAdapter;
    private String loc = "All", areaField = "All";
    private Settings generalSettings;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        databaseHandler = new DatabaseHandler(this);
        woodPresenter = new WoodPresenter(this);
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        calendar = Calendar.getInstance();

        bundlesTable = findViewById(R.id.inventory_report_table);
        location = findViewById(R.id.inventory_report_location);
        area = findViewById(R.id.inventory_report_area);
        textView = findViewById(R.id.inventory_report_tv);
        dateFrom = findViewById(R.id.inventory_report_from);
        dateTo = findViewById(R.id.inventory_report_to);
        noOfBundles = findViewById(R.id.inventory_report_no_bundles);
        noOfPieces = findViewById(R.id.inventory_report_no_pieces);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

        locationList.add("All");
        locationList.add("Amman");
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add("Rudniya Sawmill");
        locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(locationAdapter);

        areaList.add("All");
        areaList.add("Zone 1");
        areaList.add("Zone 2");
        areaList.add("Zone 3");
        areaAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        area.setAdapter(areaAdapter);

        woodPresenter.getBundlesData(this);

        dateFrom.setOnClickListener(this);
        dateTo.setOnClickListener(this);
        location.setOnItemSelectedListener(this);
        area.setOnItemSelectedListener(this);
        fillTable(bundleInfoServer);
    }

    public void filters() {
        String fromDate = dateFrom.getText().toString().trim();
        String toDate = dateTo.getText().toString();
        List<BundleInfo> filtered = new ArrayList<>();
        List<BundleInfo> dateFiltered = new ArrayList<>();

        for (int k = 0; k < bundleInfoServer.size(); k++) {
//            Log.e("-------------------","" +((!loc.equals("All")) && areaField.equals("All")
//                    && loc.equals(bundleInfoServer.get(k).getLocation())));
            Log.e("location", bundleInfoServer.get(k).getLocation());
            if ((!loc.equals("All")) && (!areaField.equals("All"))
                    && loc.equals(bundleInfoServer.get(k).getLocation())
                    && areaField.equals(bundleInfoServer.get(k).getArea()))
                filtered.add(bundleInfoServer.get(k));
            else if ((!loc.equals("All")) && areaField.equals("All")
                    && loc.equals(bundleInfoServer.get(k).getLocation())) {
                filtered.add(bundleInfoServer.get(k));
            } else if (loc.equals("All") && (!areaField.equals("All"))
                    && areaField.equals(bundleInfoServer.get(k).getArea()))
                filtered.add(bundleInfoServer.get(k));
            else if (loc.equals("All") && areaField.equals("All"))
                filtered.add(bundleInfoServer.get(k));

        }

        if (!(fromDate.equals("") && toDate.equals(""))) {
            for (int m = 0; m < filtered.size(); m++) {
                Log.e("filterdate", "" + filtered.get(m).getDateOfLoad());
                if ((formatDate(filtered.get(m).getDateOfLoad()).after(formatDate(fromDate))
                        || formatDate(filtered.get(m).getDateOfLoad()).equals(formatDate(fromDate)))
                        && (formatDate(filtered.get(m).getDateOfLoad()).before(formatDate(toDate))
                        || formatDate(filtered.get(m).getDateOfLoad()).equals(formatDate(toDate)))) {
                    dateFiltered.add(filtered.get(m));
                }
            }
            fillTable(dateFiltered);

        } else {
            fillTable(filtered);
        }
//        fillTable(filtered);

    }

    public void fillTable(List<BundleInfo> filteredList) {
//        bundleInfos = databaseHandler.getAllBundleInfo("0");
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
        bundlesTable.removeAllViews();
        TableRow tableRow;
        int sumOfBundles = 0, sumOfPieces = 0;
        for (int m = 0; m < filteredList.size(); m++) {
            sumOfBundles += 1;
            sumOfPieces += filteredList.get(m).getNoOfPieces();

            tableRow = new TableRow(this);
            tableRow = fillTableRows(tableRow
                    , filteredList.get(m).getBundleNo()
                    , "" + filteredList.get(m).getLength()
                    , "" + filteredList.get(m).getWidth()
                    , "" + filteredList.get(m).getThickness()
                    , filteredList.get(m).getGrade()
                    , "" + filteredList.get(m).getNoOfPieces()
                    , filteredList.get(m).getLocation()
                    , filteredList.get(m).getArea());
            bundlesTable.addView(tableRow);
            TableRow finalTableRow = tableRow;
            tableRow.getVirtualChildAt(8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrintHelper photoPrinter = new PrintHelper(InventoryReport.this);
                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
                    TextView bundleNo = (TextView) finalTableRow.getChildAt(0);
                    TextView length = (TextView) finalTableRow.getChildAt(1);
                    TextView width = (TextView) finalTableRow.getChildAt(2);
                    TextView thic = (TextView) finalTableRow.getChildAt(3);
                    TextView grade = (TextView) finalTableRow.getChildAt(4);
                    TextView pcs = (TextView) finalTableRow.getChildAt(5);
                    Bitmap bitmap = writeBarcode(bundleNo.getText().toString(), length.getText().toString(), width.getText().toString(),
                            thic.getText().toString(), grade.getText().toString(), pcs.getText().toString());

                    photoPrinter.printBitmap("invoice.jpg", bitmap);
                    Toast.makeText(InventoryReport.this, "tested+" + bundleNo.getText().toString(), Toast.LENGTH_SHORT).show();

                }
            });
//            TableRow clickTableRow = tableRow;
//            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View v) {
////                                                TextView textView = ((TextView) tableRow.getChildAt(0));
////                                                tableRow.setBackgroundResource(R.color.light_orange_2);
//                    String bundleNo = ((TextView) clickTableRow.getChildAt(0)).getText().toString();
//                    Log.e("b", bundleNo);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryReport.this);
//                    builder.setMessage("Are you want hide bundle number: " + bundleNo + " ?");
//                    builder.setTitle("Delete");
//                    builder.setIcon(R.drawable.ic_warning_black_24dp);
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            databaseHandler.updateBundlesFlag(bundleNo);// 1 mean hide
//                            bundlesTable.removeView(clickTableRow);
//                        }
//                    });
//                    builder.show();
//                    return false;
//                }
//            });
        }

        noOfBundles.setText("" + sumOfBundles);
        noOfPieces.setText("" + sumOfPieces);
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

    public Date formatDate(String date){

        Log.e("date", date);
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(myFormat, Locale.US);
        Date d = null;
        try {
            d = simpleDateFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                if (flag == 0)
                    dateFrom.setText(sdf.format(calendar.getTime()));
                else
                    dateTo.setText(sdf.format(calendar.getTime()));

                if (!dateFrom.getText().toString().equals("") && !dateFrom.getText().toString().equals("")) {
                    filters();
                }
            }
        };
        return date;
    }

    public Bitmap writeBarcode(String data, String length, String width, String thic, String grades, String pcs) {
        final Dialog dialog = new Dialog(InventoryReport.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.barcode_design);
        TextView companyName, bundelNo, TLW, pcsNo, grade;

        companyName = (TextView) dialog.findViewById(R.id.companyName);
        bundelNo = (TextView) dialog.findViewById(R.id.bundelNo);
        TLW = (TextView) dialog.findViewById(R.id.TLW);
        pcsNo = (TextView) dialog.findViewById(R.id.pcsNo);
        grade = (TextView) dialog.findViewById(R.id.grade);
        ImageView iv = (ImageView) dialog.findViewById(R.id.barcode);

        companyName.setText(generalSettings.getCompanyName());
        bundelNo.setText(data);
        TLW.setText(thic + " X " + width + " X " + length);
        pcsNo.setText(pcs);
        grade.setText(grades);


        LinearLayout linearView = (LinearLayout) dialog.findViewById(R.id.design);

        // barcode data
        String barcode_data = data;
        Bitmap bitmap = null;//  AZTEC -->QR
        try {
            bitmap = encodeAsBitmap(barcode_data, BarcodeFormat.CODE_128, 1100, 200);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        iv.setImageBitmap(bitmap);

        linearView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        linearView.layout(0, 0, linearView.getMeasuredWidth(), linearView.getMeasuredHeight());

        Log.e("size of img ", "width=" + linearView.getMeasuredWidth() + "      higth =" + linearView.getHeight());

        Bitmap bitmaps = Bitmap.createBitmap(linearView.getWidth(), linearView.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmaps);
        Drawable bgDrawable = linearView.getBackground();
        if (bgDrawable != null) {
            bgDrawable.draw(canvas);
        } else {
            canvas.drawColor(Color.WHITE);
        }
        linearView.draw(canvas);

//        dialog.show();

        return bitmaps;

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.inventory_report_location:
                loc = parent.getSelectedItem().toString();
                filters();
                break;
            case R.id.inventory_report_area:
                areaField = parent.getSelectedItem().toString();
                filters();
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        int flag = 0;
        switch (v.getId()) {
            case R.id.inventory_report_from:
                flag = 0;
                break;
            case R.id.inventory_report_to:
                flag = 1;
                break;
        }

        new DatePickerDialog(InventoryReport.this, openDatePickerDialog(flag), calendar
                .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();

    }

    public void setSlideAnimation() {
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(InventoryReport.this, ReportsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        setSlideAnimation();
    }
}
