package com.falconssoft.woodysystem;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
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

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Spliterator;

public class LoadingOrderReport extends AppCompatActivity {

    private TextView textView;
    private TableLayout ordersTable;
    private LinearLayout linearLayout;
    private EditText from, to;
    private Button arrow;
    private HorizontalListView listView;
    private List<Orders> orders, bundles;
    private List<Pictures> pictures;
    private Animation animation;
    ItemsListAdapter2 adapter;
    private Calendar myCalendar;
    Spinner location;
    private ArrayAdapter<String> locationAdapter;
    private String loc = "";
    private Settings generalSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order_report);

        generalSettings = new Settings();
        generalSettings = new DatabaseHandler(this).getSettings();
        orders = new ArrayList<>();
        bundles = new ArrayList<>();
        pictures = new ArrayList<>();

        textView = findViewById(R.id.loading_order_report);
        ordersTable = findViewById(R.id.orders_table);
        listView = findViewById(R.id.listview);
        linearLayout = findViewById(R.id.linearLayout);
        arrow = findViewById(R.id.arrow);
        location =(Spinner) findViewById(R.id.Loding_Order_Location);
        from = (EditText) findViewById(R.id.Loding_Order_from);
        to =(EditText) findViewById(R.id.Loding_Order_to);

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

        adapter = new ItemsListAdapter2(LoadingOrderReport.this, new ArrayList<>());
        listView.setAdapter(adapter);


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

    }

    private class JSONTask extends AsyncTask<String, String, List<Orders>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<Orders> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {

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
                        order.setNoOfPieces(finalObject.getInt("PIECES"));
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
                fillTable(orders);
//                storeInDatabase();
            } else {
                Toast.makeText(LoadingOrderReport.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    void fillTable(List<Orders> orders) {

        for (int k = 0; k < orders.size(); k++) {

            TableRow tableRow = new TableRow(this);
            for (int i = 0; i < 7; i++) {
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.color.light_orange);
                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, 40, 1f);
                textViewParam.setMargins(0, 2, 2, 0);
                textView.setTextSize(15);
                textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
                textView.setLayoutParams(textViewParam);
                switch (i) {
                    case 0:
                        textView.setText(orders.get(k).getOrderNo());
                        break;
                    case 1:
                        textView.setText(orders.get(k).getPlacingNo());
                        break;
                    case 2:
                        textView.setText(orders.get(k).getContainerNo());
                        break;
                    case 3:
                        textView.setText(orders.get(k).getDateOfLoad());
                        break;
                    case 4:
                        textView.setText(orders.get(k).getDestination());
                        break;
                    case 5:
                        final int index = k;
                        textView.setText("Preview");
                        textView.setTextColor(ContextCompat.getColor(this, R.color.preview));
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                List<BundleInfo> bundleInfos = new ArrayList<>();

                                for (int i = 0; i < bundles.size(); i++) {
//                                    Log.e("ooo  " , ""+ orders.get(index).getOrderNo() + "  " + bundles.get(i).getBundleNo());
                                    if (orders.get(index).getOrderNo().equals(bundles.get(i).getOrderNo()) &&
                                            orders.get(index).getPlacingNo().equals(bundles.get(i).getPlacingNo()) &&
                                            orders.get(index).getContainerNo().equals(bundles.get(i).getContainerNo()) &&
                                            orders.get(index).getDateOfLoad().equals(bundles.get(i).getDateOfLoad())) {

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
                                adapter = new ItemsListAdapter2(LoadingOrderReport.this, bundleInfos);
                                listView.setAdapter(adapter);
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        openLargePicDialog(StringToBitMap(bundleInfos.get(position).getPicture()));
                                    }
                                });

                                try {
                                    Thread.sleep(300);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }

                                slideUp(linearLayout);

                            }
                        });
                        break;

                    case 6:
                        final int index1 = k;
                        TableRow.LayoutParams param = new TableRow.LayoutParams(0, 40, 0.25f);
                        textViewParam.setMargins(0, 2, 2, 0);
                        textView.setLayoutParams(param);

                        textView.setBackgroundDrawable(getResources().getDrawable(R.drawable.pic));

                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Pictures pics = new Pictures();
                                for (int i = 0; i < pictures.size(); i++) {
                                    if (pictures.get(i).getOrderNo().equals(orders.get(index1).getOrderNo())) {
                                        pics = pictures.get(i);
                                        break;
                                    }
                                }
                                openPicDialog(pics);
                            }
                        });
                        break;


                }
                tableRow.addView(textView);
            }
            ordersTable.addView(tableRow);
        }

    }

    public void openLargePicDialog(Bitmap picts) {
        Dialog dialog = new Dialog(LoadingOrderReport.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.pic_dialog2);
        dialog.setCanceledOnTouchOutside(true);

        ImageView imageView = dialog.findViewById(R.id.main_pic);
        imageView.setImageBitmap(picts);

        dialog.show();

    }


    public void openPicDialog(Pictures picts) {
        Dialog dialog = new Dialog(LoadingOrderReport.this);
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

        PicturesAdapter adapter = new PicturesAdapter(LoadingOrderReport.this, pics);
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

                String myFormat = "dd/MM/yyyy";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

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

        try {
            List<Orders> filtered = new ArrayList<>();
            for (int k = 0; k < orders.size(); k++) {
                if(fromDate.equals("") || toDate.equals("")){
                    if (loc.equals("") || loc.equals(orders.get(k).getLocation()))
                        filtered.add(orders.get(k));
                } else {
                    if ((formatDate(orders.get(k).getDateOfLoad()).after(formatDate(fromDate)) || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(fromDate))) &&
                            (formatDate(orders.get(k).getDateOfLoad()).before(formatDate(toDate)) || formatDate(orders.get(k).getDateOfLoad()).equals(formatDate(toDate))) &&
                            (loc.equals("") || loc.equals(orders.get(k).getLocation())))
                        filtered.add(orders.get(k));
                }
            }

            ordersTable.removeAllViews();
            fillTable(filtered);

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}


//A3VG67T9m8cGW

