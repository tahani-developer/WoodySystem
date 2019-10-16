package com.falconssoft.woodysystem;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class LoadingOrder2 extends AppCompatActivity {

    HorizontalListView listView;
    ListView listView2;
    private EditText placingNo, orderNo, containerNo, dateOfLoad, destination;
    private ImageView img1, img2, img3, img4, img5, img6, img7, img8;
    private Button done;
    private TextView textView;
    private Orders order;
    private DatabaseHandler databaseHandler;
    private List<BundleInfo> bundles;
    private Calendar myCalendar;
    static int index = 0;
    ItemsListAdapter2 adapter;
    int imageNo = 0;

    static ArrayList<Bitmap> pics = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order2);

        init();
        databaseHandler = new DatabaseHandler(this);

        Drawable myDrawable = getResources().getDrawable(R.drawable.pic);
        Bitmap myBitmap = ((BitmapDrawable) myDrawable).getBitmap();

        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);
        pics.add(myBitmap);

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
                                                , destination.getText().toString());
                                        databaseHandler.addOrder(order);
                                    }

                                    databaseHandler.addPictures(new Pictures(orderNo.getText().toString()
                                            , pics.get(0)
                                            , pics.get(1)
                                            , pics.get(2)
                                            , pics.get(3)
                                            , pics.get(4)
                                            , pics.get(5)
                                            , pics.get(6)
                                            , pics.get(7)));

                                    Toast.makeText(LoadingOrder2.this, "Saved !", Toast.LENGTH_LONG).show();

                                    placingNo.setText("");
                                    orderNo.setText("");
                                    containerNo.setText("");
                                    dateOfLoad.setText("");
                                    destination.setText("");

//                                    for(int i = 0 ; i<pics.size() ; i++)
//                                        pics.set(i,null);
//
//                                    onResume();
                                    printReport();
                                    onCreate(savedInstanceState);

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


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera(int i) {
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

        if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
            final Bundle extras = data.getExtras();
            if (extras != null) {

                Bitmap pic = extras.getParcelable("data");


                if (index != -1) {
                    bundles.get(index).setPicture(pic);
                    adapter.notifyDataSetChanged();
                } else {
                    switch (imageNo) {
                        case 1:
                            img1.setImageBitmap(pic);
                            pics.set(0, pic);
                            break;
                        case 2:
                            img2.setImageBitmap(pic);
                            pics.set(1, pic);
                            break;
                        case 3:
                            img3.setImageBitmap(pic);
                            pics.set(2, pic);
                            break;
                        case 4:
                            img4.setImageBitmap(pic);
                            pics.set(3, pic);
                            break;
                        case 5:
                            img5.setImageBitmap(pic);
                            pics.set(4, pic);
                            break;
                        case 6:
                            img6.setImageBitmap(pic);
                            pics.set(5, pic);
                            break;
                        case 7:
                            img7.setImageBitmap(pic);
                            pics.set(6, pic);
                            break;
                        case 8:
                            img8.setImageBitmap(pic);
                            pics.set(7, pic);
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

    void printReport(){

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.e("****** ", "onResume");
        img1.setImageBitmap(pics.get(0));
        img2.setImageBitmap(pics.get(1));
        img3.setImageBitmap(pics.get(2));
        img4.setImageBitmap(pics.get(3));
        img5.setImageBitmap(pics.get(4));
        img6.setImageBitmap(pics.get(5));
        img7.setImageBitmap(pics.get(6));
        img8.setImageBitmap(pics.get(7));
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
        super.onBackPressed();
        Intent intent = new Intent(LoadingOrder2.this, LoadingOrder.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void finish() {
        super.finish();
//        setSlideAnimation();
        overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }
}
