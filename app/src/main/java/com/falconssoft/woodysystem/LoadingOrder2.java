package com.falconssoft.woodysystem;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.email.SendMailTask;
import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.PlannedPL;
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
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import static com.falconssoft.woodysystem.LoadingOrder.searchBar;
import static com.falconssoft.woodysystem.SettingsFile.emailContent;
import static com.falconssoft.woodysystem.SettingsFile.emailTitle;
import static com.falconssoft.woodysystem.SettingsFile.recipientName;
import static com.falconssoft.woodysystem.SettingsFile.senderName;
import static com.falconssoft.woodysystem.SettingsFile.senderPassword;
import static com.falconssoft.woodysystem.Stage3.flagOpenJ;

public class LoadingOrder2 extends AppCompatActivity {

    /**
     * loading order is just for whose has packing list.
     * the customer can't take bundles with other specifications(L,W,P,T) until he planned another bundles match the specifications
     * the customer can't take bundles with count not match his packing list count
     */

    HorizontalListView listView;
    ListView listView2;
    private EditText placingNo, orderNo, containerNo, dateOfLoad, destination;
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8;
    private Button done;
    private TextView textView;
    private Orders order;
    private Pictures picture;
    private DatabaseHandler databaseHandler;
    private List<BundleInfo> bundles;
    private List<Orders> checkDuplicate = new ArrayList<>();
    private List<Orders> originalList = new ArrayList<>();
    private List<Orders> checkedBundleList = new ArrayList<>();
    private List<Orders> unMatchedList = new ArrayList<>();
    private Calendar myCalendar;
    static int index = 0;
    ItemsListAdapter2 adapter;
    private List<BundleInfo> bundleInfosList = new ArrayList<>();
    int imageNo = 0;
    private Settings generalSettings;
    JSONArray ordered = new JSONArray();

    List<PlannedPL> plannedList = new ArrayList<>();
    List<Orders> missedBundles = new ArrayList<>();
//    BluetoothAdapter mBluetoothAdapter;
//    BluetoothSocket mmSocket;
//    BluetoothDevice mmDevice;
//    OutputStream mmOutputStream;
//    InputStream mmInputStream;
//    volatile boolean stopWorker;
//    String mainContent = "";
//    private boolean checkImageExist = false;

    JSONArray jsonArrayOrders;
    JSONArray jsonArrayUnmatchedList;

    JSONArray jsonArrayMissedBundles;
    JSONArray jsonArrayPics;

    static ArrayList<String> pics = new ArrayList<>();
    private List<File> imagesFileList = new ArrayList<>();

    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private ProgressDialog progressDialog;

    //    protected static final int CAMERA_PIC_REQUEST = 0;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order2);

        imagesFileList.clear();
        init();
        databaseHandler = new DatabaseHandler(this);
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        jsonArrayMissedBundles = new JSONArray();
//        jsonArrayOrders = new JSONArray();
        jsonArrayPics = new JSONArray();

        Drawable myDrawable = getResources().getDrawable(R.drawable.pic);
        Bitmap myBitmap = ((BitmapDrawable) myDrawable).getBitmap();
        myBitmap = getResizedBitmap(myBitmap, 100, 100);
        pics.add(null);
        pics.add(null);
        pics.add(null);
        pics.add(null);
        pics.add(null);
        pics.add(null);
        pics.add(null);
        pics.add(null);

        ItemsListAdapter obj = new ItemsListAdapter();
        bundles = obj.getSelectedItems();

        new JSONTask2().execute();


        adapter = new ItemsListAdapter2(LoadingOrder2.this, bundles);
        listView.setAdapter(adapter);
        listView2.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openCamera(position);
            }
        });

        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openCamera(position);
            }
        });

        img1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 1;
            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 2;
            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 3;
            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 4;
            }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 5;
            }
        });
        img6.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 6;
            }
        });
        img7.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 7;
            }
        });
        img8.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                openCamera(-1);
                imageNo = 8;
            }
        });

        myCalendar = Calendar.getInstance();

        dateOfLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(LoadingOrder2.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                if (!TextUtils.isEmpty(placingNo.getText().toString())) {
                    if (!TextUtils.isEmpty(orderNo.getText().toString())) {
                        if (!TextUtils.isEmpty(containerNo.getText().toString())) {
                            if (!TextUtils.isEmpty(dateOfLoad.getText().toString())) {
                                if (!TextUtils.isEmpty(destination.getText().toString())) {

//                                    Toast.makeText(LoadingOrder2.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
                                    sendBundle();

                                } else {
                                    destination.setError("Required!");
                                }
                            } else {
                                dateOfLoad.setError("Required!");
                            }
                        } else {
                            containerNo.setError("Required!");
                        }
                    } else {
                        orderNo.setError("Required!");
                    }
                } else {
                    placingNo.setError("Required!");
                }
            }
        });
    }

    public void sendBundle() {

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//            }
//        }).start();

        if (generalSettings.getStore().equals("Amman")) {
            Log.e("location1", generalSettings.getStore());
            jsonArrayOrders = new JSONArray();
            for (int i = 0; i < bundles.size(); i++) {

                order = new Orders(bundles.get(i).getThickness()
                        , bundles.get(i).getWidth()
                        , bundles.get(i).getLength()
                        , bundles.get(i).getGrade()
                        , bundles.get(i).getNoOfPieces()
                        , bundles.get(i).getBundleNo()
                        , bundles.get(i).getLocation()
                        , bundles.get(i).getArea()
                        , placingNo.getText().toString()
                        , orderNo.getText().toString()
                        , containerNo.getText().toString()
                        , dateOfLoad.getText().toString()
                        , destination.getText().toString()
                        , bundles.get(i).getPicture()
                        , bundles.get(i).getBackingList()
                        , bundles.get(i).getCustomer());

//                        databaseHandler.addOrder(order);
//                    Log.e("**********", "" + bundles.get(i).getPicture().length());
                jsonArrayOrders.put(order.getJSONObject());
            }

            picture = new Pictures(orderNo.getText().toString()
                    , pics.get(0)
                    , pics.get(1)
                    , pics.get(2)
                    , pics.get(3)
                    , pics.get(4)
                    , pics.get(5)
                    , pics.get(6)
                    , pics.get(7));

            jsonArrayPics.put(picture.getJSONObject());

//                    databaseHandler.addPictures(picture);

            new JSONTask4().execute();
//                    progressDialog.show();
//                new JSONTask().execute();

//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    searchBar.setText("2");
//                }
//            });
//                finish();

        } else {
            //checkDuplicate = new ArrayList<>();
            checkedBundleList = new ArrayList<>();
            for (int i = 0; i < bundles.size(); i++) {

                Log.e("location2", generalSettings.getStore());
                if (TextUtils.isEmpty(bundles.get(i).getPicture()))
                    bundles.get(i).setPicture("null");
                order = new Orders(bundles.get(i).getThickness()
                        , bundles.get(i).getWidth()
                        , bundles.get(i).getLength()
                        , bundles.get(i).getGrade()
                        , bundles.get(i).getNoOfPieces()
                        , bundles.get(i).getBundleNo()
                        , bundles.get(i).getLocation()
                        , bundles.get(i).getArea()
                        , placingNo.getText().toString()
                        , orderNo.getText().toString()
                        , containerNo.getText().toString()
                        , dateOfLoad.getText().toString()
                        , destination.getText().toString()
                        , bundles.get(i).getPicture()
                        , bundles.get(i).getBackingList()
                        , bundles.get(i).getCustomer());
                Log.e("followpic" , bundles.get(i).getPicture());

                order.setSerial(Integer.parseInt(bundles.get(i).getSerialNo()));
                checkedBundleList.add(order);
                checkedBundleList.get(i).setRemove(false);

                // databaseHandler.addOrder(order);

                Log.e("loadingorder", "" + i + bundles.get(i).getBackingList() + " orderNo " + orderNo.getText().toString());

//                        jsonArrayOrders.put(order.getJSONObject());


//                        if (!order.getPackingList().equals(order.getOrderNo()) && !order.getPackingList().equals("null") && bundles.get(i).getNoOfExist() == 0) {
//                            // this case when i want to exchange my bundles with others but i don't have enough count
//                            Log.e("loadingorder", "missedBundles " + order.getPackingList() + order.getOrderNo() + bundles.get(i).getNoOfExist());
//                            missedBundles.add(order);
//                            jsonArrayMissedBundles.put(order.getJSONObject());
//                        } else {
//                            Log.e("loadingorder", "checkDuplicate " + order.getPackingList() + order.getOrderNo() + bundles.get(i).getNoOfExist());
//                            checkDuplicate.add(order);
//                        }

                // databaseHandler.updateTableBundles(bundles.get(i).getBundleNo());
            }
//                    Log.e("loadingorder", "missedbundles" + missedBundles.size());
            picture = new Pictures(orderNo.getText().toString()
                    , pics.get(0)
                    , pics.get(1)
                    , pics.get(2)
                    , pics.get(3)
                    , pics.get(4)
                    , pics.get(5)
                    , pics.get(6)
                    , pics.get(7));

            jsonArrayPics.put(picture.getJSONObject());

            databaseHandler.addPictures(picture);

            new JSONTask3().execute();
//                new JSONTask().execute();

//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    searchBar.setText("2");
//                }
//            });

//                finish();

            progressDialog.dismiss();

        }

    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera(int i) {
//        checkImageExist = true;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, 1888);
            index = i;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        int permission = ActivityCompat.checkSelfPermission(LoadingOrder2.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    LoadingOrder2.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            //thumbnail = getResizedBitmap(thumbnail, 100, 100);
            File picture = null;

            if (extras != null) {
//                Bitmap pic = extras.getParcelable("data");
                if (index != -1) {
                    bundles.get(index).setPicture(BitMapToString(thumbnail));
                    String root9 = Environment.getExternalStorageDirectory().getAbsolutePath();
                    picture = new File(root9, "bundleImage" + index + ".png");
                    adapter.notifyDataSetChanged();
                } else {
                    switch (imageNo) {
                        case 1:
                            img1.setImageBitmap(thumbnail);
                            pics.set(0, BitMapToString(thumbnail));
                            break;
                        case 2:
                            img2.setImageBitmap(thumbnail);
                            pics.set(1, BitMapToString(thumbnail));
                            break;
                        case 3:
                            img3.setImageBitmap(thumbnail);
                            pics.set(2, BitMapToString(thumbnail));
                            break;
                        case 4:
                            img4.setImageBitmap(thumbnail);
                            pics.set(3, BitMapToString(thumbnail));
                            break;
                        case 5:
                            img5.setImageBitmap(thumbnail);
                            pics.set(4, BitMapToString(thumbnail));
                            break;
                        case 6:
                            img6.setImageBitmap(thumbnail);
                            pics.set(5, BitMapToString(thumbnail));
                            break;
                        case 7:
                            img7.setImageBitmap(thumbnail);
                            pics.set(6, BitMapToString(thumbnail));
                            break;
                        case 8:
                            img8.setImageBitmap(thumbnail);
                            pics.set(7, BitMapToString(thumbnail));
                            break;

                    }

                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(LoadingOrder2.this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1888);
            } else {
                Toast.makeText(LoadingOrder2.this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (pics.get(0) != null)
            img1.setImageBitmap(StringToBitMap(pics.get(0)));

        if (pics.get(1) != null)
            img2.setImageBitmap(StringToBitMap(pics.get(1)));

        if (pics.get(2) != null)
            img3.setImageBitmap(StringToBitMap(pics.get(2)));

        if (pics.get(3) != null)
            img4.setImageBitmap(StringToBitMap(pics.get(3)));

        if (pics.get(4) != null)
            img5.setImageBitmap(StringToBitMap(pics.get(4)));

        if (pics.get(5) != null)
            img6.setImageBitmap(StringToBitMap(pics.get(5)));

        if (pics.get(6) != null)
            img7.setImageBitmap(StringToBitMap(pics.get(6)));

        if (pics.get(7) != null)
            img8.setImageBitmap(StringToBitMap(pics.get(7)));
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
                dateOfLoad.setText(sdf.format(myCalendar.getTime()));
            }

        };
        return date;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void init() {
        listView = findViewById(R.id.listview);
        listView2 = findViewById(R.id.verticalListView);
        placingNo = findViewById(R.id.placing_no);
        orderNo = findViewById(R.id.order_no);
        containerNo = findViewById(R.id.container_no);
        dateOfLoad = findViewById(R.id.date_of_load);
        destination = findViewById(R.id.destination);
        done = findViewById(R.id.done);
        textView = findViewById(R.id.loading_order_textView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waiting...");

        img1 = findViewById(R.id.image1);
        img2 = findViewById(R.id.image2);
        img3 = findViewById(R.id.image3);
        img4 = findViewById(R.id.image4);
        img5 = findViewById(R.id.image5);
        img6 = findViewById(R.id.image6);
        img7 = findViewById(R.id.image7);
        img8 = findViewById(R.id.image8);

        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_to_right);
        textView.startAnimation(animation);

    }

    public void onBackPressed() {
//        super.onBackPressed();
        finish();
//        Intent intent = new Intent(LoadingOrder2.this, LoadingOrder.class);
//        startActivity(intent);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        if (bm != null) {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            // CREATE A MATRIX FOR THE MANIPULATION
            Matrix matrix = new Matrix();
            // RESIZE THE BIT MAP
            matrix.postScale(scaleWidth, scaleHeight);

            // "RECREATE" THE NEW BITMAP
            Bitmap resizedBitmap = Bitmap.createBitmap(
                    bm, 0, 0, width, height, matrix, false);
            return resizedBitmap;
        }
        return null;
    }

    public String BitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] arr = baos.toByteArray();
            String result = Base64.encodeToString(arr, Base64.DEFAULT);
            return result;
        }

        return "";
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

    // loading order // exchange different packing lists // hide missed bundles
    private class JSONTask extends AsyncTask<String, String, String> {

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
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));

                String newCust = "";
                if (plannedList.size() > 0)
                    newCust = plannedList.get(0).getCustName();

                Log.e("loadingorder", " JSONTask " + jsonArrayOrders.length());//+ " missed: " + jsonArrayMissedBundles.length());

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("ORIGINAL_LIST", jsonArrayOrders.toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("UN_FOUND_LIST", jsonArrayUnmatchedList.toString().trim()));
//                nameValuePairs.add(new BasicNameValuePair("NEW_CUST", "'" + newCust + "'"));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_PIC", jsonArrayPics.toString().trim()));
//                nameValuePairs.add(new BasicNameValuePair("BUNDLE_ORDERS", jsonArrayOrders.toString().trim()));
//                nameValuePairs.add(new BasicNameValuePair("MISSED_BUNDLES", jsonArrayMissedBundles.toString().trim()));

                //Log.e("tag", "" + jsonArrayPics.toString());

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
                Log.e("loadingorder", " JSONTaskmonitor " + JsonResponse);

                Log.v("loadingorder", " JSONTaskmonitor " + JsonResponse);

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
                if (s.contains("EXCHANGE_BUNDLE SUCCESS")) {//if (s.contains("BUNDLE_ORDER SUCCESS")) {
                    pics.clear();
                    flagOpenJ = 0;
//                    finish();
                    Intent intent = new Intent(LoadingOrder2.this, LoadingOrder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Log.e("tag", "****Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }

    // get nu. of exists for each bundles i choosed from previous page
    private class JSONTask2 extends AsyncTask<String, String, String> {  // check

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @Override
        protected String doInBackground(String... params) {
            try {
                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));

                for (int i = 0; i < bundles.size(); i++) {
                    ordered.put(bundles.get(i).getJSONObject());
                }

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("CHECK_CONTENT", ordered.toString()));
                nameValuePairs.add(new BasicNameValuePair("LOCATION", databaseHandler.getSettings().getStore().toString()));

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
                Log.e("loadingorder", " JSONTask2 " + JsonResponse);

                JSONObject object = new JSONObject(JsonResponse);

                bundleInfosList.clear();
//                String ff=JsonResponse.r((JsonResponse.indexOf("result")+5));
                Log.e("tag___", "" + JsonResponse.indexOf("result"));

                int ind = ordered.length();

                for (int i = 0; i < object.length(); i++) {
                    for (int f = 0; f < ind; f++) {
                        try {

                            JSONArray array = object.getJSONArray("result" + (f + 1));

                            JSONObject innerObject = array.getJSONObject(0);

                            BundleInfo bundleInfo = new BundleInfo();
                            bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                            bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                            bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                            bundleInfo.setGrade(innerObject.getString("GRADE"));
                            bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                            //bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                            //bundleInfo.setLocation(innerObject.getString("LOCATION"));
                            //bundleInfo.setArea(innerObject.getString("AREA"));
                            //bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                            //bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                            //bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                            //bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                            bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                            bundleInfo.setNoOfExist(innerObject.getInt("COUNT"));

                            bundleInfosList.add(bundleInfo);

                        } catch (Exception e) {

                        }
                    }
                }
//                }
                Log.e("tag2", "" + bundleInfosList.size());
                Log.e("tag3", "" + object.length());
                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            compare();

        }
    }

    // check if loading (packing list) has same bundles of the alternative packing list
    private class JSONTask3 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showDialog();

        }

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String JsonResponse = null;
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + databaseHandler.getSettings().getIpAddress() + "/export.php"));

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("GET_PLANNED_BUNDLE_INFO", orderNo.getText().toString()));//GET_PLANNED

//                Log.e("tagPlanned", " COMPARE_CONTENT " +plannedPLJObject.toString());
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
                Log.e("loadingorder", " JSONTask3 " + JsonResponse);

                JSONObject object = new JSONObject(JsonResponse);
                JSONArray array = object.getJSONArray("PLANNED");

                plannedList.clear();
                originalList.clear();
                for (int i = 0; i < array.length(); i++) {

                    JSONObject innerObject = array.getJSONObject(i);
//                    order = new Orders(bundles.get(i).getThickness()
//                            , bundles.get(i).getWidth()
//                            , bundles.get(i).getLength()
//                            , bundles.get(i).getGrade()
//                            , bundles.get(i).getNoOfPieces()
//                            , bundles.get(i).getBundleNo()
//                            , bundles.get(i).getLocation()
//                            , bundles.get(i).getArea()
//                            , placingNo.getText().toString()
//                            , orderNo.getText().toString()
//                            , containerNo.getText().toString()
//                            , dateOfLoad.getText().toString()
//                            , destination.getText().toString()
//                            , bundles.get(i).getPicture()
//                            , bundles.get(i).getBackingList()
//                            , bundles.get(i).getCustomer());
                    Orders orders = new Orders();
                    orders.setThickness(innerObject.getDouble("THICKNESS"));// check double
                    orders.setWidth(innerObject.getDouble("WIDTH"));
                    orders.setLength(innerObject.getDouble("LENGTH"));
                    orders.setGrade(innerObject.getString("GRADE"));
                    orders.setNoOfPieces(innerObject.getDouble("PIECES"));

                    orders.setBundleNo(innerObject.getString("BUNDLE_NO"));
                    orders.setLocation(innerObject.getString("LOCATION"));
                    orders.setArea(innerObject.getString("AREA"));
                    orders.setOrderNo(orderNo.getText().toString());
                    orders.setContainerNo(containerNo.getText().toString());
                    orders.setDateOfLoad(dateOfLoad.getText().toString());
                    orders.setDestination(destination.getText().toString());
//                    orders.setPicture(innerObject.getDouble("PIECES"));
                    orders.setPackingList(innerObject.getString("BACKING_LIST"));
                    orders.setCustomer(innerObject.getString("CUSTOMER"));
                    orders.setSerial(innerObject.getInt("B_SERIAL"));
                    orders.setRemove(false);

                    originalList.add(orders);
                    Log.e("order", "" + orders.getSerial());
                    Log.e("originalListSize", "" + originalList.size());
//                    PlannedPL pl = new PlannedPL();
//                    pl.setThickness(innerObject.getInt("THICKNESS"));
//                    pl.setWidth(innerObject.getDouble("WIDTH"));
//                    pl.setLength(innerObject.getDouble("LENGTH"));
//                    pl.setGrade(innerObject.getString("GRADE"));
//                    pl.setNoOfPieces(innerObject.getDouble("PIECES"));
//                    pl.setPackingList(innerObject.getString("BACKING_LIST"));
//                    pl.setCustName(innerObject.getString("CUSTOMER"));
//                    pl.setSerial(innerObject.getString("B_SERIAL"));

//                    plannedList.add(pl);
//                    jsonArrayMissedBundles.put(pl.getJSONObject());

                }

                //Log.e("****", "" + plannedList.size());

//                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
//                return null;
            }
            return JsonResponse;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("jsontask", s);
            if (s != null) {
                // remove found bundles in jsonArrayOrders (all orders i choosed) from packing list (order no)
//                for (int m = 0; m < checkDuplicate.size(); m++)
//                    for (int n = 0; n < plannedList.size(); n++)
//                        if (checkDuplicate.get(m).getThickness() == plannedList.get(n).getThickness() &&
//                                checkDuplicate.get(m).getWidth() == plannedList.get(n).getWidth() &&
//                                checkDuplicate.get(m).getLength() == plannedList.get(n).getLength() &&
//                                checkDuplicate.get(m).getNoOfPieces() == plannedList.get(n).getNoOfPieces() &&
//                                checkDuplicate.get(m).getGrade().equals(plannedList.get(n).getGrade())) {
//                            plannedList.remove(n);
//                            break;
//                        }
//                Log.e("loadingorder", "plannedList " + plannedList.size());
//
//                for (int i = 0; i < plannedList.size(); i++) {
//                    for (int k = 0; k < missedBundles.size(); k++) {
//
//                        if (plannedList.get(i).getThickness() == missedBundles.get(k).getThickness() &&
//                                plannedList.get(i).getWidth() == missedBundles.get(k).getWidth() &&
//                                plannedList.get(i).getLength() == missedBundles.get(k).getLength() &&
//                                plannedList.get(i).getNoOfPieces() == missedBundles.get(k).getNoOfPieces() &&
//                                plannedList.get(i).getGrade().equals(missedBundles.get(k).getGrade())) {
//
//                            //  if loading (packing list) has same bundles of the alternative
//                            // packing list it removed from list to make exchange in web service
//                            missedBundles.remove(k);
//                            jsonArrayMissedBundles.remove(k);
//                            break;
//                        }
//
//                    }
//                }
//                Log.e("loadingorder", "  missedBundles" + missedBundles.size());

//                Log.e("loadingorder", "  afterremove" + missedBundles.size());

                if (s.contains("PackingListDoesNotExist")) {
                    Toast.makeText(LoadingOrder2.this, "Packing list doesn't exist in the inventory!", Toast.LENGTH_SHORT).show();

                } else {
                    jsonArrayUnmatchedList = new JSONArray();
                    jsonArrayOrders = new JSONArray();
                    Log.e("originalListSize", "" + originalList.size());
                    if (originalList.size() != 0) {
                        for (int m = 0; m < originalList.size(); m++) {
                            for (int r = 0; r < checkedBundleList.size(); r++) {
                                if (originalList.get(m).getSerial() == checkedBundleList.get(r).getSerial()) {
                                    Log.e("loadingorder", " index " + m + "  " + r);
                                    originalList.get(m).setRemove(true);
                                    checkedBundleList.get(r).setRemove(true);
                                }

                            }

                        }

                        for (int k = 0; k < checkedBundleList.size(); k++) {
                            boolean found = false;
                            int newSerial = 0;
                            String newCustomer = "";
                            for (int i = 0; i < originalList.size(); i++) {
                                if (!originalList.get(i).isRemove() && !checkedBundleList.get(k).isRemove()) {

                                    Log.e("loadingorder", "  or " + originalList.get(i).getSerial() + "chec " + checkedBundleList.get(k).getSerial());
                                    Log.e("loadingorder", originalList.get(i).getThickness() + "==" + checkedBundleList.get(k).getThickness() + "  \n  "
                                            + originalList.get(i).getWidth() + "==" + checkedBundleList.get(k).getWidth() + " \n " +
                                            "   \n" + originalList.get(i).getLength() + "==" + checkedBundleList.get(k).getLength() + "   \n" +
                                            "   \n" + originalList.get(i).getNoOfPieces() + "==" + checkedBundleList.get(k).getNoOfPieces() + "   \n" +
                                            "   \n" + originalList.get(i).getGrade() + "==" + checkedBundleList.get(k).getGrade() + "   \n");

                                    if (originalList.get(i).getThickness() == checkedBundleList.get(k).getThickness())
                                        if (originalList.get(i).getWidth() == checkedBundleList.get(k).getWidth())
                                            if (originalList.get(i).getLength() == checkedBundleList.get(k).getLength())
                                                if (originalList.get(i).getNoOfPieces() == checkedBundleList.get(k).getNoOfPieces())
                                                    if (originalList.get(i).getGrade().equals(checkedBundleList.get(k).getGrade())) {
                                                        newSerial = originalList.get(i).getSerial();
                                                        newCustomer = originalList.get(i).getCustomer();

                                                        found = true;
                                                        originalList.get(i).setRemove(true);
                                                        checkedBundleList.get(k).setRemove(true);
                                                        break;
                                                    } else {
                                                        found = false;
                                                    }
                                } else {
                                    if (originalList.get(i).getSerial() == checkedBundleList.get(k).getSerial()) {

                                        Log.e("loadingorder", " news " + originalList.get(i).getSerial() + " cheq  " + checkedBundleList.get(k).getSerial());
                                        newSerial = originalList.get(i).getSerial();
                                        newCustomer = originalList.get(i).getCustomer();
                                        found = true;
                                        break;
                                    }

                                }
                            }
                            if (found) {
                                checkedBundleList.get(k).setNewSerial(newSerial);
                                checkedBundleList.get(k).setNewCustomer(newCustomer);
                                Log.e("loadingorderTex", "oldSerial:" + checkedBundleList.get(k).getSerial() + " newSerial: " + checkedBundleList.get(k).getNewSerial());
                                jsonArrayOrders.put(checkedBundleList.get(k).getJSONObject());
                            } else {
                                Log.e("loadingorder", "UnmatchedList oldSerial:" + checkedBundleList.get(k).getSerial() + " newSerial: " + checkedBundleList.get(k).getNewSerial());
                                checkedBundleList.get(k).setNewCustomer(originalList.get(0).getCustomer());
                                jsonArrayUnmatchedList.put(checkedBundleList.get(k).getJSONObject());
                            }
                        }
                        new JSONTask().execute();
                    }
                }
            } else {
                Toast.makeText(LoadingOrder2.this, "Check internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // send bundles for amman
    private class JSONTask4 extends AsyncTask<String, String, String> {

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
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));

                String newCust = "";
//                if(plannedList.size()>0)
//                    newCust = plannedList.get(0).getCustName();

                Log.e("loadingorder", " JSONTask " + jsonArrayOrders.length() + " missed: " + jsonArrayMissedBundles.length());

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("AMMAN_BUNDLE_ORDERS", jsonArrayOrders.toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("NEW_CUST", "'" + newCust + "'"));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_PIC", jsonArrayPics.toString().trim()));
                // nameValuePairs.add(new BasicNameValuePair("MISSED_BUNDLES", jsonArrayMissedBundles.toString().trim()));

                //Log.e("tag", "" + jsonArrayPics.toString());

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
                Log.e("ammanbundle_order", "" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("ammanbundle_order", "" + s);
//            progressDialog.dismiss();
            if (s != null) {
                if (s.contains("AMMAN_BUNDLE_ORDER SUCCESS")) {
                    pics.clear();
                    flagOpenJ=0;
                    Intent intent = new Intent(LoadingOrder2.this, LoadingOrder.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    Log.e("tag", "****Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }

    void compare() {

        // fill no of exist in choosed bundles
        for (int i = 0; i < bundles.size(); i++) {
            for (int k = 0; k < bundleInfosList.size(); k++) {

                if (bundles.get(i).getThickness() == bundleInfosList.get(k).getThickness() &&
                        bundles.get(i).getWidth() == bundleInfosList.get(k).getWidth() &&
                        bundles.get(i).getLength() == bundleInfosList.get(k).getLength() &&
                        bundles.get(i).getNoOfPieces() == bundleInfosList.get(k).getNoOfPieces() &&
                        bundles.get(i).getGrade().equals(bundleInfosList.get(k).getGrade())) {

                    //bundles.get(i).setExist("Exist");
                    bundles.get(i).setNoOfExist(bundleInfosList.get(k).getNoOfExist());

                    break;
                } else {
                    //bundles.get(i).setExist("Not Exist");
                    bundles.get(i).setNoOfExist(0);
                }

            }

        }

        // clustering
        List<BundleInfo> temp = new ArrayList(bundles);
        for (int i = 0; i < temp.size(); i++) {

            if (temp.get(i).getNoOfExist() > 0) {
                for (int k = i + 1; k < bundles.size(); k++) {

                    if (temp.get(i).getThickness() == bundles.get(k).getThickness() &&
                            temp.get(i).getWidth() == bundles.get(k).getWidth() &&
                            temp.get(i).getLength() == bundles.get(k).getLength() &&
                            temp.get(i).getNoOfPieces() == bundles.get(k).getNoOfPieces() &&
                            temp.get(i).getGrade().equals(bundles.get(k).getGrade())) {

                        if (bundles.get(k).getNoOfExist() > 0) {
                            bundles.get(k).setNoOfExist(bundles.get(k).getNoOfExist() - 1);
                        }

                    }

                }
            }
        }

        bundles = new ArrayList<>();
        bundles.addAll(temp);

        for (int i = 0; i < bundles.size(); i++) {
            Log.e("*****", " " + bundles.get(i).getNoOfExist());
        }

    }
}
