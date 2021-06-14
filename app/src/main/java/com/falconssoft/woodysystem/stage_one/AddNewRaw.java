package com.falconssoft.woodysystem.stage_one;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.creativityapps.gmailbackgroundlibrary.BackgroundMail;
import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.ExportToExcel;
import com.falconssoft.woodysystem.ExportToPDF;
import com.falconssoft.woodysystem.MainActivity;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.BundlesReport;
import com.google.gson.Gson;

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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_FLAG;
import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_LIST;
import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_RAW;
import static com.falconssoft.woodysystem.reports.AcceptanceReport.EDIT_LIST2;
import static com.falconssoft.woodysystem.reports.AcceptanceReport.EDIT_RAW2;

public class AddNewRaw extends AppCompatActivity implements View.OnClickListener {

    private boolean mState = false, isEditImage = false;
    private final String STATE_VISIBILITY = "state-visibility";
    private Settings generalSettings;
    private WoodPresenter presenter;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12, image13, image14, image15;
    private TextView addNewSupplier, searchSupplier, addButton, acceptRowButton, mainInfoButton, acceptanceDate, addPicture, totalRejected, totalBundles, total, addImageGalary;
    private EditText thickness, width, length, noOfPieces, noOfBundles, noOfRejected, truckNo, acceptor, ttnNo;
    private Spinner gradeSpinner, acceptanceLocation;
    private ArrayList<String> gradeList = new ArrayList<>();
    private ArrayList<String> locationList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter, locationAdapter;
    private List<NewRowInfo> newRowList = new ArrayList<>();
    private List<NewRowInfo> editList = new ArrayList<>();
    private String gradeText = "KD", locationText = "Kalinovka";
    public static String supplierName = "";
    private LinearLayout headerLayout, acceptRowLayout;
    private Button doneAcceptRow, excelAcceptRow, pdfAcceptRow, clear, sendEmailB;
    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private Dialog searchDialog;
    NewRowInfo newRowInfoMaster;
    List<Bitmap> imageBitmapList;
    NewRowInfo newRowInfoPic;


    private DatabaseHandler databaseHandler;
    private List<SupplierInfo> suppliers;
    private JSONObject masterData;
    private JSONArray jsonArray;
    private RelativeLayout coordinatorLayout;
    private Snackbar snackbar;
    private SuppliersAdapter adapter;
    private RecyclerView recyclerView;
    private Calendar myCalendar;
    private int imageNo = 0, editImageNo = 0;
    private List<String> imagesList = new ArrayList<>();
    private List<Bitmap> bitmapImagesList = new ArrayList<>();
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<SupplierInfo> arraylist;
    private static boolean isCamera = false;
    private int edieFlag = 0;
    private int netBundlesString = 0, netRejectedString = 0;
    private ProgressDialog progressDialog, proTTn;
    public static String truckNoBeforeUpdate = "";
    public static String serialBeforeUpdate = "";

    private NewRowInfo oldNewRowInfo, updatedNewRowInfo;
    private String oldTruck = "", editSerial = "";// for edit
    //    Bitmap serverPicBitmap;
    private String mCameraFileName, path, pathImage;
    //    ImageView imageView;
    private Uri image;
    private Process process;
    private Bitmap bitmap;
    private String thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal;
    private Uri imageUri;
    int id = 0;
    int update = 0, index = 0;
    TableRow tableRowEdit;
    TextView supplierTextTemp = null, getTtNo;
    String myFormat;
    private SimpleDateFormat sdf;
    Dialog passwordDialog;
    int PICK_IMAGE_MULTIPLE = 1;
    List<NewRowInfo> listOfEmail = new ArrayList<>();
    int flagIsGet = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_add_new_raw);
        } catch (Exception e) {
            Log.e("exceptaionMaster", "112");
        }

        databaseHandler = new DatabaseHandler(AddNewRaw.this);
        suppliers = new ArrayList<>();
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        presenter = new WoodPresenter(this);
        this.arraylist = new ArrayList<>();
//        this.arraylist.addAll(this.supplierInfoList);
        myFormat = "dd/MM/yyyy";
        newRowInfoMaster = new NewRowInfo();
        imageBitmapList = new ArrayList<>();
        sdf = new SimpleDateFormat(myFormat, Locale.US);
        coordinatorLayout = findViewById(R.id.addNewRow_coordinator);
        addNewSupplier = findViewById(R.id.addNewRaw_add_supplier);
        addImageGalary = findViewById(R.id.addNewRaw_get_photo);
        searchSupplier = findViewById(R.id.addNewRaw_search_supplier);
        thickness = findViewById(R.id.addNewRaw_thickness);
        width = findViewById(R.id.addNewRaw_width);
        length = findViewById(R.id.addNewRaw_length);
        noOfPieces = findViewById(R.id.addNewRaw_no_of_pieces);
        noOfBundles = findViewById(R.id.addNewRaw_no_of_bundles);
        noOfRejected = findViewById(R.id.addNewRaw_no_of_rejected);
        gradeSpinner = findViewById(R.id.addNewRaw_grade);
        addButton = findViewById(R.id.addNewRaw_add_button);
        acceptRowButton = findViewById(R.id.addNewRaw_acceptRaw_button);
        headerLayout = findViewById(R.id.addNewRaw_linearLayoutHeader);
        acceptRowLayout = findViewById(R.id.addNewRaw_acceptRow_linear);
        mainInfoButton = findViewById(R.id.addNewRaw_acceptRow_back);
        tableLayout = findViewById(R.id.addNewRaw_table);
        headerTableLayout = findViewById(R.id.addNewRaw_table_header);
        truckNo = findViewById(R.id.addNewRaw_truckNo);
        acceptanceDate = findViewById(R.id.addNewRaw_acceptance_date);
        acceptor = findViewById(R.id.addNewRaw_acceptor);
        acceptanceLocation = findViewById(R.id.addNewRaw_acceptance_location);
        ttnNo = findViewById(R.id.addNewRaw_ttn_no);
        totalRejected = findViewById(R.id.addNewRaw_total_rejected);
        totalBundles = findViewById(R.id.addNewRaw_total_bundles);
        doneAcceptRow = findViewById(R.id.addNewRaw_acceptRow_done);
        excelAcceptRow = findViewById(R.id.addNewRaw_acceptRow_excel);
        pdfAcceptRow = findViewById(R.id.addNewRaw_acceptRow_pdf);
        addPicture = findViewById(R.id.addNewRaw_add_photo);
        image1 = findViewById(R.id.addNewRaw_image1);
        image2 = findViewById(R.id.addNewRaw_image2);
        image3 = findViewById(R.id.addNewRaw_image3);
        image4 = findViewById(R.id.addNewRaw_image4);
        image5 = findViewById(R.id.addNewRaw_image5);
        image6 = findViewById(R.id.addNewRaw_image6);
        image7 = findViewById(R.id.addNewRaw_image7);
        image8 = findViewById(R.id.addNewRaw_image8);

        image9 = findViewById(R.id.addNewRaw_image9);
        image10 = findViewById(R.id.addNewRaw_image10);
        image11 = findViewById(R.id.addNewRaw_image11);
        image12 = findViewById(R.id.addNewRaw_image12);
        image13 = findViewById(R.id.addNewRaw_image13);
        image14 = findViewById(R.id.addNewRaw_image14);
        image15 = findViewById(R.id.addNewRaw_image15);
        getTtNo = findViewById(R.id.addNewRaw_get_data);
        clear = findViewById(R.id.addNewRaw_acceptRow_clear);
        sendEmailB = findViewById(R.id.addNewRaw_acceptRow_Email);


        thickness.requestFocus();
        headerLayout.setVisibility(View.VISIBLE);
        acceptRowLayout.setVisibility(View.VISIBLE);
        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
        image4.setVisibility(View.INVISIBLE);
        image5.setVisibility(View.INVISIBLE);
        image6.setVisibility(View.INVISIBLE);
        image7.setVisibility(View.INVISIBLE);
        image8.setVisibility(View.INVISIBLE);

        image9.setVisibility(View.INVISIBLE);
        image10.setVisibility(View.INVISIBLE);
        image11.setVisibility(View.INVISIBLE);
        image12.setVisibility(View.INVISIBLE);
        image13.setVisibility(View.INVISIBLE);
        image14.setVisibility(View.INVISIBLE);
        image15.setVisibility(View.INVISIBLE);
        acceptanceDate.setText(sdf.format(myCalendar.getTime()));
        gradeList.clear();
        gradeList.add("KD");
        gradeList.add("Fresh");
        gradeList.add("Blue Stain");
        gradeList.add("Reject");
//        gradeList.add("KD Blue Stain");
//        gradeList.add("AST");
//        gradeList.add("AST Blue Stain");
//        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
        gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                switch (parent.getId()) {
//                    case R.id.addNewRaw_grade:
                gradeText = parent.getItemAtPosition(position).toString();
//                        break;
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        clear.setOnClickListener(this);
        addPicture.setOnClickListener(this);
        mainInfoButton.setOnClickListener(this);
        doneAcceptRow.setOnClickListener(this);
        excelAcceptRow.setOnClickListener(this);
        pdfAcceptRow.setOnClickListener(this);
        acceptRowButton.setOnClickListener(this);
        addImageGalary.setOnClickListener(this);
        addNewSupplier.setOnClickListener(this);
        searchSupplier.setOnClickListener(this);
        addButton.setOnClickListener(this);
        acceptanceDate.setOnClickListener(this);
        image1.setOnClickListener(this);
        image2.setOnClickListener(this);
        image3.setOnClickListener(this);
        image4.setOnClickListener(this);
        image5.setOnClickListener(this);
        image6.setOnClickListener(this);
        image7.setOnClickListener(this);
        image8.setOnClickListener(this);

        image9.setOnClickListener(this);
        image10.setOnClickListener(this);
        image11.setOnClickListener(this);
        image12.setOnClickListener(this);
        image13.setOnClickListener(this);
        image14.setOnClickListener(this);
        image15.setOnClickListener(this);
        getTtNo.setOnClickListener(this);
        sendEmailB.setOnClickListener(this);

        imagesList.add(0, null);
        imagesList.add(1, null);
        imagesList.add(2, null);
        imagesList.add(3, null);
        imagesList.add(4, null);
        imagesList.add(5, null);
        imagesList.add(6, null);
        imagesList.add(7, null);

        bitmapImagesList.add(0, null);
        bitmapImagesList.add(1, null);
        bitmapImagesList.add(2, null);
        bitmapImagesList.add(3, null);
        bitmapImagesList.add(4, null);
        bitmapImagesList.add(5, null);
        bitmapImagesList.add(6, null);
        bitmapImagesList.add(7, null);


        imageBitmapList.add(0, null);
        imageBitmapList.add(1, null);
        imageBitmapList.add(2, null);
        imageBitmapList.add(3, null);
        imageBitmapList.add(4, null);
        imageBitmapList.add(5, null);
        imageBitmapList.add(6, null);
        imageBitmapList.add(7, null);

        imageBitmapList.add(8, null);
        imageBitmapList.add(9, null);
        imageBitmapList.add(10, null);
        imageBitmapList.add(11, null);
        imageBitmapList.add(12, null);
        imageBitmapList.add(13, null);
        imageBitmapList.add(14, null);

        checkIfEditItem();

        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    void checkIfEditItem() {
        edieFlag = getIntent().getIntExtra(EDIT_FLAG, 0);
        Log.e("flag", "" + edieFlag);
        if (edieFlag == 10) {// from Acceptance Report 2 // sons
            Bundle bundle = getIntent().getExtras();
            NewRowInfo rowInfo = (NewRowInfo) bundle.getSerializable(EDIT_RAW);
            editList = (List<NewRowInfo>) bundle.getSerializable(EDIT_LIST);
            // todo
            Log.e("serializable", "" + rowInfo.getSerial());

            addNewSupplier.setVisibility(View.INVISIBLE);
//            searchSupplier.setClickable(false);
            oldTruck = rowInfo.getTruckNo();
            editSerial = rowInfo.getSerial();
            thickness.setText("" + (int) rowInfo.getThickness());
            width.setText("" + (int) rowInfo.getWidth());
            length.setText("" + (int) rowInfo.getLength());
            noOfPieces.setText("" + (int) rowInfo.getNoOfPieces());
            noOfRejected.setText("" + (int) rowInfo.getNoOfRejected());
            noOfBundles.setText("" + (int) rowInfo.getNoOfBundles());
            int position = gradeAdapter.getPosition(rowInfo.getGrade());
            gradeSpinner.setSelection(position);
            searchSupplier.setText(rowInfo.getSupplierName());
            supplierName = searchSupplier.getText().toString();
            truckNo.setText(rowInfo.getTruckNo());
            acceptor.setText(rowInfo.getAcceptedPersonName());
            ttnNo.setText(rowInfo.getTtnNo());
            totalBundles.setText(rowInfo.getNetBundles());
//            acceptanceLocation.setText(rowInfo.getLocationOfAcceptance());
            acceptanceDate.setText(rowInfo.getDate());
            totalRejected.setText(rowInfo.getTotalRejectedNo());

        } else if (edieFlag == 11) { // from accepted truck report

            Bundle bundle = getIntent().getExtras();
            NewRowInfo rowInfo = (NewRowInfo) bundle.getSerializable(EDIT_RAW2);
            editList = (List<NewRowInfo>) bundle.getSerializable(EDIT_LIST2);
            editSerial = rowInfo.getSerial();
            oldTruck = rowInfo.getTruckNo();
            oldNewRowInfo = new NewRowInfo();
            updatedNewRowInfo = new NewRowInfo();
            addNewSupplier.setVisibility(View.INVISIBLE);
//            searchSupplier.setClickable(false);
            truckNo.setText(rowInfo.getTruckNo());
            acceptor.setText(rowInfo.getAcceptedPersonName());
            ttnNo.setText(rowInfo.getTtnNo());
            totalBundles.setText(rowInfo.getNetBundles());
            locationText = rowInfo.getLocationOfAcceptance();
            acceptanceDate.setText(rowInfo.getDate());
            totalRejected.setText(rowInfo.getTotalRejectedNo());

            locationList.clear();
            locationList.add("Kalinovka");
            locationList.add("Rudniya Store");
            if (!rowInfo.getLocationOfAcceptance().equals("Kalinovka") && !rowInfo.getLocationOfAcceptance().equals("Rudniya Store"))
                locationList.add(rowInfo.getLocationOfAcceptance());
            locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
            locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
            acceptanceLocation.setAdapter(locationAdapter);
//            int pos = locationAdapter.getPosition(locationText);
            acceptanceLocation.setSelection(locationAdapter.getPosition(locationText));
            acceptanceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    locationText = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            addTableHeader(headerTableLayout);
            fillDataFromReport1();
        } else {
            locationList.clear();
            locationList.add("Kalinovka");
            locationList.add("Rudniya Store");
            locationAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, locationList);
            locationAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
            acceptanceLocation.setAdapter(locationAdapter);
            acceptanceLocation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    locationText = parent.getItemAtPosition(position).toString();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }

    private void fillDataFromReport1() {
        for (int i = 0; i < editList.size(); i++) {
            tableRow = new TableRow(this);
            gradeText = editList.get(i).getGrade();
            supplierName = editList.get(i).getSupplierName();
            fillTableRow(tableRow, "" + (int) editList.get(i).getThickness(), "" + (int) editList.get(i).getWidth()
                    , "" + (int) editList.get(i).getLength(), "" + (int) editList.get(i).getNoOfPieces()
                    , "" + (int) editList.get(i).getNoOfRejected(), "" + (int) editList.get(i).getNoOfBundles(), gradeText);
            tableLayout.addView(tableRow);

            int finalI = i;
//            tableRow.getChildAt(8).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    supplierName = editList.get(finalI).getSupplierName();
//
//                    oldNewRowInfo.setThickness(editList.get(finalI).getThickness());
//                    oldNewRowInfo.setWidth(editList.get(finalI).getWidth());
//                    oldNewRowInfo.setLength(editList.get(finalI).getLength());
//                    oldNewRowInfo.setNoOfPieces(editList.get(finalI).getNoOfPieces());
//                    oldNewRowInfo.setNoOfRejected(editList.get(finalI).getNoOfRejected());
//                    oldNewRowInfo.setNoOfBundles(editList.get(finalI).getNoOfBundles());
//                    oldNewRowInfo.setGrade(editList.get(finalI).getGrade());
//                    oldNewRowInfo.setSerial(editList.get(finalI).getSerial());
//
//                    thickness.setText("" + (int) editList.get(finalI).getThickness());
//                    width.setText("" + (int) editList.get(finalI).getWidth());
//                    length.setText("" + (int) editList.get(finalI).getLength());
//                    noOfPieces.setText("" + (int) editList.get(finalI).getNoOfPieces());
//                    noOfRejected.setText("" + (int) editList.get(finalI).getNoOfRejected());
//                    noOfBundles.setText("" + (int) editList.get(finalI).getNoOfBundles());
//
//                    int position = gradeAdapter.getPosition(editList.get(finalI).getGrade());
//                    gradeSpinner.setSelection(position);
//                }
//            });

//            tableRow.getChildAt(8).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Log.e("delete", "" + finalI + editList.get(finalI).getThickness()
//                            + "/" + editList.get(finalI).getWidth()
//                            + "/" + editList.get(finalI).getLength()
//                            + "/" + editList.get(finalI).getNoOfPieces()
//                    );
//                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRaw.this);
//                    builder.setMessage("Are you want delete this row?");
//                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();
//                            editList.remove(finalI);
//                            fillDataFromReport1();
//                        }
//                    });
//                    builder.show();
//                }
//            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.addNewRaw_acceptRow_back:
                acceptRowLayout.setVisibility(View.VISIBLE);
                acceptRowButton.setBackgroundResource(R.drawable.frame_shape_2);
                mainInfoButton.setBackgroundResource(R.drawable.frame_shape_3);

                //Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                //headerLayout.setVisibility(View.VISIBLE);
                //headerLayout.startAnimation(animation1);
                thickness.requestFocus();
                break;
            case R.id.addNewRaw_acceptRow_done:
                if (flagIsGet == 0) {
                    doneButtonMethod();
                } else {
                    Toast.makeText(this, "This For Email Can Not Edit", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.addNewRaw_acceptance_date:
                new DatePickerDialog(AddNewRaw.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.addNewRaw_acceptRaw_button:
                // headerLayout.setVisibility(View.VISIBLE);
                rejectAdd();
                break;
            case R.id.addNewRaw_add_button:
//                if(update==0){

                if (flagIsGet == 0) {
                    addButtonMethod(-1, 0);
                    rejectAdd();
                } else {
                    Toast.makeText(this, "This For Email Can Not Edit", Toast.LENGTH_SHORT).show();

                }

//                }else if(update==1){
//                    updateFlag();
//                    rejectAdd();
//                }
                break;
            case R.id.addNewRaw_add_photo:
                if (flagIsGet == 0) {
                    openCamera();
                } else {
                    Toast.makeText(this, "This For Email Can Not Edit", Toast.LENGTH_SHORT).show();
                }
//                cameraIntent();
                break;
            case R.id.addNewRaw_add_supplier:

                showPasswordDialog();

                break;
            case R.id.addNewRaw_search_supplier:
                suppliers.clear();
                isCamera = false;
                new JSONTask().execute();

                searchDialog = new Dialog(this);
                searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                searchDialog.setContentView(R.layout.search_supplier_dialog);
                searchDialog.setCancelable(false);

                SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
                TextView close = searchDialog.findViewById(R.id.search_supplier_close);
                total = searchDialog.findViewById(R.id.total_suppliers);

                recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new SuppliersAdapter(this, suppliers, null, null, 0);
                recyclerView.setAdapter(adapter);

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        filter(newText);
//                        adapter.notifyDataSetChanged();
                        return false;
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        searchDialog.dismiss();
                        isCamera = false;
                    }
                });
                searchDialog.show();
                break;
            case R.id.addNewRaw_image1:
                isEditImage = true;
                editImageNo = 1;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image2:
                isEditImage = true;
                editImageNo = 2;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image3:
                isEditImage = true;
                editImageNo = 3;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image4:
                isEditImage = true;
                editImageNo = 4;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image5:
                isEditImage = true;
                editImageNo = 5;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image6:
                isEditImage = true;
                editImageNo = 6;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image7:
                isEditImage = true;
                editImageNo = 7;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image8:
                isEditImage = true;
                editImageNo = 8;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image9:
                isEditImage = true;
                editImageNo = 9;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image10:
                isEditImage = true;
                editImageNo = 10;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image11:
                isEditImage = true;
                editImageNo = 11;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image12:
                isEditImage = true;
                editImageNo = 12;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image13:
                isEditImage = true;
                editImageNo = 13;
//                cameraIntent();
                openCamera();
                break;

            case R.id.addNewRaw_image14:
                isEditImage = true;
                editImageNo = 14;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_image15:
                isEditImage = true;
                editImageNo = 15;
//                cameraIntent();
                openCamera();
                break;
            case R.id.addNewRaw_acceptRow_pdf:
                if (!truckNo.getText().toString().equals("")) {
                    if (!acceptor.getText().toString().equals("")) {
                        if (!ttnNo.getText().toString().equals("")) {
                            if (!acceptanceDate.getText().toString().equals("")) {
                                String truckNoLocal = truckNo.getText().toString();
                                String acceptorLocal = acceptor.getText().toString();
                                String ttnNoLocal = ttnNo.getText().toString();
                                String acceptanceDateLocal = acceptanceDate.getText().toString();

                                newRowInfoMaster.setTruckNo(truckNoLocal);
                                newRowInfoMaster.setAcceptedPersonName(acceptorLocal);
                                newRowInfoMaster.setTtnNo(ttnNoLocal);
                                newRowInfoMaster.setNetBundles("" + netBundlesString);
                                newRowInfoMaster.setDate(acceptanceDateLocal);
                                newRowInfoMaster.setLocationOfAcceptance(locationText);
                                newRowInfoMaster.setTotalRejectedNo("" + netRejectedString);
                                try {
                                    createPdf();
                                } catch (Exception e) {
                                    Log.e("error_711", "createPdf");
                                }
                            } else {
                                acceptanceDate.setError("Requierd!");
                            }
                        } else {
                            ttnNo.setError("Requierd!");
                        }
                    } else {
                        acceptor.setError("Requierd!");
                    }
                } else {
                    truckNo.setError("Requierd!");
                }
                break;
            case R.id.addNewRaw_acceptRow_excel:

                if (!truckNo.getText().toString().equals("")) {
                    if (!acceptor.getText().toString().equals("")) {
                        if (!ttnNo.getText().toString().equals("")) {
                            if (!acceptanceDate.getText().toString().equals("")) {
                                String truckNoLocal = truckNo.getText().toString();
                                String acceptorLocal = acceptor.getText().toString();
                                String ttnNoLocal = ttnNo.getText().toString();
                                String acceptanceDateLocal = acceptanceDate.getText().toString();

                                newRowInfoMaster.setTruckNo(truckNoLocal);
                                newRowInfoMaster.setAcceptedPersonName(acceptorLocal);
                                newRowInfoMaster.setTtnNo(ttnNoLocal);
                                newRowInfoMaster.setNetBundles("" + netBundlesString);
                                newRowInfoMaster.setDate(acceptanceDateLocal);
                                newRowInfoMaster.setLocationOfAcceptance(locationText);
                                newRowInfoMaster.setTotalRejectedNo("" + netRejectedString);
                                try {
                                    createExcel();
                                } catch (Exception e) {
                                    Log.e("error_711", "createExcel");
                                }
                            } else {
                                acceptanceDate.setError("Requierd!");
                            }
                        } else {
                            ttnNo.setError("Requierd!");
                        }
                    } else {
                        acceptor.setError("Requierd!");
                    }
                } else {
                    truckNo.setError("Requierd!");
                }

                break;
            case R.id.addNewRaw_get_photo:
                if (flagIsGet == 0) {
                    getPicFromGallery();
                } else {
                    Toast.makeText(this, "This For Email Can Not Edit", Toast.LENGTH_SHORT).show();

                }
                break;
            case R.id.addNewRaw_get_data:
//                if(!ttnNo.getText().toString().equals("")) {

                AlertDialog.Builder builderGet = new AlertDialog.Builder(AddNewRaw.this);
                builderGet.setMessage("are you sure ,you want delete every thing?");
                builderGet.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();

                        new JSONTaskTTN().execute();
                    }
                });
                builderGet.show();

//                }else {
//                    ttnNo.setError("Requierd !");
//                }
                break;
            case R.id.addNewRaw_acceptRow_clear:


                AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRaw.this);
                builder.setMessage("Are you want Clear all ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();

                        clear();
                    }
                });
                builder.show();

                break;
            case R.id.addNewRaw_acceptRow_Email:

                if (listOfEmail.size() != 0) {
                    ExportToPDF obj = new ExportToPDF(AddNewRaw.this);
                    obj.exportTruckAcceptanceSendEmail(listOfEmail, sdf.format(myCalendar.getTime()));
                    if (newRowInfoPic != null) {
                        fillImageBitmap(newRowInfoPic);
                    }

                    sendEmailDialog();
                } else {
                    Toast.makeText(this, "no Data ", Toast.LENGTH_SHORT).show();
                }


                break;
        }

    }

    public void clear() {
//        AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRaw.this);
//        builder.setMessage("Are you want Clear all ?");
//        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
////                            tableLayout.removeAllViews();

        flagIsGet = 0;
        listOfEmail.clear();
        tableLayout.removeAllViews();
        headerTableLayout.removeAllViews();
        ttnNo.setText("");
        try {
            newRowInfoPic.setPic11(null);
            newRowInfoPic.setPic22(null);
            newRowInfoPic.setPic33(null);
            newRowInfoPic.setPic44(null);
            newRowInfoPic.setPic55(null);
            newRowInfoPic.setPic66(null);
            newRowInfoPic.setPic77(null);
            newRowInfoPic.setPic88(null);
            newRowInfoPic.setPic99(null);
            newRowInfoPic.setPic1010(null);
            newRowInfoPic.setPic1111(null);
            newRowInfoPic.setPic1212(null);
            newRowInfoPic.setPic1313(null);
            newRowInfoPic.setPic1414(null);
            newRowInfoPic.setPic1515(null);
        }catch (Exception e)
        {}

        image1.setVisibility(View.INVISIBLE);
        image2.setVisibility(View.INVISIBLE);
        image3.setVisibility(View.INVISIBLE);
        image4.setVisibility(View.INVISIBLE);
        image5.setVisibility(View.INVISIBLE);
        image6.setVisibility(View.INVISIBLE);
        image7.setVisibility(View.INVISIBLE);
        image8.setVisibility(View.INVISIBLE);

        image9.setVisibility(View.INVISIBLE);
        image10.setVisibility(View.INVISIBLE);
        image11.setVisibility(View.INVISIBLE);
        image12.setVisibility(View.INVISIBLE);
        image13.setVisibility(View.INVISIBLE);
        image14.setVisibility(View.INVISIBLE);
        image15.setVisibility(View.INVISIBLE);


        try {
            fillImagesFromEmail();
        }catch (Exception e){

        }
        rejectAdd();
//            }
//        });
//        builder.show();

    }

    private void getPicFromGallery() {

        try {
            Intent intent = new Intent();

            // setting type to select to be image
            intent.setType("image/*");

            // allowing multiple image to be selected
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_MULTIPLE);
        } catch (Exception e) {
            Log.e("ExcIntentG", "Gallery");
        }

    }

    void showPasswordDialog() {
        passwordDialog = new Dialog(this);
        passwordDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        passwordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        passwordDialog.setContentView(R.layout.password_dialog);

        TextInputEditText password = passwordDialog.findViewById(R.id.password_dialog_password);
        TextView done = passwordDialog.findViewById(R.id.password_dialog_done);

        done.setText(getResources().getString(R.string.done));

        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (password.getText().toString().equals("3030111")) {
                    passwordDialog.dismiss();
                    Intent intent = new Intent(AddNewRaw.this, AddNewSupplier.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                } else
                    Toast.makeText(AddNewRaw.this, "Password is not correct!", Toast.LENGTH_SHORT).show();

            }
        });

        passwordDialog.show();
    }

    void createPdf() {
        if (newRowList.size() != 0) {
            ExportToPDF obj = new ExportToPDF(AddNewRaw.this);
            obj.exportTruckAcceptance(newRowList, newRowInfoMaster, sdf.format(myCalendar.getTime()));
        } else {
            Toast.makeText(this, "no Data ", Toast.LENGTH_SHORT).show();
        }
    }

    void createExcel() {
//        try {
        if (newRowList.size() != 0) {
            ExportToExcel.getInstance().createExcelFile(AddNewRaw.this, "Acceptance_Report_2.xls", 8, (List<?>) newRowInfoMaster, null);
        } else {
            Toast.makeText(this, "no Data ", Toast.LENGTH_SHORT).show();
        }
//        }catch (Exception e){
//            Log.e("dataError","Acc");
//        }
    }

    void rejectAdd() {
        acceptRowButton.setBackgroundResource(R.drawable.frame_shape_3);
        mainInfoButton.setBackgroundResource(R.drawable.frame_shape_2);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        // acceptRowLayout.setVisibility(View.VISIBLE);
        // acceptRowLayout.startAnimation(animation);
        truckNo.requestFocus();

        netRejectedString = 0;
        netBundlesString = 0;
        Log.e("fromedit11", "" + editList.size());
        if (flagIsGet == 0) {
            if (edieFlag == 11)
                for (int n = 0; n < editList.size(); n++) {
                    netRejectedString += editList.get(n).getNoOfRejected();
                    netBundlesString += editList.get(n).getNoOfBundles();
                }
            else if (edieFlag == 10) ;
            else
                for (int n = 0; n < newRowList.size(); n++) {
                    netRejectedString += newRowList.get(n).getNoOfRejected();
                    netBundlesString += newRowList.get(n).getNoOfBundles();
                }
        } else {
            for (int n = 0; n < listOfEmail.size(); n++) {
                netRejectedString += listOfEmail.get(n).getNoOfRejected();
                netBundlesString += listOfEmail.get(n).getNoOfBundles();
            }
        }

        totalRejected.setText("" + netRejectedString);
        totalBundles.setText("" + netBundlesString);
    }

    void updateFlag(NewRowInfo newRowInfo, int index) {
        Log.e("aaaa1", " " + newRowList.size() + "     " + index);
        newRowList.remove(index);
        tableLayout.removeViewAt(index);
        newRowList.add(index, newRowInfo);
        tableRow = new TableRow(this);
        fillTableRow(tableRow, "" + newRowInfo.getThickness(), "" + newRowInfo.getWidth(), "" + newRowInfo.getLength()
                , "" + newRowInfo.getNoOfPieces(), "" + newRowInfo.getNoOfRejected(), "" + newRowInfo.getNoOfBundles(), newRowInfo.getGrade());
        tableLayout.addView(tableRow, index);


        for (int i = 0; i < tableLayout.getChildCount(); i++) {

            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            ImageView imageViewEdit = (ImageView) tableRow.getChildAt(8);
            ImageView imageViewDelete = (ImageView) tableRow.getChildAt(9);
            imageViewEdit.setTag(i);
            imageViewDelete.setTag(i);
            tableRow.setTag(i);
        }

        Log.e("aaaa", " " + newRowList.size());


    }

    void deleteFlag() {
        newRowList.remove(index);
        tableLayout.removeViewAt(index);

        for (int i = 0; i < tableLayout.getChildCount(); i++) {

            TableRow tableRow = (TableRow) tableLayout.getChildAt(i);
            ImageView imageViewEdit = (ImageView) tableRow.getChildAt(8);
            ImageView imageViewDelete = (ImageView) tableRow.getChildAt(9);
            imageViewEdit.setTag(i);
            imageViewDelete.setTag(i);
            tableRow.setTag(i);
        }

        Log.e("aaaa", " " + newRowList.size());


    }

    public void filter(String charText) { // by Name
        charText = charText.toLowerCase(Locale.getDefault());
        arraylist.clear();
        if (charText.length() == 0) {
            arraylist.addAll(suppliers);
        } else {
            for (SupplierInfo supplierInfo : suppliers) {//for (SupplierInfo supplierInfo : arraylist){
                if (supplierInfo.getSupplierName().toLowerCase(Locale.getDefault()).contains(charText)) {
                    arraylist.add(supplierInfo);
                }
            }
        }
        adapter = new SuppliersAdapter(this, arraylist, null, null, 0);
        recyclerView.setAdapter(adapter);
    }

    void addButtonMethod(int indexs, int flag) {
        thicknessLocal = thickness.getText().toString();
        widthLocal = width.getText().toString();
        lengthLocal = length.getText().toString();
        noOfPiecesLocal = noOfPieces.getText().toString();
        noOfRejectedLocal = noOfRejected.getText().toString();
        noOfBundlesLocal = noOfBundles.getText().toString();
        Log.e("abeer", supplierName);

        if (!TextUtils.isEmpty(supplierName)) {
            if (!TextUtils.isEmpty(thicknessLocal) && (!checkValidData(thicknessLocal)))
                if (!TextUtils.isEmpty(widthLocal) && (!checkValidData(widthLocal)))
                    if (!TextUtils.isEmpty(lengthLocal) && (!checkValidData(lengthLocal)))
                        if (!TextUtils.isEmpty(noOfPiecesLocal) && (!checkValidData(noOfPiecesLocal)))
                            if (!TextUtils.isEmpty(noOfRejectedLocal) && (!noOfRejectedLocal.equals(".")))
                                if (!TextUtils.isEmpty(noOfBundlesLocal) && (!checkValidData(noOfBundlesLocal))) {

                                    thickness.setError(null);
                                    width.setError(null);
                                    length.setError(null);
                                    noOfPieces.setError(null);
                                    noOfRejected.setError(null);
                                    noOfBundles.setError(null);

                                    thicknessLocal = formatDecimalValue(thicknessLocal);
                                    widthLocal = formatDecimalValue(widthLocal);
                                    lengthLocal = formatDecimalValue(lengthLocal);
                                    noOfPiecesLocal = formatDecimalValue(noOfPiecesLocal);
                                    noOfRejectedLocal = formatDecimalValue(noOfRejectedLocal);
                                    noOfBundlesLocal = formatDecimalValue(noOfBundlesLocal);

                                    thicknessLocal = isContainValueAfterDot(thicknessLocal);
                                    widthLocal = isContainValueAfterDot(widthLocal);
                                    lengthLocal = isContainValueAfterDot(lengthLocal);
                                    noOfPiecesLocal = isContainValueAfterDot(noOfPiecesLocal);
                                    noOfRejectedLocal = isContainValueAfterDot(noOfRejectedLocal);
                                    noOfBundlesLocal = isContainValueAfterDot(noOfBundlesLocal);

                                    netBundlesString += Integer.parseInt(noOfBundlesLocal);
                                    netRejectedString += Integer.parseInt(noOfRejectedLocal);

                                    NewRowInfo rowInfo = new NewRowInfo();
                                    rowInfo.setSupplierName(supplierName);
                                    rowInfo.setThickness(Double.parseDouble(thicknessLocal));
                                    rowInfo.setWidth(Double.parseDouble(widthLocal));
                                    rowInfo.setLength(Double.parseDouble(lengthLocal));
                                    rowInfo.setNoOfPieces(Double.parseDouble(noOfPiecesLocal));
                                    rowInfo.setNoOfRejected(Double.parseDouble(noOfRejectedLocal));
                                    rowInfo.setNoOfBundles(Double.parseDouble(noOfBundlesLocal));
                                    rowInfo.setGrade(gradeText);

                                    if (headerTableLayout.getChildCount() == 0)
                                        addTableHeader(headerTableLayout);

                                    tableRow = new TableRow(this);
                                    if (edieFlag == 10 && tableLayout.getChildCount() < 2) { //Report 2
                                        tableLayout.removeAllViews();
                                        newRowList.clear();
                                        rowInfo.setSerial(editSerial);
                                        Log.e("reportTwo", rowInfo.getSerial());

                                        fillTableRow(tableRow, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal, gradeText);
                                        tableLayout.addView(tableRow);
                                        supplierName = "";
                                    } else if (edieFlag == 11 && tableLayout.getChildCount() > 0) { //truck Report

//                                        boolean isOverflowRaw = false;
//                                        for (int m = 0; m < editList.size(); m++)
//                                            if (oldNewRowInfo.getThickness() == editList.get(m).getThickness()
//                                                    && oldNewRowInfo.getWidth() == editList.get(m).getWidth()
//                                                    && oldNewRowInfo.getLength() == editList.get(m).getLength()
//                                                    && oldNewRowInfo.getNoOfPieces() == editList.get(m).getNoOfPieces()
//                                                    && oldNewRowInfo.getNoOfRejected() == editList.get(m).getNoOfRejected()
//                                                    && oldNewRowInfo.getNoOfBundles() == editList.get(m).getNoOfBundles()
//                                            ) {
//                                                isOverflowRaw = true;
//                                                editList.remove(m);
//                                            }
//
//                                        if (isOverflowRaw) {
                                        rowInfo.setSerial(editSerial);//oldNewRowInfo.getSerial());
                                        oldNewRowInfo = new NewRowInfo();
                                        editList.add(rowInfo);
                                        tableLayout.removeAllViews();
                                        fillDataFromReport1();
                                        Log.e("fromedit11", "2:" + editList.size());
//                                        } else {
//                                            rowInfo = null;
//                                            Toast.makeText(this, "Please choose the raw first!", Toast.LENGTH_SHORT).show();
//                                        }
                                    } else {
                                        fillTableRow(tableRow, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal, gradeText);
                                        if (flag == 1) {
                                            tableLayout.addView(tableRow, indexs);
                                        } else {
                                            tableLayout.addView(tableRow);
                                        }
                                    }
                                    if (!(rowInfo == null))
                                        newRowList.add(rowInfo);

                                    thickness.setText("");
                                    width.setText("");
                                    length.setText("");
                                    noOfPieces.setText("");
                                    noOfRejected.setText("");
                                    noOfBundles.setText("");
                                    gradeSpinner.setSelection(gradeList.indexOf("KD"));
                                    gradeText = "KD";
                                    thickness.requestFocus();

                                } else {
                                    noOfBundles.setError("Required!");
                                }
                            else {
                                noOfRejected.setError("Required!");
                            }
                        else {
                            noOfPieces.setError("Required!");
                        }
                    else {
                        length.setError("Required!");
                    }
                else {
                    width.setError("Required!");
                }
            else {
                thickness.setError("Required!");
            }
        } else {
            searchSupplier.setError("Please Select First!");
        }
    }

    void idInAcceptanceNew() {
        id = newRowList.size();
        tableRow.setTag(id);
    }

    void editRowInAcceptanceNew() {
        tableRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                update = 1;
                tableRowEdit = (TableRow) v;
                index = (Integer) v.getTag();
                fillDialog(newRowList.get((Integer) v.getTag()));
                Log.e("acceptance", "" + v.getTag());

                return false;
            }
        });


    }

    void fillDialog(NewRowInfo newRowInfo) {

        thickness.setText("" + (int) newRowInfo.getThickness());
        width.setText("" + (int) newRowInfo.getWidth());
        length.setText("" + (int) newRowInfo.getLength());
        noOfPieces.setText("" + (int) newRowInfo.getNoOfPieces());
        noOfRejected.setText("" + (int) newRowInfo.getNoOfRejected());
        noOfBundles.setText("" + (int) newRowInfo.getNoOfBundles());
        searchSupplier.setText(newRowInfo.getSupplierName());
        supplierName = newRowInfo.getSupplierName();
        try {
            gradeSpinner.setSelection(getGrade(newRowInfo.getGrade()));
        } catch (Exception e) {
            Log.e("grade", "Ex:Grade Error");
        }

        try {

        } catch (Exception e) {
            Log.e("grade", "Ex:Grade Error");
        }

    }

    void EditDialog(NewRowInfo newRowInfo, int index) {
        if (flagIsGet == 0) {
            final Dialog dialog = new Dialog(AddNewRaw.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(true);
            dialog.setContentView(R.layout.edite_dialog);

            TextView addNewRaw_search_supplier_edit;
            Spinner addNewRaw_grade_edit;
            Button editButton;

            EditText addNewRaw_thickness_edit, addNewRaw_width_edit, addNewRaw_length_edit, addNewRaw_no_of_pieces_edit, addNewRaw_no_of_rejected_edit,
                    addNewRaw_no_of_bundles_edit;

            addNewRaw_thickness_edit = dialog.findViewById(R.id.addNewRaw_thickness_edit);
            addNewRaw_width_edit = dialog.findViewById(R.id.addNewRaw_width_edit);
            addNewRaw_length_edit = dialog.findViewById(R.id.addNewRaw_length_edit);

            addNewRaw_no_of_pieces_edit = dialog.findViewById(R.id.addNewRaw_no_of_pieces_edit);

            addNewRaw_no_of_rejected_edit = dialog.findViewById(R.id.addNewRaw_no_of_rejected_edit);
            addNewRaw_no_of_bundles_edit = dialog.findViewById(R.id.addNewRaw_no_of_bundles_edit);


            addNewRaw_grade_edit = dialog.findViewById(R.id.addNewRaw_grade_edit);
            editButton = dialog.findViewById(R.id.editButton);


            addNewRaw_search_supplier_edit = dialog.findViewById(R.id.addNewRaw_search_supplier_edit);

            supplierTextTemp = addNewRaw_search_supplier_edit;
            final String[] gradeTextEdit = {"KD"};

            ArrayAdapter gradeAdapter = new ArrayAdapter<String>(this, R.layout.spinner_layout, gradeList);
            gradeAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
            addNewRaw_grade_edit.setAdapter(gradeAdapter);
            addNewRaw_grade_edit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                    gradeTextEdit[0] = parent.getItemAtPosition(position).toString();

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            addNewRaw_thickness_edit.setText("" + (int) newRowInfo.getThickness());
            addNewRaw_width_edit.setText("" + (int) newRowInfo.getWidth());
            addNewRaw_length_edit.setText("" + (int) newRowInfo.getLength());
            addNewRaw_no_of_pieces_edit.setText("" + (int) newRowInfo.getNoOfPieces());
            addNewRaw_no_of_rejected_edit.setText("" + (int) newRowInfo.getNoOfRejected());
            addNewRaw_no_of_bundles_edit.setText("" + (int) newRowInfo.getNoOfBundles());
            addNewRaw_search_supplier_edit.setText(newRowInfo.getSupplierName());

            addNewRaw_search_supplier_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    supplierDialog();
                }
            });

            try {
                addNewRaw_grade_edit.setSelection(getGrade(newRowInfo.getGrade()));
            } catch (Exception e) {
                Log.e("grade", "Ex:Grade Error");
            }

            try {

            } catch (Exception e) {
                Log.e("grade", "Ex:Grade Error");
            }

            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!addNewRaw_thickness_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_thickness_edit.getText().toString()) != 0) {
                        if (!addNewRaw_width_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_width_edit.getText().toString()) != 0) {

                            if (!addNewRaw_length_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_length_edit.getText().toString()) != 0) {

                                if (!addNewRaw_no_of_pieces_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_no_of_pieces_edit.getText().toString()) != 0) {

                                    if (!addNewRaw_no_of_rejected_edit.getText().toString().equals("") /*&& Integer.parseInt(addNewRaw_no_of_rejected_edit.getText().toString()) != 0*/) {

                                        if (!addNewRaw_no_of_bundles_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_no_of_bundles_edit.getText().toString()) != 0) {

                                            if (!addNewRaw_search_supplier_edit.getText().toString().equals("")) {

                                                NewRowInfo newRowInfo1 = new NewRowInfo();
                                                newRowInfo1.setThickness(Double.parseDouble(addNewRaw_thickness_edit.getText().toString()));
                                                newRowInfo1.setWidth(Double.parseDouble(addNewRaw_width_edit.getText().toString()));
                                                newRowInfo1.setLength(Double.parseDouble(addNewRaw_length_edit.getText().toString()));
                                                newRowInfo1.setNoOfPieces(Double.parseDouble(addNewRaw_no_of_pieces_edit.getText().toString()));
                                                newRowInfo1.setNoOfRejected(Double.parseDouble(addNewRaw_no_of_rejected_edit.getText().toString()));
                                                newRowInfo1.setNoOfBundles(Double.parseDouble(addNewRaw_no_of_bundles_edit.getText().toString()));
                                                newRowInfo1.setSupplierName(addNewRaw_search_supplier_edit.getText().toString());
                                                newRowInfo1.setGrade(gradeTextEdit[0]);
                                                updateFlag(newRowInfo1, index);
                                                rejectAdd();
                                                dialog.dismiss();

                                            } else {
                                                addNewRaw_search_supplier_edit.setError("Required !");
                                            }


                                        } else {
                                            addNewRaw_no_of_bundles_edit.setError("Required !");
                                        }

                                    } else {
                                        addNewRaw_no_of_rejected_edit.setError("Required !");
                                    }

                                } else {
                                    addNewRaw_no_of_pieces_edit.setError("Required !");
                                }

                            } else {
                                addNewRaw_length_edit.setError("Required !");
                            }

                        } else {
                            addNewRaw_width_edit.setError("Required !");
                        }
                    } else {
                        addNewRaw_thickness_edit.setError("Required !");
                    }


                }
            });

            dialog.show();
        } else {
            Toast.makeText(this, "Can Not Edit ", Toast.LENGTH_SHORT).show();
        }
    }

    int getGrade(String grade) {
        int position = -1;

        for (int i = 0; i < gradeList.size(); i++) {

            if (gradeList.get(i).equals(grade)) {
                position = i;
                break;
            }
        }

        return position;
    }

    void doneButtonMethod() {
        String truckNoLocal = truckNo.getText().toString();
        String acceptorLocal = acceptor.getText().toString();
        String ttnNoLocal = ttnNo.getText().toString();
        String acceptanceDateLocal = acceptanceDate.getText().toString();

        if (edieFlag == 11 && editList.size() > 0)
            newRowList = editList;
        Log.e("ddddddddddddddddddddd", "" + netBundlesString);

        if (newRowList.size() > 0) {
            if (!TextUtils.isEmpty(truckNoLocal)) {
                if (!TextUtils.isEmpty(acceptorLocal))
                    if (!TextUtils.isEmpty(ttnNoLocal))
                        if (!TextUtils.isEmpty(acceptanceDateLocal)) {
                            doneAcceptRow.setEnabled(false);
                            truckNo.setError(null);
                            ttnNo.setError(null);
                            acceptor.setError(null);

                            if (edieFlag == 10 && editList.size() > 0) {
                                for (int m = 0; m < editList.size(); m++) {
                                    newRowList.add(editList.get(m));
                                    Log.e("showc", editList.get(m).getSerial());
                                }
                            }
                            jsonArray = new JSONArray();

                            for (int i = 0; i < newRowList.size(); i++) {
                                newRowList.get(i).setTruckNo(truckNoLocal);
                                newRowList.get(i).setAcceptedPersonName(acceptorLocal);
                                newRowList.get(i).setTtnNo(ttnNoLocal);
                                newRowList.get(i).setNetBundles("" + netBundlesString);
                                newRowList.get(i).setDate(acceptanceDateLocal);
                                newRowList.get(i).setLocationOfAcceptance(locationText);
                                newRowList.get(i).setTotalRejectedNo("" + netRejectedString);

                                JSONObject object = newRowList.get(i).getJsonData();
                                jsonArray.put(object);

                            }

                            Log.e("newRowList//", "" + newRowList.get(0).getSerial());
                            fillImage(newRowList.get(0));
                            masterData = new JSONObject();
                            masterData = newRowList.get(0).getJsonDataMaster();
//                            if (newRowList.size() != 0) {
//                                ExportToPDF obj = new ExportToPDF(AddNewRaw.this);
//                                obj.exportTruckAcceptanceSendEmail(newRowList, sdf.format(myCalendar.getTime()));
//                            } else {
//                                Toast.makeText(this, "no Data ", Toast.LENGTH_SHORT).show();
//                            }
//
//                            fillImageBitmap(newRowList.get(0));

//                            for(int i=0;i<imageBitmapList.size();i++) {
//                                createDirectoryAndSaveFile(imageBitmapList.get(i),"image_"+i);
//                            }

                            new JSONTask1().execute();// save
//                            }
                        } else {
                            acceptanceDate.setError("Required!");
                        }
                    else {
                        ttnNo.setError("Required!");
                    }
                else {
                    acceptor.setError("Required!");
                }
            } else {
                truckNo.setError("Required");
            }
        } else {
            Toast.makeText(this, "Please add rows first!", Toast.LENGTH_SHORT).show();
        }
    }

    private void fillImageBitmap(NewRowInfo newRowInfo) {

        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageOne()), "image_1.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageTwo()), "image_2.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageThree()), "image_3.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageFour()), "image_4.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageFive()), "image_5.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageSix()), "image_6.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageSeven()), "image_7.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImageEight()), "image_8.png");

        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage9()), "image_9.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage10()), "image_10.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage11()), "image_11.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage12()), "image_12.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage13()), "image_13.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage14()), "image_14.png");
        createDirectoryAndSaveFile(stringToBitMap(newRowInfo.getImage15()), "image_15.png");

    }

    void deleteFiles1(String path) {
        File fdelete = new File(path);
        if (fdelete.exists()) {
            if (fdelete.delete()) {
                System.out.println("file Deleted :");
            } else {
                System.out.println("file not Deleted :");
            }
        }
    }

    private void createDirectoryAndSaveFile(Bitmap imageToSave, String fileName) {
        String directory_path = Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood/";
        File file = new File(directory_path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String targetPdf = directory_path + fileName;
        File path = new File(targetPdf);

        try {
            FileOutputStream out = new FileOutputStream(path);
            imageToSave.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void sendEmailDialog() {
        final Dialog dialog = new Dialog(AddNewRaw.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.send_email_dialog);
        Button sendButton = dialog.findViewById(R.id.sendButton);
        EditText toEmail = dialog.findViewById(R.id.addNewRaw_toEmail);
        EditText subject = dialog.findViewById(R.id.addNewRaw_subject);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toEmail.getText().toString().equals("")) {
                    sendEmail(toEmail.getText().toString(), subject.getText().toString());
                    dialog.dismiss();
                } else {
                    toEmail.setError("Required!");
                }
            }
        });


        dialog.show();
    }

    void sendEmail(String toEmil, String subject) {


        File folder = new File(Environment.getExternalStorageDirectory().getPath() + "/SendEmailWood");
        File[] listOfFiles = folder.listFiles();
        Log.e("pathh= ", "" + folder.getPath().toString() + "  " + listOfFiles.length);
        ArrayList<String> images = new ArrayList<String>();
        for (int i = 0; i < listOfFiles.length; i++) {
            //if (listOfFiles[i].getName().endsWith(".jpg")) {
            images.add(listOfFiles[i].getPath());
            // }
        }
        String  subject2="";
        if(!TextUtils.isEmpty(subject)){
              subject2=subject;
        }else {
              subject2="quality";
        }

//
        BackgroundMail.newBuilder(AddNewRaw.this)//rawanwork2021@gmail.com
                .withUsername("quality@blackseawood.com")//quality@blackseawood.com
                .withPassword("12345678Q")
                .withMailto(toEmil)
                .withType(BackgroundMail.TYPE_PLAIN)
                .withSubject("quality BLACK SEA WOOD")
                .withBody(subject2 /*"this is the body \n www.google.com  \n  http://5.189.130.98:8085/import.php?FLAG=3 \n "  */)
                .withProcessVisibility(true)
                .withAttachments(images)
                .withOnSuccessCallback(new BackgroundMail.OnSuccessCallback() {
                    @Override
                    public void onSuccess() {
                        //do some magic
                        clear();
                        deleteTempFolder(folder.getPath());
                    }
                })
                .withOnFailCallback(new BackgroundMail.OnFailCallback() {
                    @Override
                    public void onFail() {
                        //do some magic
                    }
                })
                .send();


    }

    private void deleteTempFolder(String dir) {
        File myDir = new File(dir);
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    void fillImage(NewRowInfo image) {
        for (int i = 0; i < 15; i++)
            switch (i) {
                case 0:
                    Bitmap bitmap = null;
                    if (image1.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image1.getDrawable();
                        bitmap = drawable.getBitmap();
                        image.setPic11(bitmap);
                        image.setImageOne(bitMapToString(bitmap));
                    } else
                        image.setImageOne(null);
                    break;
                case 1:
                    Bitmap bitmap2 = null;
                    if (image2.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image2.getDrawable();
                        bitmap2 = drawable.getBitmap();
                        image.setPic22(bitmap2);
                        image.setImageTwo(bitMapToString(bitmap2));
                    } else
                        image.setImageTwo(null);
                    break;
                case 2:
                    Bitmap bitmap3 = null;
                    if (image3.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image3.getDrawable();
                        bitmap3 = drawable.getBitmap();
                        image.setPic33(bitmap3);
                        image.setImageThree(bitMapToString(bitmap3));
                    } else
                        image.setImageThree(null);
                    break;
                case 3:
                    Bitmap bitmap4 = null;
                    if (image4.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image4.getDrawable();
                        bitmap4 = drawable.getBitmap();
                        image.setPic44(bitmap4);
                        image.setImageFour(bitMapToString(bitmap4));
                    } else
                        image.setImageFour(null);
                    break;
                case 4:
                    Bitmap bitmap5 = null;
                    if (image5.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image5.getDrawable();
                        bitmap5 = drawable.getBitmap();
                        image.setPic55(bitmap5);
                        image.setImageFive(bitMapToString(bitmap5));
                    } else
                        image.setImageFive(null);
                    break;
                case 5:
                    Bitmap bitmap6 = null;
                    if (image6.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image6.getDrawable();
                        bitmap6 = drawable.getBitmap();
                        image.setPic66(bitmap6);
                        image.setImageSix(bitMapToString(bitmap6));
                    } else
                        image.setImageSix(null);
                    break;
                case 6:
                    Bitmap bitmap7 = null;
                    if (image7.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image7.getDrawable();
                        bitmap7 = drawable.getBitmap();
                        image.setPic77(bitmap7);
                        image.setImageSeven(bitMapToString(bitmap7));
                    } else
                        image.setImageSeven(null);
                    break;
                case 7:
                    Bitmap bitmap8 = null;
                    if (image8.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image8.getDrawable();
                        bitmap8 = drawable.getBitmap();
                        image.setPic88(bitmap8);
                        image.setImageEight(bitMapToString(bitmap8));
                    } else
                        image.setImageEight(null);
                    break;
                case 8:
                    Bitmap bitmap9 = null;
                    if (image9.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image9.getDrawable();
                        bitmap9 = drawable.getBitmap();
                        image.setPic99(bitmap9);
                        image.setImage9(bitMapToString(bitmap9));
                    } else
                        image.setImage9(null);
                    break;
                case 9:
                    Bitmap bitmap10 = null;
                    if (image10.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image10.getDrawable();
                        bitmap10 = drawable.getBitmap();
                        image.setPic1010(bitmap10);
                        image.setImage10(bitMapToString(bitmap10));
                    } else
                        image.setImage10(null);
                    break;
                case 10:
                    Bitmap bitmap11 = null;
                    if (image11.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image11.getDrawable();
                        bitmap11 = drawable.getBitmap();
                        image.setPic1111(bitmap11);
                        image.setImage11(bitMapToString(bitmap11));
                    } else
                        image.setImage11(null);
                    break;
                case 11:
                    Bitmap bitmap12 = null;
                    if (image12.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image12.getDrawable();
                        bitmap12 = drawable.getBitmap();
                        image.setPic1212(bitmap12);
                        image.setImage12(bitMapToString(bitmap12));
                    } else
                        image.setImage12(null);
                    break;
                case 12:
                    Bitmap bitmap13 = null;
                    if (image13.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image13.getDrawable();
                        bitmap13 = drawable.getBitmap();
                        image.setPic1313(bitmap13);
                        image.setImage13(bitMapToString(bitmap13));
                    } else
                        image.setImageEight(null);
                    break;
                case 13:
                    Bitmap bitmap14 = null;
                    if (image14.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image14.getDrawable();
                        bitmap14 = drawable.getBitmap();
                        image.setPic1414(bitmap14);
                        image.setImage14(bitMapToString(bitmap14));
                    } else
                        image.setImage14(null);
                    break;
                case 14:
                    Bitmap bitmap15 = null;
                    if (image15.getVisibility() == View.VISIBLE) {
                        BitmapDrawable drawable = (BitmapDrawable) image15.getDrawable();
                        bitmap15 = drawable.getBitmap();
                        image.setPic1515(bitmap15);
                        image.setImage15(bitMapToString(bitmap15));
                    } else
                        image.setImage15(null);
                    break;


            }
//        for (int i = 0; i < imagesList.size(); i++)
//            switch (i) {
//                case 0:
//                    image.setImageOne(imagesList.get(i));
//                    break;
//                case 1:
//                    image.setImageTwo(imagesList.get(i));
//                    break;
//                case 2:
//                    image.setImageThree(imagesList.get(i));
//                    break;
//                case 3:
//                    image.setImageFour(imagesList.get(i));
//                    break;
//                case 4:
//                    image.setImageFive(imagesList.get(i));
//                    break;
//                case 5:
//                    image.setImageSix(imagesList.get(i));
//                    break;
//                case 6:
//                    image.setImageSeven(imagesList.get(i));
//                    break;
//                case 7:
//                    image.setImageEight(imagesList.get(i));
//                    break;
//            }
        Log.e("showimages", " 1: " +
                image.getImageOne() + " 2: " +
                image.getImageTwo() + " 3: " +
                image.getImageThree() + " 4: " +
                image.getImageFour() + " 5: " +
                image.getImageFive() + " 6: " +
                image.getImageSix() + " 7: " +
                image.getImageSeven() + " 8: " +
                image.getImageEight()
        );

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        if (flagIsGet == 0) {
            if (imageNo < 15 || isEditImage) {

                if ((ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                        && (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                } else {
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.TITLE, "NewPicture");
                    values.put(MediaStore.Images.Media.DESCRIPTION, "FromyourCamera");
                    imageUri = getContentResolver().insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(intent, 18);
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(cameraIntent, 18);
//            imageNo = i;
                }
                isCamera = false;
            } else {
                showSnackbar("Reached maximum size of images!", false);
            }
        } else {
            Toast.makeText(this, "This For Email Can Not Edit", Toast.LENGTH_SHORT).show();

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100)
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                return;

        openCamera();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int permission = ActivityCompat.checkSelfPermission(AddNewRaw.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    AddNewRaw.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (requestCode == 18 && resultCode == RESULT_OK) {
            Bitmap thumbnail = null;
//            try {
//                thumbnail = MediaStore.Images.Media.getBitmap(
//                        getContentResolver(), imageUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            image1.setImageBitmap(thumbnail);
//            imagesList.add(0, bitMapToString(thumbnail));
//            imageurl = getRealPathFromURI(imageUri);

//            Bundle intent = data.getExtras();
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
//            bitmap = getResizedBitmap(bitmap, 100, 100);
//            File pictureFile;

            int check;
            if (!isEditImage) {
                imageNo++;
                check = imageNo;
            } else {
                check = editImageNo;
            }

//            Log.e("checkvalue", "" + check);
//            if (data != null) {
            switch (check) {
                case 1:
                    Log.e("checkvalue1", "" + check);
                    image1.setVisibility(View.VISIBLE);
//                    convertToURI(data, image1, check);
//                    image1.setImageBitmap(bitmap);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    image1.setImageBitmap(thumbnail);
//                    imagesList.add(0, bitMapToString(thumbnail));
                    break;
                case 2:
                    image2.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image2.setImageBitmap(thumbnail);
//                    imagesList.add(1, bitMapToString(thumbnail));
                    break;
                case 3:
                    image3.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image3.setImageBitmap(thumbnail);
//                    imagesList.add(2, bitMapToString(thumbnail));
                    break;
                case 4:
                    image4.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image4.setImageBitmap(thumbnail);
//                    imagesList.add(3, bitMapToString(bitmap));
                    break;
                case 5:
                    image5.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image5.setImageBitmap(thumbnail);
//                    imagesList.add(4, bitMapToString(bitmap));
                    break;
                case 6:
                    image6.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image6.setImageBitmap(thumbnail);
//                    imagesList.add(5, bitMapToString(bitmap));
                    break;
                case 7:
                    image7.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image7.setImageBitmap(thumbnail);
//                    imagesList.add(6, bitMapToString(bitmap));
                    break;
                case 8:
                    image8.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image8.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;

                case 9:
                    image9.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image9.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 10:
                    image10.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image10.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 11:
                    image11.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image11.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 12:
                    image12.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image12.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 13:
                    image13.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image13.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 14:
                    image14.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image14.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;


                case 15:
                    image15.setVisibility(View.VISIBLE);
                    try {
                        thumbnail = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageUri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    image15.setImageBitmap(thumbnail);
//                    imagesList.add(7, bitMapToString(bitmap));
                    break;

//                }
            }

            isEditImage = false;
        }

        if (requestCode == PICK_IMAGE_MULTIPLE && resultCode == RESULT_OK && null != data) {
            // Get the Image from data
            if (data.getClipData() != null) {
                ClipData mClipData = data.getClipData();
                int cout = data.getClipData().getItemCount();
                imageBitmapList.clear();
                for (int i = 0; i < cout; i++) {
                    // adding imageuri in array

                    try {
                        Uri imageurl = data.getClipData().getItemAt(i).getUri();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                                getContentResolver(), imageurl);

                        imageBitmapList.add(i, bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                Log.e("imageListSize", "test = " + imageBitmapList.size());
                // setting 1st selected image into image switcher
                //imageView.setImageURI(mArrayUri.get(0));
                //  position = 0;
//                  for (int i = 0; i < imageBitmapList.size(); i++)
//            switch (i) {
//                case 0:
//                    if (imageBitmapList.get(i) == null) {
//                        image1.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image1.setVisibility(View.VISIBLE);
//                    image1.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 1:
//                    if (imageBitmapList.get(i) == null) {
//                        image2.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image2.setVisibility(View.VISIBLE);
//                    image2.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 2:
//                    if (imageBitmapList.get(i) == null) {
//                        image3.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image3.setVisibility(View.VISIBLE);
//                    image3.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 3:
//                    if (imageBitmapList.get(i) == null) {
//                        image4.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image4.setVisibility(View.VISIBLE);
//                    image4.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 4:
//                    if (imageBitmapList.get(i) == null) {
//                        image5.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image5.setVisibility(View.VISIBLE);
//                    image5.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 5:
//                    if (imageBitmapList.get(i) == null) {
//                        image6.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image6.setVisibility(View.VISIBLE);
//                    image6.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 6:
//                    if (imageBitmapList.get(i) == null) {
//                        image7.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image7.setVisibility(View.VISIBLE);
//                    image7.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 7:
//                    if (imageBitmapList.get(i) == null) {
//                        image8.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image8.setVisibility(View.VISIBLE);
//                    image8.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 8:
//                    if (imageBitmapList.get(i) == null) {
//                        image9.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image9.setVisibility(View.VISIBLE);
//                    image9.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 9:
//                    if (imageBitmapList.get(i) == null) {
//                        image10.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image10.setVisibility(View.VISIBLE);
//                    image10.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 10:
//                    if (imageBitmapList.get(i) == null) {
//                        image11.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image11.setVisibility(View.VISIBLE);
//                    image11.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 11:
//                    if (imageBitmapList.get(i) == null) {
//                        image12.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image12.setVisibility(View.VISIBLE);
//                    image12.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 12:
//                    if (imageBitmapList.get(i) == null) {
//                        image13.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image13.setVisibility(View.VISIBLE);
//                    image13.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 13:
//                    if (imageBitmapList.get(i) == null) {
//                        image14.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image14.setVisibility(View.VISIBLE);
//                    image14.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 14:
//                    if (imageBitmapList.get(i) == null) {
//                        image15.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image15.setVisibility(View.VISIBLE);
//                    image15.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
                fillImagesInLayout();


//        super.onRestoreInstanceState(savedInstanceState);


            } else {
                Uri imageurl = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            getContentResolver(), imageurl);
                    imageBitmapList.clear();
                    imageBitmapList.add(0, bitmap);
                    fillImagesInLayout();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                //imageView.setImageURI(mArrayUri.get(0));
                //position = 0;
            }

        } else {
            // show this if no image is selected
            Toast.makeText(this, "You haven't picked Image", Toast.LENGTH_LONG).show();
        }


        isCamera = true;
    }


    void fillImagesInLayout() {
        for (int i = 0; i < imageBitmapList.size(); i++) {

            if (imageNo < 15) {
                if (image1.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    Log.e("checkvalue1", "" + i);
                    image1.setVisibility(View.VISIBLE);
//                    convertToURI(data, image1, check);
//                    image1.setImageBitmap(bitmap);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image1.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(0, bitMapToString(thumbnail));
//                    break;
                } else if (image2.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image2.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image2.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(1, bitMapToString(thumbnail));
//                    break;
                } else if (image3.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image3.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image3.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(2, bitMapToString(thumbnail));
//                    break;
                } else if (image4.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image4.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image4.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(3, bitMapToString(bitmap));
//                    break;
                } else if (image5.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image5.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image5.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(4, bitMapToString(bitmap));
//                    break;
                } else if (image6.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image6.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image6.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(5, bitMapToString(bitmap));
//                    break;
                } else if (image7.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image7.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image7.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(6, bitMapToString(bitmap));
//                    break;
                } else if (image8.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image8.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image8.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;

                } else if (image9.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image9.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image9.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;


                } else if (image10.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image10.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image10.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;


                } else if (image11.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image11.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image11.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;


                } else if (image12.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image12.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image12.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;


                } else if (image13.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image13.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image13.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;


                } else if (image14.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image14.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image14.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;
                } else if (image15.getVisibility() == View.INVISIBLE && imageBitmapList.get(i) != null) {
                    image15.setVisibility(View.VISIBLE);
//                    try {
//                        thumbnail = MediaStore.Images.Media.getBitmap(
//                                getContentResolver(), imageUri);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                    imageNo++;
                    image15.setImageBitmap(imageBitmapList.get(i));
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;
                }


//                }

//            switch (i) {
//                case 0:
//                    if (imageBitmapList.get(i) == null) {
//                        image1.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image1.setVisibility(View.VISIBLE);
//                    image1.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 1:
//                    if (imageBitmapList.get(i) == null) {
//                        image2.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image2.setVisibility(View.VISIBLE);
//                    image2.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 2:
//                    if (imageBitmapList.get(i) == null) {
//                        image3.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image3.setVisibility(View.VISIBLE);
//                    image3.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 3:
//                    if (imageBitmapList.get(i) == null) {
//                        image4.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image4.setVisibility(View.VISIBLE);
//                    image4.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 4:
//                    if (imageBitmapList.get(i) == null) {
//                        image5.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image5.setVisibility(View.VISIBLE);
//                    image5.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 5:
//                    if (imageBitmapList.get(i) == null) {
//                        image6.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image6.setVisibility(View.VISIBLE);
//                    image6.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 6:
//                    if (imageBitmapList.get(i) == null) {
//                        image7.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image7.setVisibility(View.VISIBLE);
//                    image7.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//                case 7:
//                    if (imageBitmapList.get(i) == null) {
//                        image8.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image8.setVisibility(View.VISIBLE);
//                    image8.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 8:
//                    if (imageBitmapList.get(i) == null) {
//                        image9.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image9.setVisibility(View.VISIBLE);
//                    image9.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 9:
//                    if (imageBitmapList.get(i) == null) {
//                        image10.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image10.setVisibility(View.VISIBLE);
//                    image10.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 10:
//                    if (imageBitmapList.get(i) == null) {
//                        image11.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image11.setVisibility(View.VISIBLE);
//                    image11.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 11:
//                    if (imageBitmapList.get(i) == null) {
//                        image12.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image12.setVisibility(View.VISIBLE);
//                    image12.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 12:
//                    if (imageBitmapList.get(i) == null) {
//                        image13.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image13.setVisibility(View.VISIBLE);
//                    image13.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 13:
//                    if (imageBitmapList.get(i) == null) {
//                        image14.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image14.setVisibility(View.VISIBLE);
//                    image14.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//                case 14:
//                    if (imageBitmapList.get(i) == null) {
//                        image15.setVisibility(View.INVISIBLE);
//                        break;
//                    }
//                    imageNo++;
//                    image15.setVisibility(View.VISIBLE);
//                    image15.setImageBitmap((imageBitmapList.get(i)));
//                    break;
//
//
//            }

            } else {
                showSnackbar("Reached maximum size of images!", false);

            }
        }
    }

    void fillImagesFromEmail() {
        for (int i = 0; i < 15; i++) {


            if (image1.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic11() != null) {
                image1.setVisibility(View.VISIBLE);
                image1.setImageBitmap(newRowInfoPic.getPic11());

            } else if (image2.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic22() != null) {
                image2.setVisibility(View.VISIBLE);
                image2.setImageBitmap(newRowInfoPic.getPic22());

            } else if (image3.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic33() != null) {
                image3.setVisibility(View.VISIBLE);
                image3.setImageBitmap(newRowInfoPic.getPic33());

            } else if (image4.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic44() != null) {
                image4.setVisibility(View.VISIBLE);
                image4.setImageBitmap(newRowInfoPic.getPic44());

            } else if (image5.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic55() != null) {
                image5.setVisibility(View.VISIBLE);
//
                image5.setImageBitmap(newRowInfoPic.getPic55());

            } else if (image6.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic66() != null) {
                image6.setVisibility(View.VISIBLE);
//
                image6.setImageBitmap(newRowInfoPic.getPic66());
//                    imagesList.add(5, bitMapToString(bitmap));
//                    break;
            } else if (image7.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic77() != null) {
                image7.setVisibility(View.VISIBLE);

                image7.setImageBitmap(newRowInfoPic.getPic88());
//                    imagesList.add(6, bitMapToString(bitmap));
//                    break;
            } else if (image8.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic88() != null) {
                image8.setVisibility(View.VISIBLE);
                image8.setImageBitmap(newRowInfoPic.getPic88());
//                    imagesList.add(7, bitMapToString(bitmap));
//                    break;

            } else if (image9.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic99() != null) {
                image9.setVisibility(View.VISIBLE);
                imageNo++;
                image9.setImageBitmap(newRowInfoPic.getPic99());


            } else if (image10.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1010() != null) {
                image10.setVisibility(View.VISIBLE);
                image10.setImageBitmap(newRowInfoPic.getPic1010());


            } else if (image11.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1111() != null) {
                image11.setVisibility(View.VISIBLE);
                image11.setImageBitmap(newRowInfoPic.getPic1111());

            } else if (image12.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1212() != null) {
                image12.setVisibility(View.VISIBLE);

                image12.setImageBitmap(newRowInfoPic.getPic1212());


            } else if (image13.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1313() != null) {
                image13.setVisibility(View.VISIBLE);

                image13.setImageBitmap(newRowInfoPic.getPic1313());

            } else if (image14.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1414() != null) {
                image14.setVisibility(View.VISIBLE);
                image14.setImageBitmap(newRowInfoPic.getPic1414());

            } else if (image15.getVisibility() == View.INVISIBLE && newRowInfoPic.getPic1515() != null) {
                image15.setVisibility(View.VISIBLE);

                image15.setImageBitmap(newRowInfoPic.getPic1515());

            }


        }
    }

    void convertToURI(Intent data, ImageView viewImage, int i) {
        Log.e("indexxx", "" + i + " / " + viewImage.getId());
//        if (data != null) {
//            image = data.getData();
//            viewImage.setImageURI(image);
//        }
//        if (image == null && mCameraFileName != null) {
//            image = Uri.fromFile(new File(mCameraFileName));
//            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png";
//            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png");
//            viewImage.setImageBitmap(bitmap);
//            imagesList.add(i, bitMapToString(bitmap));
        ////            deleteFiles(path);
//        }
        File file = new File(mCameraFileName);
        if (!file.exists()) {
            file.mkdir();
            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png";
            Log.e("InventoryDBFolder1", "" + path);
            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png");
            viewImage.setImageBitmap(bitmap);
//            bitmapImagesList.add(i, bitmap);
            imagesList.add(i, bitMapToString(bitmap));
//            imagesList.add(i, "bitMapToString(bitmap)");
//            deleteFiles(path);
        } else {

            path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png";
            Log.e("InventoryDBFolder2", "" + path);
            bitmap = BitmapFactory.decodeFile(Environment.getExternalStorageDirectory().getAbsolutePath() + "/in" + imageNo + ".png");
            viewImage.setImageBitmap(bitmap);
            imagesList.add(i, bitMapToString(bitmap));
//            bitmapImagesList.add(i, bitmap);// bitMapToString(bitmap)
//            deleteFiles(path);

        }
    }

    public void deleteFiles(String path) {
        File file = new File(path);

        if (file.exists()) {
            String deleteCmd = "rm -r " + path;
//            Runtime runtime = Runtime.getRuntime();
            try {
                process = Runtime.getRuntime().exec(deleteCmd);

//                runtime.exec(deleteCmd);
            } catch (IOException e) {

            }
        }
        process.destroy();
    }

    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
        } else {
            if (imageNo < 8) {
                isCamera = false;
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                Intent intent = new Intent();
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);

                String newPicFile = "in" + (imageNo + 1) + ".png";
                String outPath = Environment.getExternalStorageDirectory() + File.separator + newPicFile;
                Log.e("InventoryDBFolder", "" + outPath);
                File outFile = new File(outPath);
                path = outPath;
                mCameraFileName = outFile.toString();
                Uri outuri = Uri.fromFile(outFile);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, outuri);
                startActivityForResult(intent, imageNo);
            } else {
                showSnackbar("Reached maximum size of images!", false);
            }
        }
    }

    public String bitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 20, baos);
            byte[] arr = baos.toByteArray();
            String result = Base64.encodeToString(arr, Base64.DEFAULT);
            return result;
        }

        return "";
    }

    public Bitmap stringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    boolean checkValidData(String word) {
//        Log.e("checkValidData4", word + ((word.length() == 1)));
//        Log.e("checkValidData4", word + ((word.contains("."))));
//        Log.e("checkValidData4", word + ((word.length() == 1) && (word.equals("."))));
        if (((word.length() == 1) && (word.contains("."))))
            return true;
        else if (((word.length() > 0) && Double.parseDouble(word) == 0))
            return true;
        return false;
    }

    String formatDecimalValue(String value) {
        String charOne = String.valueOf(value.charAt(0));
        String charEnd = String.valueOf(value.charAt(value.length() - 1));

        if (charOne.equals("."))
            return ("0" + value);
        else if (charEnd.equals(".")) {
            value = value.substring(0, value.length() - 1);
//            Log.e("checkValidData3", "" +value);
            return (value);
        }
//        Log.e("value", value);
        return value;
    }

    String isContainValueAfterDot(String string) {
        if (string.contains(".")) {
            String isConten = "";
            String afterDot = string.substring(string.indexOf(".") + 1, string.length());
            Log.e("afterDot1", "" + afterDot + "      " + string);
            if (!(Double.parseDouble(afterDot) > 0)) {
                Log.e("afterDot2", "" + string.substring(0, string.indexOf(".")));
                isConten = string.substring(0, string.indexOf("."));

            } else {
                isConten = string;
            }
//        Log.e("value2", isConten);
//        Log.e("afterDotreturn", "" + afterDot + "      " + isConten);
            return isConten;
        } else
            return string;

    }

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal, int updateFlag) {

        supplierName = supplierNameLocal;
        searchSupplier.setText(supplierName);
        searchSupplier.setError(null);
        if (updateFlag == 1) {
            supplierTextTemp.setText(supplierNameLocal);
        }
        searchDialog.dismiss();


    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        int max = 10;
        if (edieFlag == 11)
            max = 9;
        for (int i = 0; i < max; i++) {
            TextView textView = new TextView(this);
            textView.setBackgroundResource(R.color.orange);
            TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
//            textViewParam.setMargins(1, 5, 1, 1);
            textView.setTextSize(15);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setLayoutParams(textViewParam);
            ImageView imageView = new ImageView(this);
            switch (i) {
                case 0:
                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
//                    param.setMargins(1, 5, 1, 1);
                    textView.setLayoutParams(param);
                    textView.setText("Supplier");
                    tableRow.addView(textView);
                    break;
                case 1:
                    textView.setText("Thic");
                    tableRow.addView(textView);
                    break;
                case 2:
                    textView.setText("Width");
                    tableRow.addView(textView);
                    break;
                case 3:
                    textView.setText("Length");
                    tableRow.addView(textView);
                    break;
                case 4:
                    textView.setText("Pieces");
                    tableRow.addView(textView);
                    break;
                case 5:
                    textView.setText("Rejected");
                    tableRow.addView(textView);
                    break;
                case 6:
                    textView.setText("Bundles");
                    tableRow.addView(textView);
                    break;
                case 7:
                    textView.setText("Grade");
                    tableRow.addView(textView);
                    break;
                case 8:
                    TableRow.LayoutParams editParam = new TableRow.LayoutParams(40, 40);
                    imageView.setPadding(0, 10, 0, 10);
                    imageView.setLayoutParams(editParam);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_24dp));
                    imageView.setBackgroundResource(R.color.orange);
                    tableRow.addView(imageView);
                    break;
                case 9:
                    TableRow.LayoutParams deleteParam = new TableRow.LayoutParams(40, 40);
                    imageView.setPadding(0, 10, 0, 10);
                    imageView.setLayoutParams(deleteParam);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                    imageView.setBackgroundResource(R.color.orange);
                    tableRow.addView(imageView);
                    break;

            }

        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void fillTableRow(TableRow tableRow, String thicknessText, String widthText, String lengthText, String noOfPiecesText
            , String noOfRejectedText, String noBundleText, String grade) {
        try {
            int max = 10;
            if (edieFlag == 11)
                max = 9;
            for (int i = 0; i < max; i++) {
                TextView textView = new TextView(this);
                textView.setBackgroundResource(R.color.light_orange);
                TableRow.LayoutParams textViewParam = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT, 1f);
                textViewParam.setMargins(1, 5, 1, 1);
                textView.setPadding(0, 10, 0, 10);
                textView.setTextSize(15);
                textView.setTextColor(ContextCompat.getColor(this, R.color.gray_dark_one));
                textView.setLayoutParams(textViewParam);
                switch (i) {
                    case 0:
//                    TableRow.LayoutParams param = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
//                    param.setMargins(1, 5, 1, 1);
//                    textView.setPadding(0, 10, 0, 10);
//                    textView.setLayoutParams(param);
                        textView.setText(supplierName);
                        tableRow.addView(textView);
                        break;
                    case 1:
                        textView.setText(thicknessText);
                        tableRow.addView(textView);
                        break;
                    case 2:
                        textView.setText(widthText);
                        tableRow.addView(textView);
                        break;
                    case 3:
                        textView.setText(lengthText);
                        tableRow.addView(textView);
                        break;
                    case 4:
                        textView.setText(noOfPiecesText);
                        tableRow.addView(textView);
                        break;
                    case 5:
                        textView.setText(noOfRejectedText);
                        tableRow.addView(textView);
                        break;
                    case 6:
                        textView.setText(noBundleText);
                        tableRow.addView(textView);
                        break;
                    case 7:
                        textView.setText(grade);
                        tableRow.addView(textView);
                        break;

                    case 8:
                        ImageView imageView = new ImageView(this);
                        TableRow.LayoutParams editParam = new TableRow.LayoutParams(40, TableRow.LayoutParams.WRAP_CONTENT);
                        editParam.setMargins(1, 5, 1, 1);
                        imageView.setPadding(0, 10, 0, 10);
                        imageView.setLayoutParams(editParam);
                        imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_o));
                        imageView.setBackgroundResource(R.color.light_orange);
                        imageView.setTag(tableRow.getTag().toString());
                        tableRow.addView(imageView);

                        tableRow.getChildAt(8).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (flagIsGet == 0) {

                                    ImageView imageView = (ImageView) v;
                                    Log.e("tagImageViewid", "= " + imageView.getTag().toString());
                                    int i = Integer.parseInt(imageView.getTag().toString());
                                    EditDialog(newRowList.get(i), i);
                                } else {
                                    Toast.makeText(AddNewRaw.this, "Can Not Edit", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        break;
                    case 9:
                        ImageView imageView2 = new ImageView(this);
                        TableRow.LayoutParams deleteParam = new TableRow.LayoutParams(40, TableRow.LayoutParams.WRAP_CONTENT);
                        deleteParam.setMargins(1, 5, 1, 1);
                        imageView2.setPadding(0, 10, 0, 10);
                        imageView2.setLayoutParams(deleteParam);
                        imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_delete_forever));
                        imageView2.setBackgroundResource(R.color.light_orange);
                        imageView2.setTag(tableRow.getTag().toString());
                        tableRow.addView(imageView2);

                        tableRow.getChildAt(9).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (flagIsGet == 0) {
                                    ImageView imageView = (ImageView) v;
                                    Log.e("tagImageViewid", "= " + imageView.getTag().toString());
                                    AlertDialog.Builder builder = new AlertDialog.Builder(AddNewRaw.this);
                                    builder.setMessage("Are you want delete this row?");
                                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();

                                            deleteFlag();
                                            rejectAdd();

                                        }
                                    });
                                    builder.show();
                                } else {
                                    Toast.makeText(AddNewRaw.this, "Can Not Delete", Toast.LENGTH_SHORT).show();
                                }
                            }

                        });

                        break;
                }
//            tableRow.addView(textView);
                idInAcceptanceNew();
//            editRowInAcceptanceNew();


            }
        } catch (Exception e) {
            Log.e("ExceptaionEE", "rr");
        }

    }


    void supplierDialog() {
        suppliers.clear();
        isCamera = false;
        new JSONTask().execute();

        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_supplier_dialog);
        searchDialog.setCancelable(false);

        SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog.findViewById(R.id.search_supplier_close);
        total = searchDialog.findViewById(R.id.total_suppliers);

        recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuppliersAdapter(this, suppliers, null, null, 1);
        recyclerView.setAdapter(adapter);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
//                        adapter.notifyDataSetChanged();
                return false;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchDialog.dismiss();
                isCamera = false;
            }
        });
        searchDialog.show();
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
                acceptanceDate.setText(sdf.format(myCalendar.getTime()));
            }

        };
        return date;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e("newConfig", "config changed");

        int orientation = newConfig.orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT || orientation == Configuration.ORIENTATION_LANDSCAPE)
            isCamera = true;

    }

    void showSnackbar(String text, boolean showImage) {
        snackbar = Snackbar.make(coordinatorLayout, Html.fromHtml("<font color=\"#3167F0\">" + text + "</font>"), Snackbar.LENGTH_SHORT);//Updated Successfully
        View snackbarLayout = snackbar.getView();
        TextView textViewSnackbar = (TextView) snackbarLayout.findViewById(R.id.snackbar_text);
        if (showImage)
            textViewSnackbar.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_24dp, 0, 0, 0);
        snackbar.show();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
//        linearLayoutView.getVisibility();
        outState.putBoolean(STATE_VISIBILITY, mState);

        if (isCamera) {
            List<TableRow> tableRows = new ArrayList<>();
            int rowcount = tableLayout.getChildCount();
            for (int i = 0; i < rowcount; i++) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                tableRows.add(row);
            }
            outState.putSerializable("table", (Serializable) tableRows);
        }
        outState.putSerializable("list", (Serializable) imagesList);
        outState.putSerializable("rows_list", (Serializable) newRowList);
//        outState.putSerializable("bitmap_image", (Serializable) bitmapImagesList);

//        Log.e("size b", "" + imagesList.size());

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        // Restore state members from saved instance
        mState = savedInstanceState.getBoolean(STATE_VISIBILITY);//
        thickness.requestFocus();
//        linearLayoutView.setVisibility(mState ? View.VISIBLE : View.GONE);
//        presenter.getImportData();
//        bitmapImagesList.clear();
//        bitmapImagesList.addAll((Collection<? extends Bitmap>) savedInstanceState.getSerializable("bitmap_image"));

        imagesList.clear();
        imagesList.addAll((Collection<? extends String>) savedInstanceState.getSerializable("list"));

        newRowList.clear();
        newRowList.addAll((Collection<? extends NewRowInfo>) savedInstanceState.getSerializable("rows_list"));
//        Log.e("size a", "" +savedInstanceState.getSerializable("list"));
//        Log.e("size aa", "" +imagesList.size());
        if (isCamera) {
            isCamera = false;
            List<TableRow> tableRows = (List<TableRow>) savedInstanceState.getSerializable("table");

            if (tableRows.size() > 0)
                addTableHeader(headerTableLayout);

            for (int i = 0; i < tableRows.size(); i++) {
                if (tableRows.get(i).getParent() != null) {
                    ((ViewGroup) tableRows.get(i).getParent()).removeView(tableRows.get(i)); // <- fix
                }
                tableLayout.addView(tableRows.get(i));
            }
        }
        isEditImage = false;
        imageNo = 0;
        for (int i = 0; i < imagesList.size(); i++)
            switch (i) {
                case 0:
                    if (imagesList.get(i) == null) {
                        image1.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image1.setVisibility(View.VISIBLE);
                    image1.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 1:
                    if (imagesList.get(i) == null) {
                        image2.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image2.setVisibility(View.VISIBLE);
                    image2.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 2:
                    if (imagesList.get(i) == null) {
                        image3.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image3.setVisibility(View.VISIBLE);
                    image3.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 3:
                    if (imagesList.get(i) == null) {
                        image4.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image4.setVisibility(View.VISIBLE);
                    image4.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 4:
                    if (imagesList.get(i) == null) {
                        image5.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image5.setVisibility(View.VISIBLE);
                    image5.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 5:
                    if (imagesList.get(i) == null) {
                        image6.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image6.setVisibility(View.VISIBLE);
                    image6.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 6:
                    if (imagesList.get(i) == null) {
                        image7.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image7.setVisibility(View.VISIBLE);
                    image7.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 7:
                    if (imagesList.get(i) == null) {
                        image8.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image8.setVisibility(View.VISIBLE);
                    image8.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 8:
                    if (imagesList.get(i) == null) {
                        image9.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image9.setVisibility(View.VISIBLE);
                    image9.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 9:
                    if (imagesList.get(i) == null) {
                        image10.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image10.setVisibility(View.VISIBLE);
                    image10.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 10:
                    if (imagesList.get(i) == null) {
                        image11.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image11.setVisibility(View.VISIBLE);
                    image11.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 11:
                    if (imagesList.get(i) == null) {
                        image12.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image12.setVisibility(View.VISIBLE);
                    image12.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 12:
                    if (imagesList.get(i) == null) {
                        image13.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image13.setVisibility(View.VISIBLE);
                    image13.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 13:
                    if (imagesList.get(i) == null) {
                        image14.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image14.setVisibility(View.VISIBLE);
                    image14.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


                case 14:
                    if (imagesList.get(i) == null) {
                        image15.setVisibility(View.INVISIBLE);
                        break;
                    }
                    imageNo++;
                    image15.setVisibility(View.VISIBLE);
                    image15.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


            }

//        super.onRestoreInstanceState(savedInstanceState);
    }

    // *************************************** GET SUPPLIERS ***************************************
    private class JSONTask extends AsyncTask<String, String, List<SupplierInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected List<SupplierInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=4");

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
                    JSONArray parentArrayOrders = parentObject.getJSONArray("SUPPLIERS");

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        SupplierInfo supplier = new SupplierInfo();
                        supplier.setSupplierNo(innerObject.getString("SUPPLIER_NO"));
                        supplier.setSupplierName(innerObject.getString("SUPPLIER_NAME"));

                        suppliers.add(supplier);
                        arraylist.add(supplier);

                    }
                } catch (JSONException e) {
                    Log.e("Import Data2", e.getMessage().toString());
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
            return suppliers;
        }


        @Override
        protected void onPostExecute(final List<SupplierInfo> result) {
            super.onPostExecute(result);

            if (result != null) {
                Log.e("result", "*****************" + result.size());
                total.setText("" + suppliers.size());
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(AddNewRaw.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // *************************************** SAVE ***************************************
    private class JSONTask1 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
//https://5.189.130.98/WOODY/export.php

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                JSONObject object = jsonArray.getJSONObject(0);
//                String h = "" + object.getDouble("REJECTED");
//                Log.e("update/", "" + h);
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_TWO", "1"));// list
//                for (int i=0 ;i< newRowList.size();i++) {
//                    nameValuePairs.add(new BasicNameValuePair("ROW_INFO_DETAILS", newRowList.get(i).toString()));
//                }
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_DETAILS", jsonArray.toString().trim()));// list
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_MASTER", masterData.toString().trim())); // json object
//                nameValuePairs.add(new BasicNameValuePair("BUNDLE_PIC", jsonArrayPics.toString().trim()));

                Log.e("addNewRow/", "save " + masterData.toString().trim());
                Log.e("newRowList", "json" + jsonArray.length());
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
                Log.e("tag", "save " + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag of row info", "" + s);

            if (s != null) {
                if (s.contains("RAW_INFO SUCCESS")) {

                    showSnackbar("Added Successfully", true);

                    if (newRowList.size() != 0) {
                        try {
                            ExportToPDF obj = new ExportToPDF(AddNewRaw.this);
                            obj.exportTruckAcceptanceSendEmail(newRowList, sdf.format(myCalendar.getTime()));

                            fillImageBitmap(newRowList.get(0));
                        }catch (Exception e){

                        }
                    } else {
                        Toast.makeText(AddNewRaw.this, "no Data ", Toast.LENGTH_SHORT).show();
                    }



                    truckNo.setText("");
                    acceptor.setText("");
                    ttnNo.setText("");
                    totalBundles.setText("");
                    acceptanceDate.setText(sdf.format(myCalendar.getTime()));
                    acceptanceLocation.setSelection(0);
                    totalRejected.setText("");
                    supplierName = "";
                    searchSupplier.setText("");
                    tableLayout.removeAllViews();
                    newRowList.clear();
                    imagesList.clear();
//                    bitmapImagesList.clear();
                    imageNo = 0;
                    image1.setVisibility(View.INVISIBLE);
                    image2.setVisibility(View.INVISIBLE);
                    image3.setVisibility(View.INVISIBLE);
                    image4.setVisibility(View.INVISIBLE);
                    image5.setVisibility(View.INVISIBLE);
                    image6.setVisibility(View.INVISIBLE);
                    image7.setVisibility(View.INVISIBLE);
                    image8.setVisibility(View.INVISIBLE);

                    image9.setVisibility(View.INVISIBLE);
                    image10.setVisibility(View.INVISIBLE);
                    image11.setVisibility(View.INVISIBLE);
                    image12.setVisibility(View.INVISIBLE);
                    image13.setVisibility(View.INVISIBLE);
                    image14.setVisibility(View.INVISIBLE);
                    image15.setVisibility(View.INVISIBLE);

                    acceptRowLayout.setVisibility(View.VISIBLE);
                    headerLayout.setVisibility(View.VISIBLE);

                    acceptRowButton.setBackgroundResource(R.drawable.frame_shape_2);
                    mainInfoButton.setBackgroundResource(R.drawable.frame_shape_3);
                    doneAcceptRow.setEnabled(true);

                    sendEmailDialog();
                    progressDialog.dismiss();
                    Log.e("tag", "save Success");
                } else {
                    Log.e("tag", "****Failed to export data");
                    progressDialog.dismiss();
                }
            } else {
                progressDialog.dismiss();
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AddNewRaw.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    // *************************************** UPDATE ***************************************
    private class JSONTask2 extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
//https://5.189.130.98/WOODY/export.php

                String JsonResponse = null;
                HttpClient client = new DefaultHttpClient();
                HttpPost request = new HttpPost();
                request.setURI(new URI("http://" + generalSettings.getIpAddress() + "/export.php"));//import 10.0.0.214

                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
//                Log.e("addToInventory/", "" + jsonArrayBundles.toString());
                nameValuePairs.add(new BasicNameValuePair("UPDATE_RAW_INFO", "1"));// list
//                for (int i=0 ;i< newRowList.size();i++) {
//                    nameValuePairs.add(new BasicNameValuePair("ROW_INFO_DETAILS", newRowList.get(i).toString()));
//                }
                nameValuePairs.add(new BasicNameValuePair("TRUCK", oldTruck));//oldTruck
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_DETAILS", jsonArray.toString().trim()));// list
                nameValuePairs.add(new BasicNameValuePair("RAW_INFO_MASTER", masterData.toString().trim())); // json object
//                Log.e("addNewRow/", "update" + masterData.toString().trim() + " ///oldTruck" + oldTruck);

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
                Log.e("tag/", "update" + JsonResponse);

                return JsonResponse;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e("tag of update row info", s);
            progressDialog.dismiss();
            if (s != null) {
                if (s.contains("UPDATE RAWS SUCCESS")) {

                    showSnackbar("Updated Successfully", true);
                    editList.clear();


                    truckNo.setText("");
                    acceptor.setText("");
                    ttnNo.setText("");
                    totalBundles.setText("");
                    acceptanceDate.setText("");
                    acceptanceLocation.setSelection(0);
                    totalRejected.setText("");
                    supplierName = "";
                    searchSupplier.setText("");
                    tableLayout.removeAllViews();
                    newRowList.clear();

                    Intent it;
                    if (edieFlag == 10)
                        it = new Intent(AddNewRaw.this, AcceptanceInfoReport.class);
                    else
                        it = new Intent(AddNewRaw.this, AcceptanceReport.class);
                    it.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(it);
                    finish();
                    Log.e("tag", "update Success");
                } else {
//                    presenter.setSerialNo("");
//                    SettingsFile.serialNumber = "";
                    Log.e("tag", "****Failed to export data");
//                    Toast.makeText(AddToInventory.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
                }
            } else {
//                presenter.setSerialNo("");
//                SettingsFile.serialNumber = "";
                Log.e("tag", "****Failed to export data Please check internet connection");
                Toast.makeText(AddNewRaw.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    // *************************************** GET TTN ***************************************
    private class JSONTaskTTN extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            proTTn = new ProgressDialog(AddNewRaw.this, R.style.MyAlertDialogStyle);
            proTTn.setMessage("Please Waiting...");
            proTTn.show();
        }

        @Override
        protected String doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;
            String finalJson = null;
            try {
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=19&TTN_NO=" + ttnNo.getText().toString().trim());

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

                finalJson = sb.toString();
                Log.e("finalJson*********", finalJson);


            } catch (IOException e) {
                Log.e("Import Data2", e.getMessage().toString());
            }


            return finalJson;
        }


        @Override
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);

            tableLayout.removeAllViews();
            headerTableLayout.removeAllViews();
            rejectAdd();
            if (result != null) {
                if (!result.contains("noBundleFound")) {
                    // Log.e("result", "*****************" + result.size());
                    flagIsGet = 1;
                    Gson gson = new Gson();
                    NewRowInfo list = gson.fromJson(result, NewRowInfo.class);
                    listOfEmail.clear();
                    listOfEmail.addAll(list.getDetailsList());

                    tableLayout.removeAllViews();
                    addTableHeader(headerTableLayout);
                    for (int i = 0; i < listOfEmail.size(); i++) {
                        supplierName = listOfEmail.get(i).getSupplierName();
                        tableRow = new TableRow(AddNewRaw.this);
                        gradeText = listOfEmail.get(i).getGrade();
                        fillTableRow(tableRow, "" + (int) listOfEmail.get(i).getThickness(), "" + (int) listOfEmail.get(i).getWidth()
                                , "" + (int) listOfEmail.get(i).getLength(), "" + (int) listOfEmail.get(i).getNoOfPieces()
                                , "" + (int) listOfEmail.get(i).getNoOfRejected(), "" + (int) listOfEmail.get(i).getNoOfBundles(), gradeText);
                        tableLayout.addView(tableRow);


                    }
                    new BitmapImage2().execute(listOfEmail.get(0));
                    rejectAdd();
                } else {
                    flagIsGet = 0;
                    Toast.makeText(AddNewRaw.this, "The TTN.NO Not Found", Toast.LENGTH_SHORT).show();
                    proTTn.dismiss();
                }


            } else {
                proTTn.dismiss();
                flagIsGet = 0;
                Toast.makeText(AddNewRaw.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class BitmapImage2 extends AsyncTask<NewRowInfo, String, NewRowInfo> {
        //  Settings generalSettings = new DatabaseHandler(previewLinearContext).getSettings();

        @Override
        protected NewRowInfo doInBackground(NewRowInfo... pictures) {

            newRowInfoPic = pictures[0];
            URL url;
            Bitmap bitmap;
            try {
                if (!newRowInfoPic.equals("null")) {
                    for (int i = 0; i < 15; i++) {

                        switch (i) {
                            case 0:
                                if (pictures[0].getImageOne() != null) {//http://192.168.2.17:8088/woody/images/2342_img_1.png
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageOne());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic11(bitmap);
                                        newRowInfoPic.setImageOne(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic11(bitmap);

                                    } catch (Exception e) {
//                                        pictures[0].setPic11(bitmap);
                                    }
                                }
                                break;
                            case 1:
                                if (pictures[0].getImageTwo() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageTwo());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic22(bitmap);
                                        newRowInfoPic.setImageTwo(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic22(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic22(bitmap);
                                    }
                                }
                                break;
                            case 2:
                                if (pictures[0].getImageThree() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageThree());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic33(bitmap);
                                        newRowInfoPic.setImageThree(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic33(bitmap);

                                    } catch (Exception e) {
//                                        pictures[0].setPic33(bitmap);
                                    }
                                }
                                break;
                            case 3:
                                if (pictures[0].getImageFour() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFour());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic44(bitmap);
                                        newRowInfoPic.setImageFour(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic44(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic44(bitmap);
                                    }
                                }
                                break;
                            case 4:
                                if (pictures[0].getImageFive() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageFive());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic55(bitmap);
                                        newRowInfoPic.setImageFive(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic55(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic55(bitmap);
                                    }
                                }
                                break;
                            case 5:
                                if (pictures[0].getImageSix() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSix());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic66(bitmap);
                                        newRowInfoPic.setImageSix(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic66(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic66(bitmap);
                                    }
                                }
                                break;
                            case 6:
                                if (pictures[0].getImageSeven() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageSeven());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic77(bitmap);
                                        newRowInfoPic.setImageSeven(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic77(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic77(bitmap);
                                    }
                                }
                                break;
                            case 7:
                                if (pictures[0].getImageEight() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImageEight());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic88(bitmap);
                                        newRowInfoPic.setImageEight(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic88(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 8:
                                if (pictures[0].getImage9() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage9());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic99(bitmap);
                                        newRowInfoPic.setImage9(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic99(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;


                            case 9:
                                if (pictures[0].getImage10() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage10());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1010(bitmap);
                                        newRowInfoPic.setImage10(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1010(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 10:
                                if (pictures[0].getImage11() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage11());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1111(bitmap);
                                        newRowInfoPic.setImage11(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1111(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 11:
                                if (pictures[0].getImage12() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage12());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1212(bitmap);
                                        newRowInfoPic.setImage12(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1212(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 12:
                                if (pictures[0].getImage13() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage13());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1313(bitmap);
                                        newRowInfoPic.setImage13(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1313(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 13:
                                if (pictures[0].getImage14() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage14());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1414(bitmap);
                                        newRowInfoPic.setImage14(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1414(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;

                            case 14:

                                if (pictures[0].getImage15() != null) {
                                    url = new URL("http://" + generalSettings.getIpAddress() + "/" + pictures[0].getImage15());
                                    try {
                                        bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                                        newRowInfoPic.setPic1515(bitmap);
                                        newRowInfoPic.setImage15(bitMapToString(bitmap));
                                        listOfEmail.get(0).setPic1515(bitmap);
                                    } catch (Exception e) {
//                                        pictures[0].setPic88(bitmap);
                                    }
                                }
                                break;
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("fromclass2", "exception:doInBackground " + e.getMessage());
                return null;
            }
            return newRowInfoPic;// BitmapFactory.decodeStream(in);
        }

        @Override
        protected void onPostExecute(NewRowInfo pictures) {


            if (pictures != null) {
                //fillImageBitmap(newRowInfoPic);
                fillImagesFromEmail();
                proTTn.dismiss();
            } else {
                proTTn.dismiss();
                Toast.makeText(AddNewRaw.this, "Fail get Pic", Toast.LENGTH_SHORT).show();
            }


        }
    }


}


