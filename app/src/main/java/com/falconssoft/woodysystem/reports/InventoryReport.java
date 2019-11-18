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

public class InventoryReport extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private TableLayout bundlesTable;
    private DatabaseHandler databaseHandler;
    public static List<BundleInfo> bundleInfoServer = new ArrayList<>();
    private List<String> locationList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private WoodPresenter woodPresenter;
    private Animation animation;
    private TextView textView;
    private Spinner location, area;
    private ArrayAdapter<String> locationAdapter;
    private ArrayAdapter<String> areaAdapter;
    private String loc = "All", areaField = "All";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_report);

        databaseHandler = new DatabaseHandler(this);
        woodPresenter = new WoodPresenter(this);
        bundlesTable = findViewById(R.id.inventory_report_table);
        location = findViewById(R.id.inventory_report_location);
        area = findViewById(R.id.inventory_report_area);
        textView = findViewById(R.id.inventory_report_tv);
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
        location.setOnItemSelectedListener(this);
        area.setOnItemSelectedListener(this);
//        fillTable(bundleInfoServer);
    }

    /**
     * private class JSONTask extends AsyncTask<String, String, List<Orders>> {
     *
     * @Override protected void onPreExecute() {
     * super.onPreExecute();
     * <p>
     * }
     * @Override protected List<Orders> doInBackground(String... params) {
     * URLConnection connection = null;
     * BufferedReader reader = null;
     * <p>
     * try {
     * <p>
     * URL url = new URL("http://" + SettingsFile.ipAddress + "/import.php?FLAG=2");
     * <p>
     * URLConnection conn = url.openConnection();
     * conn.setDoOutput(true);
     * <p>
     * reader = new BufferedReader(new
     * InputStreamReader(conn.getInputStream()));
     * <p>
     * StringBuilder sb = new StringBuilder();
     * String line = null;
     * <p>
     * // Read Server Response
     * while ((line = reader.readLine()) != null) {
     * sb.append(line);
     * }
     * <p>
     * String finalJson = sb.toString();
     * Log.e("finalJson*********", finalJson);
     * <p>
     * JSONObject parentObject = new JSONObject(finalJson);
     * <p>
     * try {
     * JSONArray parentArrayOrders = parentObject.getJSONArray("ONLY_ORDER");
     * orders.clear();
     * for (int i = 0; i < parentArrayOrders.length(); i++) {
     * JSONObject finalObject = parentArrayOrders.getJSONObject(i);
     * <p>
     * Orders order = new Orders();
     * order.setPlacingNo(finalObject.getString("PLACING_NO"));
     * order.setOrderNo(finalObject.getString("ORDER_NO"));
     * order.setContainerNo(finalObject.getString("CONTAINER_NO"));
     * order.setDateOfLoad(finalObject.getString("DATE_OF_LOAD"));
     * order.setDestination(finalObject.getString("DESTINATION"));
     * order.setLocation(finalObject.getString("LOCATION"));
     * <p>
     * orders.add(order);
     * }
     * } catch (JSONException e) {
     * Log.e("Import Data2", e.getMessage().toString());
     * }
     * <p>
     * try {
     * JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_ORDER");
     * bundles.clear();
     * for (int i = 0; i < parentArrayOrders.length(); i++) {
     * JSONObject finalObject = parentArrayOrders.getJSONObject(i);
     * <p>
     * Orders order = new Orders();
     * order.setThickness(finalObject.getDouble("THICKNESS"));
     * order.setWidth(finalObject.getDouble("WIDTH"));
     * order.setLength(finalObject.getDouble("LENGTH"));
     * order.setGrade(finalObject.getString("GRADE"));
     * order.setNoOfPieces(finalObject.getInt("PIECES"));
     * order.setBundleNo(finalObject.getString("BUNDLE_NO"));
     * order.setLocation(finalObject.getString("LOCATION"));
     * order.setArea(finalObject.getString("AREA"));
     * order.setPlacingNo(finalObject.getString("PLACING_NO"));
     * order.setOrderNo(finalObject.getString("ORDER_NO"));
     * order.setContainerNo(finalObject.getString("CONTAINER_NO"));
     * order.setDateOfLoad(finalObject.getString("DATE_OF_LOAD"));
     * order.setDestination(finalObject.getString("DESTINATION"));
     * <p>
     * String pic = finalObject.getString("PART1") + finalObject.getString("PART2") +
     * finalObject.getString("PART3") + finalObject.getString("PART4") +
     * finalObject.getString("PART5") + finalObject.getString("PART6") +
     * finalObject.getString("PART7") + finalObject.getString("PART8");
     * <p>
     * pic = pic.replaceAll("null", "");
     * <p>
     * order.setPicture(pic);
     * <p>
     * bundles.add(order);
     * }
     * } catch (JSONException e) {
     * Log.e("Import Data1", e.getMessage().toString());
     * }
     * <p>
     * <p>
     * try {
     * JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_PIC");
     * pictures.clear();
     * for (int i = 0; i < parentArrayOrders.length(); i++) {
     * JSONObject finalObject = parentArrayOrders.getJSONObject(i);
     * <p>
     * Pictures picture = new Pictures();
     * picture.setOrderNo(finalObject.getString("ORDER_NO"));
     * <p>
     * String[] rowPics = new String[8];
     * <p>
     * for (int k = 1; k <= 8; k++) {
     * String pic = finalObject.getString("PIC" + k + "PART1") + finalObject.getString("PIC" + k + "PART2") +
     * finalObject.getString("PIC" + k + "PART3") + finalObject.getString("PIC" + k + "PART4") +
     * finalObject.getString("PIC" + k + "PART5") + finalObject.getString("PIC" + k + "PART6") +
     * finalObject.getString("PIC" + k + "PART7") + finalObject.getString("PIC" + k + "PART8");
     * <p>
     * pic = pic.replaceAll("null", "");
     * rowPics[k - 1] = pic;
     * }
     * <p>
     * picture.setPic1(rowPics[0]);
     * picture.setPic2(rowPics[1]);
     * picture.setPic3(rowPics[2]);
     * picture.setPic4(rowPics[3]);
     * picture.setPic5(rowPics[4]);
     * picture.setPic6(rowPics[5]);
     * picture.setPic7(rowPics[6]);
     * picture.setPic8(rowPics[7]);
     * <p>
     * pictures.add(picture);
     * }
     * } catch (JSONException e) {
     * Log.e("Import Data1", e.getMessage().toString());
     * }
     * <p>
     * <p>
     * } catch (MalformedURLException e) {
     * Log.e("Customer", "********ex1");
     * e.printStackTrace();
     * } catch (IOException e) {
     * Log.e("Customer", e.getMessage().toString());
     * e.printStackTrace();
     * <p>
     * } catch (JSONException e) {
     * Log.e("Customer", "********ex3  " + e.toString());
     * e.printStackTrace();
     * } finally {
     * Log.e("Customer", "********finally");
     * if (connection != null) {
     * Log.e("Customer", "********ex4");
     * // connection.disconnect();
     * }
     * try {
     * if (reader != null) {
     * reader.close();
     * }
     * } catch (IOException e) {
     * e.printStackTrace();
     * }
     * }
     * return orders;
     * }
     * @Override protected void onPostExecute(final List<Orders> result) {
     * super.onPostExecute(result);
     * <p>
     * if (result != null) {
     * Log.e("result", "*****************" + orders.size());
     * fillTable(orders);
     * //                storeInDatabase();
     * } else {
     * Toast.makeText(LoadingOrderReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
     * }
     * }
     * }
     */

    public void filters() {
        List<BundleInfo> filtered = new ArrayList<>();
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

        fillTable(filtered);

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
        for (int m = 0; m < filteredList.size(); m++) {
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
//            TableRow finalTableRow = tableRow;
//            tableRow.getVirtualChildAt(8).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    PrintHelper photoPrinter = new PrintHelper(BundlesReport.this);
//                    photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
//                    TextView bundleNo=(TextView) finalTableRow.getChildAt(0);
//                    TextView length=(TextView) finalTableRow.getChildAt(1);
//                    TextView width=(TextView) finalTableRow.getChildAt(2);
//                    TextView thic=(TextView) finalTableRow.getChildAt(3);
//                    TextView grade=(TextView) finalTableRow.getChildAt(4);
//                    TextView pcs=(TextView) finalTableRow.getChildAt(5);
//                    Bitmap bitmap=writeBarcode(bundleNo.getText().toString(),length.getText().toString(),width.getText().toString(),
//                            thic.getText().toString(),grade.getText().toString(),pcs.getText().toString());
//
//                    photoPrinter.printBitmap("invoice.jpg", bitmap);
//                    Toast.makeText(BundlesReport.this, "tested+"+bundleNo.getText().toString(), Toast.LENGTH_SHORT).show();
//
//                }
//            });
            TableRow clickTableRow = tableRow;
            tableRow.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
//                                                TextView textView = ((TextView) tableRow.getChildAt(0));
//                                                tableRow.setBackgroundResource(R.color.light_orange_2);
                    String bundleNo = ((TextView) clickTableRow.getChildAt(0)).getText().toString();
                    Log.e("b", bundleNo);
                    AlertDialog.Builder builder = new AlertDialog.Builder(InventoryReport.this);
                    builder.setMessage("Are you want hide bundle number: " + bundleNo + " ?");
                    builder.setTitle("Delete");
                    builder.setIcon(R.drawable.ic_warning_black_24dp);
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            databaseHandler.updateBundlesFlag(bundleNo);// 1 mean hide
                            bundlesTable.removeView(clickTableRow);
                        }
                    });
                    builder.show();
                    return false;
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
        Intent intent = new Intent(InventoryReport.this, ReportsActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        setSlideAnimation();
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

        companyName.setText(SettingsFile.companyName);
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
}
