package com.falconssoft.woodysystem;

import android.Manifest;
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
import com.falconssoft.woodysystem.models.Settings;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

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

public class LoadingOrder2 extends AppCompatActivity {

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
    private Calendar myCalendar;
    static int index = 0;
    ItemsListAdapter2 adapter;
    int imageNo = 0;
    private Settings generalSettings;

//    BluetoothAdapter mBluetoothAdapter;
//    BluetoothSocket mmSocket;
//    BluetoothDevice mmDevice;
//    OutputStream mmOutputStream;
//    InputStream mmInputStream;
//    volatile boolean stopWorker;
//    String mainContent = "";
//    private boolean checkImageExist = false;

    JSONArray jsonArrayOrders;
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
        jsonArrayOrders = new JSONArray();
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

                                    Toast.makeText(LoadingOrder2.this, "Saved Successfully", Toast.LENGTH_SHORT).show();
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

        new Thread(new Runnable() {
            @Override
            public void run() {

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
                            , bundles.get(i).getPicture());
                    databaseHandler.addOrder(order);

//                    Log.e("**********", "" + bundles.get(i).getPicture().length());

                    jsonArrayOrders.put(order.getJSONObject());

                    databaseHandler.updateTableBundles(bundles.get(i).getBundleNo());
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

                databaseHandler.addPictures(picture);

                new JSONTask().execute();

                new Handler(Looper.getMainLooper()).post(new Runnable(){
                    @Override
                    public void run() {
                        searchBar.setText("2");
                    }
                });

                finish();

                progressDialog.dismiss();

            }
        }).start();

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

        if(pics.get(0) != null)
            img1.setImageBitmap(StringToBitMap(pics.get(0)));

        if(pics.get(1) != null)
        img2.setImageBitmap(StringToBitMap(pics.get(1)));

        if(pics.get(2) != null)
        img3.setImageBitmap(StringToBitMap(pics.get(2)));

        if(pics.get(3) != null)
        img4.setImageBitmap(StringToBitMap(pics.get(3)));

        if(pics.get(4) != null)
        img5.setImageBitmap(StringToBitMap(pics.get(4)));

        if(pics.get(5) != null)
        img6.setImageBitmap(StringToBitMap(pics.get(5)));

        if(pics.get(6) != null)
        img7.setImageBitmap(StringToBitMap(pics.get(6)));

        if(pics.get(7) != null)
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

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_ORDERS", jsonArrayOrders.toString().trim()));
                nameValuePairs.add(new BasicNameValuePair("BUNDLE_PIC", jsonArrayPics.toString().trim()));
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
                if (s.contains("BUNDLE_ORDER SUCCESS")) {
pics.clear();
                    Log.e("tag", "****Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                }
            } else {
                Log.e("tag", "****Failed to export data Please check internet connection");
            }
        }
    }
}
