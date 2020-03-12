package com.falconssoft.woodysystem.reports;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.HorizontalListView;
import com.falconssoft.woodysystem.ItemsListAdapter2;
import com.falconssoft.woodysystem.PicturesAdapter;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class LoadingOrderReport extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private TextView textView;
    private static LinearLayout linearLayout, headerLinear;
    private EditText from, to, searchBundleNo;
    private Button arrow;
//    private static HorizontalListView listView;
    private static ListView listView;
    private static List<Orders> orders, bundles;
    private static List<Pictures> pictures;
    private Animation animation;
    //    static ItemsListAdapter2 adapter;
    static LoadingOrderReportAdapter2 adapter;
    static LoadingOrderReportAdapter adapter2;
    private ListView list;
    private Calendar myCalendar;
    Spinner location;
    private ArrayAdapter<String> locationAdapter;
    private String loc = "", searchBundleNoString = "";
    private Settings generalSettings;
    private String orderNo;
    private JSONArray bundleNo = new JSONArray();
    private DatabaseHandler MHandler;
    List<BundleInfo> bundleInfos;
    //    List<String> bundleNoString;
    static Dialog dialog;

    String myFormat;
    SimpleDateFormat sdf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order_report);

        progressDialog = new ProgressDialog(this,R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");
        MHandler = new DatabaseHandler(LoadingOrderReport.this);
        generalSettings = new DatabaseHandler(this).getSettings();
        orders = new ArrayList<>();
        bundles = new ArrayList<>();
        pictures = new ArrayList<>();
//        bundleNoString= new ArrayList<>();

        list = findViewById(R.id.list);
        textView = findViewById(R.id.loading_order_report);
        listView = findViewById(R.id.listview);
        linearLayout = findViewById(R.id.linearLayout);
        arrow = findViewById(R.id.arrow);
        location = (Spinner) findViewById(R.id.Loding_Order_Location);
        from = (EditText) findViewById(R.id.Loding_Order_from);
        to = (EditText) findViewById(R.id.Loding_Order_to);
        searchBundleNo = findViewById(R.id.loadingOrder_report_search_bundleNo);
        searchBundleNo.addTextChangedListener(new watchTextChange(searchBundleNo));

        myFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat(myFormat, Locale.US);

        List<String> locationList = new ArrayList<>();
        locationList.add("");
        locationList.add("Amman");
        locationList.add("Kalinovka");
        locationList.add("Rudniya Store");
        locationList.add("Rudniya Sawmill");
        locationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location.setAdapter(locationAdapter);

        animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

//        adapter = new ItemsListAdapter2(LoadingOrderReport.this, new ArrayList<>());
        adapter = new LoadingOrderReportAdapter2(LoadingOrderReport.this, new ArrayList<>());
        listView.setAdapter(adapter);

        adapter2 = new LoadingOrderReportAdapter(LoadingOrderReport.this, new ArrayList<>(), new ArrayList<>());
        list.setAdapter(adapter2);


        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slideDown(linearLayout);
            }
        });

        new JSONTask().execute();

        location.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                loc = parent.getSelectedItem().toString();
                filters();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        myCalendar = Calendar.getInstance();

        from.setText("1/12/2019");
        to.setText(sdf.format(myCalendar.getTime()));

        from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoadingOrderReport.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoadingOrderReport.this, openDatePickerDialog(1), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

//        fillTable(orders);

    }

    class watchTextChange implements TextWatcher {

        private View view;

        public watchTextChange(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            switch (view.getId()) {
                case R.id.loadingOrder_report_search_bundleNo:
                    if (linearLayout.getVisibility() == View.VISIBLE)
                        slideDown(linearLayout);

                    searchBundleNoString = String.valueOf(s);//formatDecimalValue(String.valueOf(s));
                    filters();
                    break;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }

    private class JSONTask extends AsyncTask<String, String, List<Orders>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog();

        }

        @Override
        protected List<Orders> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.214/woody/import.php?FLAG=2
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=2");

                URLConnection conn = url.openConnection();
                conn.setDoOutput(true);

                reader = new BufferedReader(new
                        InputStreamReader(conn.getInputStream()));

                StringBuilder sb = new StringBuilder();
                String line = null;

                // Read Server Response
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }

                String finalJson = sb.toString();
                Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("ONLY_ORDER");
                    orders.clear();
                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject finalObject = parentArrayOrders.getJSONObject(i);

                        Orders order = new Orders();
                        order.setPlacingNo(finalObject.getString("PLACING_NO"));
                        order.setOrderNo(finalObject.getString("ORDER_NO"));
                        order.setContainerNo(finalObject.getString("CONTAINER_NO"));
                        order.setDateOfLoad(finalObject.getString("DATE_OF_LOAD"));
                        order.setDestination(finalObject.getString("DESTINATION"));
                        order.setLocation(finalObject.getString("LOCATION"));

                        orders.add(order);
                    }
                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
                }

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_ORDER");
                    bundles.clear();

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject finalObject = parentArrayOrders.getJSONObject(i);

                        Orders order = new Orders();
                        order.setThickness(finalObject.getDouble("THICKNESS"));
                        order.setWidth(finalObject.getDouble("WIDTH"));
                        order.setLength(finalObject.getDouble("LENGTH"));
                        order.setGrade(finalObject.getString("GRADE"));
                        order.setNoOfPieces(finalObject.getDouble("PIECES"));
                        order.setBundleNo(finalObject.getString("BUNDLE_NO"));
                        order.setLocation(finalObject.getString("LOCATION"));
                        order.setArea(finalObject.getString("AREA"));
                        order.setPlacingNo(finalObject.getString("PLACING_NO"));
                        order.setOrderNo(finalObject.getString("ORDER_NO"));
                        order.setContainerNo(finalObject.getString("CONTAINER_NO"));
                        order.setDateOfLoad(finalObject.getString("DATE_OF_LOAD"));
                        order.setDestination(finalObject.getString("DESTINATION"));

                        String pic = finalObject.getString("PART1") + finalObject.getString("PART2") +
                                finalObject.getString("PART3") + finalObject.getString("PART4") +
                                finalObject.getString("PART5") + finalObject.getString("PART6") +
                                finalObject.getString("PART7") + finalObject.getString("PART8");

                        pic = pic.replaceAll("null", "");

                        order.setPicture(pic);

                        bundles.add(order);
                    }
                } catch (JSONException e) {
                    Log.e("Import Data1", e.getMessage().toString());
                }


                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_PIC");
                    pictures.clear();
                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject finalObject = parentArrayOrders.getJSONObject(i);

                        Pictures picture = new Pictures();
                        picture.setOrderNo(finalObject.getString("ORDER_NO"));

                        String[] rowPics = new String[8];

                        for (int k = 1; k <= 8; k++) {
                            String pic = finalObject.getString("PIC" + k + "PART1") + finalObject.getString("PIC" + k + "PART2") +
                                    finalObject.getString("PIC" + k + "PART3") + finalObject.getString("PIC" + k + "PART4") +
                                    finalObject.getString("PIC" + k + "PART5") + finalObject.getString("PIC" + k + "PART6") +
                                    finalObject.getString("PIC" + k + "PART7") + finalObject.getString("PIC" + k + "PART8");

                            pic = pic.replaceAll("null", "");
                            rowPics[k - 1] = pic;
                        }

                        picture.setPic1(rowPics[0]);
                        picture.setPic2(rowPics[1]);
                        picture.setPic3(rowPics[2]);
                        picture.setPic4(rowPics[3]);
                        picture.setPic5(rowPics[4]);
                        picture.setPic6(rowPics[5]);
                        picture.setPic7(rowPics[6]);
                        picture.setPic8(rowPics[7]);

                        pictures.add(picture);
                    }
                } catch (JSONException e) {
                    Log.e("Import Data1", e.getMessage().toString());
                }


            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
                e.printStackTrace();

            } catch (JSONException e) {
                Log.e("Customer", "********ex3  " + e.toString());
                e.printStackTrace();
            } finally {
                Log.e("Customer", "********finally");
                if (connection != null) {
                    Log.e("Customer", "********ex4");
                    // connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return orders;
        }


        @Override
        protected void onPostExecute(final List<Orders> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + orders.size());
                adapter2 = new LoadingOrderReportAdapter(LoadingOrderReport.this, orders, bundles);
                list.setAdapter(adapter2);
                dismissDialog();

                //fillTable(orders);
//                storeInDatabase();
            } else {
                Toast.makeText(LoadingOrderReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void previewLinear(int index, Context Context, List<Orders> ordersList) {

        Log.d("ooo  ", "orders " + orders.get(index).getOrderNo() + "  " + orders.get(index).getPlacingNo()
                + "  " + orders.get(index).getContainerNo() + "  " + orders.get(index).getDateOfLoad());

        bundleInfos = new ArrayList<>();
        for (int i = 0; i < bundles.size(); i++) {
            Log.d("ooo  ", "bundles " + i + " :   " + bundles.get(i).getOrderNo() + "  " + bundles.get(i).getPlacingNo()
                    + "  " + bundles.get(i).getContainerNo() + "  " + bundles.get(i).getDateOfLoad());
//            if (orders.get(index).getOrderNo().equals(bundles.get(i).getOrderNo()) &&
//                    orders.get(index).getPlacingNo().equals(bundles.get(i).getPlacingNo()) &&
//                    orders.get(index).getContainerNo().equals(bundles.get(i).getContainerNo()) &&
//                    orders.get(index).getDateOfLoad().equals(bundles.get(i).getDateOfLoad())) {
            if (ordersList.get(index).getOrderNo().equals(bundles.get(i).getOrderNo()) &&
                    ordersList.get(index).getPlacingNo().equals(bundles.get(i).getPlacingNo()) &&
                    ordersList.get(index).getContainerNo().equals(bundles.get(i).getContainerNo()) &&
                    ordersList.get(index).getDateOfLoad().equals(bundles.get(i).getDateOfLoad())) {

                bundleInfos.add(new BundleInfo(
                        bundles.get(i).getThickness(),
                        bundles.get(i).getWidth(),
                        bundles.get(i).getLength(),
                        bundles.get(i).getGrade(),
                        bundles.get(i).getNoOfPieces(),
                        bundles.get(i).getBundleNo(),
                        bundles.get(i).getLocation(),
                        bundles.get(i).getArea(),
                        "",
                        bundles.get(i).getPicture()));
            }
        }

        Log.e("ooo  ", "" + bundleInfos.size());
//        adapter = new ItemsListAdapter2(Context, bundleInfos);
        adapter = new LoadingOrderReportAdapter2(Context, bundleInfos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openLargePicDialog(StringToBitMap(bundleInfos.get(position).getPicture()), Context);
            }
        });

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        slideUp(linearLayout);

    }

    public void previewPics(int index, Context context) {
        Pictures pics = new Pictures();
        for (int i = 0; i < pictures.size(); i++) {
            if (pictures.get(i).getOrderNo().equals(orders.get(index).getOrderNo())) {
                pics = pictures.get(i);
                break;
            }
        }
        openPicDialog(pics, context);
    }

//    void fillTable(List<Orders> orders) {
//
//        for (int k = 0; k < orders.size(); k++) {
//            final int index = k;
//            TableRow tableRow = new TableRow(this);
//            for (int i = 0; i < 7; i++) {
//                TextView textView = new TextView(this);
//                textView.setBackgroundResource(R.color.light_orange);
//                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, 40, 1f);
//                textViewParam.setMargins(0, 2, 2, 0);
//                textView.setTextSize(15);
//                textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
//                textView.setLayoutParams(textViewParam);
//                switch (i) {
//                    case 0:
//                        textView.setText(orders.get(k).getOrderNo());
//                        break;
//                    case 1:
//                        textView.setText(orders.get(k).getPlacingNo());
//                        break;
//                    case 2:
//                        textView.setText(orders.get(k).getContainerNo());
//                        break;
//                    case 3:
//                        textView.setText(orders.get(k).getDateOfLoad());
//                        break;
//                    case 4:
//                        textView.setText(orders.get(k).getDestination());
//                        break;
//                    case 5:
//                        textView.setText("Preview");
//                        textView.setTextColor(ContextCompat.getColor(this, R.color.preview));
//                        textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                bundleInfos = new ArrayList<>();
//
//                                for (int i = 0; i < bundles.size(); i++) {
////                                    Log.e("ooo  " , ""+ orders.get(index).getOrderNo() + "  " + bundles.get(i).getBundleNo());
//                                    if (orders.get(index).getOrderNo().equals(bundles.get(i).getOrderNo()) &&
//                                            orders.get(index).getPlacingNo().equals(bundles.get(i).getPlacingNo()) &&
//                                            orders.get(index).getContainerNo().equals(bundles.get(i).getContainerNo()) &&
//                                            orders.get(index).getDateOfLoad().equals(bundles.get(i).getDateOfLoad())) {
//
//                                        bundleInfos.add(new BundleInfo(
//                                                bundles.get(i).getThickness(),
//                                                bundles.get(i).getWidth(),
//                                                bundles.get(i).getLength(),
//                                                bundles.get(i).getGrade(),
//                                                bundles.get(i).getNoOfPieces(),
//                                                bundles.get(i).getBundleNo(),
//                                                bundles.get(i).getLocation(),
//                                                bundles.get(i).getArea(),
//                                                "",
//                                                bundles.get(i).getPicture()));
//                                    }
//                                }
//
//                                Log.e("ooo  ", "" + bundleInfos.size());
//                                adapter = new ItemsListAdapter2(LoadingOrderReport.this, bundleInfos);
//                                listView.setAdapter(adapter);
//                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                    @Override
//                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                                        openLargePicDialog(StringToBitMap(bundleInfos.get(position).getPicture()));
//                                    }
//                                });
//
//                                try {
//                                    Thread.sleep(300);
//                                } catch (InterruptedException e) {
//                                    e.printStackTrace();
//                                }
//
//                                slideUp(linearLayout);
//
//                            }
//                        });
//                        break;
//
//                    case 6:
//                        TableRow.LayoutParams param = new TableRow.LayoutParams(0, 40, 0.25f);
//                        textViewParam.setMargins(0, 2, 2, 0);
//                        textView.setLayoutParams(param);
//
//                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.pic));
//
//                        textView.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//
//                                Pictures pics = new Pictures();
//                                for (int i = 0; i < pictures.size(); i++) {
//                                    if (pictures.get(i).getOrderNo().equals(orders.get(index).getOrderNo())) {
//                                        pics = pictures.get(i);
//                                        break;
//                                    }
//                                }
////                                openPicDialog(pics);
//                            }
//                        });
//                        break;
//
//
//                }
//                tableRow.addView(textView);
//
//                tableRow.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//
//                        Dialog passwordDialog = new Dialog(LoadingOrderReport.this);
//                        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//                        passwordDialog.setContentView(R.layout.password_dialog);
//                        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//
//                        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
//                        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);
//
//                        done.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                if (password.getText().toString().equals("301190")) {
//                                    orderNo = orders.get(index).getOrderNo();
//
//                                    bundleInfos = new ArrayList<>();
//
//                                    for (int i = 0; i < bundles.size(); i++) {
//                                        if (orders.get(index).getOrderNo().equals(bundles.get(i).getOrderNo()) &&
//                                                orders.get(index).getPlacingNo().equals(bundles.get(i).getPlacingNo()) &&
//                                                orders.get(index).getContainerNo().equals(bundles.get(i).getContainerNo()) &&
//                                                orders.get(index).getDateOfLoad().equals(bundles.get(i).getDateOfLoad())) {
//
//                                            bundleInfos.add(new BundleInfo(
//                                                    bundles.get(i).getThickness(),
//                                                    bundles.get(i).getWidth(),
//                                                    bundles.get(i).getLength(),
//                                                    bundles.get(i).getGrade(),
//                                                    bundles.get(i).getNoOfPieces(),
//                                                    bundles.get(i).getBundleNo(),
//                                                    bundles.get(i).getLocation(),
//                                                    bundles.get(i).getArea(),
//                                                    "",
//                                                    ""));
//                                        }
//                                    }
//                                    for (int i = 0; i < bundleInfos.size(); i++) {
//                                        bundleNo.put(bundleInfos.get(i).getJSONObject());
//                                    }
//                                    new JSONTask2().execute();
////                                    ordersTable.removeView(tableRow);
//                                    passwordDialog.dismiss();
//                                } else {
//                                    Toast.makeText(LoadingOrderReport.this, "Not Authorized!", Toast.LENGTH_SHORT).show();
//                                    password.setText("");
//                                }
//                            }
//                        });
//
//                        passwordDialog.show();
////                        AlertDialog.Builder builder = new AlertDialog.Builder(LoadingOrderReport.this);
////                        builder.setMessage("Are you want delete this order ?");
////                        builder.setTitle("Delete");
////                        builder.setIcon(R.drawable.ic_warning_black_24dp);
////                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
////                            @Override
////                            public void onClick(DialogInterface dialog, int which) {
////
////                                orderNo = orders.get(index).getOrderNo();
////                                new JSONTask2().execute();
////                                ordersTable.removeView(tableRow);
////                            }
////                        });
////                        builder.show();
//
//
//                        return false;
//                    }
//                });
//            }
////            ordersTable.addView(tableRow);
//        }
//
//    }

    public void openLargePicDialog(Bitmap picts, Context context) {
        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pic_dialog2);
        dialog.setCanceledOnTouchOutside(true);

        ImageView imageView = dialog.findViewById(R.id.main_pic);
        imageView.setImageBitmap(picts);

        dialog.show();

    }

    public void openPicDialog(Pictures picts, Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pic_dialog);
        dialog.setCanceledOnTouchOutside(true);


        HorizontalListView listView = dialog.findViewById(R.id.listview);

        ArrayList<Bitmap> pics = new ArrayList<>();
        pics.add(StringToBitMap(picts.getPic1()));
        pics.add(StringToBitMap(picts.getPic2()));
        pics.add(StringToBitMap(picts.getPic3()));
        pics.add(StringToBitMap(picts.getPic4()));
        pics.add(StringToBitMap(picts.getPic5()));
        pics.add(StringToBitMap(picts.getPic6()));
        pics.add(StringToBitMap(picts.getPic7()));
        pics.add(StringToBitMap(picts.getPic8()));

        PicturesAdapter adapter = new PicturesAdapter(context, pics);
        listView.setAdapter(adapter);

        dialog.show();

    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);


                if (flag == 0)
                    from.setText(sdf.format(myCalendar.getTime()));
                else
                    to.setText(sdf.format(myCalendar.getTime()));

                if (!from.getText().toString().equals("") && !to.getText().toString().equals(""))
                    filters();

            }

        };
        return date;
    }

    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public void showDialog(){
        progressDialog.show();
    }

    public void dismissDialog(){
        progressDialog.dismiss();
    }

    private class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("DELETE_ORDER", "1"));
                nameValuePairs.add(new BasicNameValuePair("ORDER_NO", orderNo));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_NO", bundleNo.toString()));

                request.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                HttpResponse response = client.execute(request);

                BufferedReader in = new BufferedReader(new
                        InputStreamReader(response.getEntity().getContent()));

                StringBuffer sb = new StringBuffer("");
                String line = "";

                while ((line = in.readLine()) != null) {
                    sb.append(line);
                }

                in.close();

                JsonResponse = sb.toString();
                Log.e("tag", "" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s != null) {
                if (s.contains("DELETE ORDER SUCCESS")) {
                    MHandler.deleteOrder(orderNo);
                } else {
                    Toast.makeText(LoadingOrderReport.this, "Failed to export data!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(LoadingOrderReport.this, "No internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void slideUp(View view) {
        view.setVisibility(View.VISIBLE);
//        view.bringToFront();
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                // toXDelta
                view.getHeight(),  // fromYDelta
                0);                // toYDelta
        animate.setDuration(500);
        animate.setStartTime(100);
        animate.setFillAfter(true);
        view.startAnimation(animate);

//        headerLinear = new LinearLayout(this);
//        addListeHeader(headerLinear);
//        listView.addView(headerLinear);
    }

    public void slideDown(View view) {
        view.setVisibility(View.GONE);
        TranslateAnimation animate = new TranslateAnimation(
                0,                 // fromXDelta
                0,                 // toXDelta
                0,  // fromYDelta
                view.getHeight() + 3);                // toYDelta
        animate.setDuration(500);
        animate.setStartTime(200);
//        animate.setFillAfter(true);
        view.startAnimation(animate);

    }

    public Date formatDate(String date) throws ParseException {

        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        Date d = sdf.parse(date);
        return d;
    }

    public void filters() {

        String fromDate = from.getText().toString().trim();
        String toDate = to.getText().toString();
        List<Orders> filtered = new ArrayList<>();
        try {

            filtered.clear();

            String localDate = "", localContainer = "", localPlacingNo = "", localOrderNo = "";
            for (int m = 0; m < bundles.size(); m++) {
//                    Log.e("compare333",bundles.get(m).getBundleNo().toLowerCase());
//                    Log.e("compare334","" + bundles.get(m).getBundleNo().toLowerCase().contains(searchBundleNoString));
                if (bundles.get(m).getBundleNo().toLowerCase().contains(searchBundleNoString) || searchBundleNoString.equals("")) {
                    localDate = bundles.get(m).getDateOfLoad();
                    localContainer = bundles.get(m).getContainerNo();
                    localPlacingNo = bundles.get(m).getPlacingNo();
                    localOrderNo = bundles.get(m).getOrderNo();
                    for (int k = 0; k < orders.size(); k++) {
//                if (fromDate.equals("") || toDate.equals("")) {
//                    if (loc.equals("") || loc.equals(orders.get(k).getLocation()))
//                        filtered.add(orders.get(k));
//                } else {
                        if ((formatDate(orders.get(k).getDateOfLoad()).after(formatDate(fromDate))
                                || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(fromDate)) || fromDate.equals("")) &&
                                (formatDate(orders.get(k).getDateOfLoad()).before(formatDate(toDate))
                                        || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(toDate)) || toDate.equals("")) &&
                                (loc.equals("") || loc.equals(orders.get(k).getLocation())))
                            if ((orders.get(k).getContainerNo().equals(localContainer) || localContainer.equals(""))
                                    && (orders.get(k).getPlacingNo().equals(localPlacingNo) || localPlacingNo.equals(""))
                                    && (orders.get(k).getOrderNo().equals(localOrderNo) || localOrderNo.equals(""))
                                    && (orders.get(k).getDateOfLoad().equals(localDate) || localDate.equals(""))) {
                                filtered.add(orders.get(k));
                                break;
                            }
//            }
//                }
                    }

                }
            }
//            for (int k = 0; k < orders.size(); k++) {
////                if (fromDate.equals("") || toDate.equals("")) {
////                    if (loc.equals("") || loc.equals(orders.get(k).getLocation()))
////                        filtered.add(orders.get(k));
////                } else {
//                if ((formatDate(orders.get(k).getDateOfLoad()).after(formatDate(fromDate))
//                        || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(fromDate)) || fromDate.equals("")) &&
//                        (formatDate(orders.get(k).getDateOfLoad()).before(formatDate(toDate))
//                                || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(toDate)) || toDate.equals("")) &&
//                        (loc.equals("") || loc.equals(orders.get(k).getLocation())))
//                    if ((orders.get(k).getContainerNo().equals(localContainer) || localContainer.equals(""))
//                            && (orders.get(k).getPlacingNo().equals(localPlacingNo) || localPlacingNo.equals(""))
//                            && (orders.get(k).getOrderNo().equals(localOrderNo) || localOrderNo.equals("")))
//                        filtered.add(orders.get(k));
//
////            }
////                }
//            }
            Log.e("set 1", "" + filtered.size());
            HashSet<Orders> listToSet = new HashSet<Orders>(filtered);
            filtered.clear();
            filtered.addAll(listToSet);
            Log.e("set", "" + filtered.size());
            adapter2 = new LoadingOrderReportAdapter(LoadingOrderReport.this, filtered, bundles);
            list.setAdapter(adapter2);

        } catch (
                ParseException e) {
            e.printStackTrace();
        }

    }

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}


//A3VG67T9m8cGW

