package com.falconssoft.woodysystem.stage_one;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import com.falconssoft.woodysystem.DatabaseHandler;
import com.falconssoft.woodysystem.HorizontalListView;
import com.falconssoft.woodysystem.PicturesAdapter;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.WoodPresenter;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.models.Settings;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.reports.AcceptanceInfoReport;
import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;
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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import static com.falconssoft.woodysystem.reports.AcceptanceInfoReport.EDIT_FLAG;
import static com.falconssoft.woodysystem.reports.AcceptanceReport.EDIT_LIST2;
import static com.falconssoft.woodysystem.reports.AcceptanceReport.EDIT_RAW2;

public class EditPage extends AppCompatActivity implements View.OnClickListener {

    private boolean mState = false, isEditImage = false;
    private final String STATE_VISIBILITY = "state-visibility";
    private Settings generalSettings;
    private WoodPresenter presenter;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8, image9, image10, image11, image12, image13, image14, image15;
    private TextView addNewSupplier, searchSupplier, addButton, acceptRowButton, mainInfoButton, acceptanceDate, addPicture, totalRejected, totalBundles;
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
    private Button doneAcceptRow;
    //    private TableLayout tableLayout, headerTableLayout;
    private TableRow tableRow;
    private Dialog searchDialog;

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
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private List<SupplierInfo> arraylist;
    private static boolean isCamera = false;
    private int edieFlag = 0;
    private int netBundlesString = 0, netRejectedString = 0;
    private ProgressDialog progressDialog;
    public static String truckNoBeforeUpdate = "";
    public static String serialBeforeUpdate = "";
    private RecyclerView rowsRecyclerView;
    private EditPageAdapter editPageAdapter;

    private NewRowInfo imagesRowInfo;
    private String oldTruck = "", editSerial = "";// for edit
    private Dialog dialog;


    private String thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal;

    TextView supplierTextTemp=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);

        databaseHandler = new DatabaseHandler(EditPage.this);
        suppliers = new ArrayList<>();
        myCalendar = Calendar.getInstance();
        generalSettings = new Settings();
        generalSettings = databaseHandler.getSettings();
        presenter = new WoodPresenter(this);
        this.arraylist = new ArrayList<>();
//        this.arraylist.addAll(this.supplierInfoList);

        rowsRecyclerView = findViewById(R.id.editPage_recycler);
        coordinatorLayout = findViewById(R.id.editPage_coordinator);
        addNewSupplier = findViewById(R.id.editPage_add_supplier);
        searchSupplier = findViewById(R.id.editPage_search_supplier);
        thickness = findViewById(R.id.editPage_thickness);
        width = findViewById(R.id.editPage_width);
        length = findViewById(R.id.editPage_length);
        noOfPieces = findViewById(R.id.editPage_no_of_pieces);
        noOfBundles = findViewById(R.id.editPage_no_of_bundles);
        noOfRejected = findViewById(R.id.editPage_no_of_rejected);
        gradeSpinner = findViewById(R.id.editPage_grade);
        addButton = findViewById(R.id.editPage_add_button);
        acceptRowButton = findViewById(R.id.editPage_acceptRaw_button);
        headerLayout = findViewById(R.id.editPage_linearLayoutHeader);
        acceptRowLayout = findViewById(R.id.editPage_acceptRow_linear);
        mainInfoButton = findViewById(R.id.editPage_acceptRow_back);
//        tableLayout = findViewById(R.id.editPage_table);
//        headerTableLayout = findViewById(R.id.editPage_table_header);
        truckNo = findViewById(R.id.editPage_truckNo);
        acceptanceDate = findViewById(R.id.editPage_acceptance_date);
        acceptor = findViewById(R.id.editPage_acceptor);
        acceptanceLocation = findViewById(R.id.editPage_acceptance_location);
        ttnNo = findViewById(R.id.editPage_ttn_no);
        totalRejected = findViewById(R.id.editPage_total_rejected);
        totalBundles = findViewById(R.id.editPage_total_bundles);
        doneAcceptRow = findViewById(R.id.editPage_acceptRow_done);
        addPicture = findViewById(R.id.editPage_add_photo);
        image1 = findViewById(R.id.editPage_image1);
        image2 = findViewById(R.id.editPage_image2);
        image3 = findViewById(R.id.editPage_image3);
        image4 = findViewById(R.id.editPage_image4);
        image5 = findViewById(R.id.editPage_image5);
        image6 = findViewById(R.id.editPage_image6);
        image7 = findViewById(R.id.editPage_image7);
        image8 = findViewById(R.id.editPage_image8);

        image9 = findViewById(R.id.editPage_image9);
        image10 = findViewById(R.id.editPage_image10);
        image11 = findViewById(R.id.editPage_image11);
        image12 = findViewById(R.id.editPage_image12);
        image13 = findViewById(R.id.editPage_image13);
        image14 = findViewById(R.id.editPage_image14);
        image15 = findViewById(R.id.editPage_image15);

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

        addPicture.setOnClickListener(this);
        mainInfoButton.setOnClickListener(this);
        doneAcceptRow.setOnClickListener(this);
        acceptRowButton.setOnClickListener(this);
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

        checkIfEditItem();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Waiting...");

        rowsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        editPageAdapter = new EditPageAdapter(this, new ArrayList<>());
        rowsRecyclerView.setAdapter(editPageAdapter);

        new JSONTask3().execute();


    }


    void checkIfEditItem() {
        edieFlag = getIntent().getIntExtra(EDIT_FLAG, 0);
        Log.e("flag", "" + edieFlag);
        if (edieFlag == 11) { // from accepted truck report

            Bundle bundle = getIntent().getExtras();
            NewRowInfo rowInfo = (NewRowInfo) bundle.getSerializable(EDIT_RAW2);
            Log.e("checkedit", "" + editList.size());
            editSerial = rowInfo.getSerial();
            oldTruck = rowInfo.getTruckNo();

            imagesRowInfo = new NewRowInfo();
            imagesRowInfo.setImageOne(rowInfo.getImageOne());
            imagesRowInfo.setImageTwo(rowInfo.getImageTwo());
            imagesRowInfo.setImageThree(rowInfo.getImageThree());
            imagesRowInfo.setImageFour(rowInfo.getImageFour());
            imagesRowInfo.setImageFive(rowInfo.getImageFive());
            imagesRowInfo.setImageSix(rowInfo.getImageSix());
            imagesRowInfo.setImageSeven(rowInfo.getImageSeven());
            imagesRowInfo.setImageEight(rowInfo.getImageEight());

            imagesRowInfo.setImage9(rowInfo.getImage9());
            imagesRowInfo.setImage10(rowInfo.getImage10());
            imagesRowInfo.setImage11(rowInfo.getImage11());
            imagesRowInfo.setImage12(rowInfo.getImage12());
            imagesRowInfo.setImage13(rowInfo.getImage13());
            imagesRowInfo.setImage14(rowInfo.getImage14());
            imagesRowInfo.setImage15(rowInfo.getImage15());


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

        }
    }

    private void fillDataFromReport1() {
        for (int i = 0; i < editList.size(); i++) {
            tableRow = new TableRow(this);
            gradeText = editList.get(i).getGrade();
            supplierName = editList.get(i).getSupplierName();
            fillTableRow(tableRow, "" + (int) editList.get(i).getThickness(), "" + (int) editList.get(i).getWidth()
                    , "" + (int) editList.get(i).getLength(), "" + (int) editList.get(i).getNoOfPieces()
                    , "" + (int) editList.get(i).getNoOfRejected(), "" + (int) editList.get(i).getNoOfBundles());
//            tableLayout.addView(tableRow);

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

            tableRow.getChildAt(8).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("delete", "" + finalI + editList.get(finalI).getThickness()
                            + "/" + editList.get(finalI).getWidth()
                            + "/" + editList.get(finalI).getLength()
                            + "/" + editList.get(finalI).getNoOfPieces()
                    );
                    AlertDialog.Builder builder = new AlertDialog.Builder(EditPage.this);
                    builder.setMessage("Are you want delete this row?");
                    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();
                            editList.remove(finalI);
                            fillDataFromReport1();
                        }
                    });
                    builder.show();
                }
            });
        }
    }

    public void deleteRaw(int index) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditPage.this);
        builder.setMessage("Are you want delete this row?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                            tableLayout.removeAllViews();
                editList.remove(index);
                editPageAdapter.notifyDataSetChanged();

                netRejectedString = 0;
                netBundlesString = 0;
                for (int n = 0; n < editList.size(); n++) {
                    netRejectedString += editList.get(n).getNoOfRejected();
                    netBundlesString += editList.get(n).getNoOfBundles();
                }
                totalRejected.setText("" + netRejectedString);
                totalBundles.setText("" + netBundlesString);
            }
        });
        builder.show();
    }


    public  void EditDialog(NewRowInfo newRowInfo,int index){
        final Dialog dialog = new Dialog(EditPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.edite_dialog);

        TextView addNewRaw_search_supplier_edit;
        Spinner addNewRaw_grade_edit;
        Button editButton;

        EditText addNewRaw_thickness_edit,addNewRaw_width_edit,addNewRaw_length_edit,addNewRaw_no_of_pieces_edit,addNewRaw_no_of_rejected_edit,
                addNewRaw_no_of_bundles_edit;

        addNewRaw_thickness_edit=dialog.findViewById(R.id.addNewRaw_thickness_edit);
        addNewRaw_width_edit=dialog.findViewById(R.id.addNewRaw_width_edit);
        addNewRaw_length_edit=dialog.findViewById(R.id.addNewRaw_length_edit);

        addNewRaw_no_of_pieces_edit=dialog.findViewById(R.id.addNewRaw_no_of_pieces_edit);

        addNewRaw_no_of_rejected_edit=dialog.findViewById(R.id.addNewRaw_no_of_rejected_edit);
        addNewRaw_no_of_bundles_edit=dialog.findViewById(R.id.addNewRaw_no_of_bundles_edit);


        addNewRaw_grade_edit=dialog.findViewById(R.id.addNewRaw_grade_edit);
        editButton=dialog.findViewById(R.id.editButton);



        addNewRaw_search_supplier_edit=dialog.findViewById(R.id.addNewRaw_search_supplier_edit);

       supplierTextTemp=addNewRaw_search_supplier_edit;
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

        addNewRaw_thickness_edit.setText(""+(int)newRowInfo.getThickness());
        addNewRaw_width_edit.setText(""+(int)newRowInfo.getWidth());
        addNewRaw_length_edit.setText(""+(int)newRowInfo.getLength());
        addNewRaw_no_of_pieces_edit.setText(""+(int)newRowInfo.getNoOfPieces());
        addNewRaw_no_of_rejected_edit.setText(""+(int)newRowInfo.getNoOfRejected());
        addNewRaw_no_of_bundles_edit.setText(""+(int)newRowInfo.getNoOfBundles());
        addNewRaw_search_supplier_edit.setText(newRowInfo.getSupplierName());

        addNewRaw_search_supplier_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              supplierDialog();
            }
        });

        try {
            addNewRaw_grade_edit.setSelection(getGrade(newRowInfo.getGrade()));
        }catch (Exception e){
            Log.e("grade","Ex:Grade Error");
        }

        try {

        }catch (Exception e){
            Log.e("grade","Ex:Grade Error");
        }

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addNewRaw_thickness_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_thickness_edit.getText().toString()) != 0) {
                    if (!addNewRaw_width_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_width_edit.getText().toString()) != 0) {

                        if (!addNewRaw_length_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_length_edit.getText().toString()) != 0) {

                            if (!addNewRaw_no_of_pieces_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_no_of_pieces_edit.getText().toString()) != 0) {

                                if (!addNewRaw_no_of_rejected_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_no_of_rejected_edit.getText().toString()) != 0) {

                                    if (!addNewRaw_no_of_bundles_edit.getText().toString().equals("") && Integer.parseInt(addNewRaw_no_of_bundles_edit.getText().toString()) != 0) {

                                        if (!addNewRaw_search_supplier_edit.getText().toString().equals("")) {

                                            editList.get(index).setThickness(Double.parseDouble(addNewRaw_thickness_edit.getText().toString()));
                                            editList.get(index).setWidth(Double.parseDouble(addNewRaw_width_edit.getText().toString()));
                                            editList.get(index).setLength(Double.parseDouble(addNewRaw_length_edit.getText().toString()));
                                            editList.get(index).setNoOfPieces(Double.parseDouble(addNewRaw_no_of_pieces_edit.getText().toString()));
                                            editList.get(index).setNoOfRejected(Double.parseDouble(addNewRaw_no_of_rejected_edit.getText().toString()));
                                            editList.get(index).setNoOfBundles(Double.parseDouble(addNewRaw_no_of_bundles_edit.getText().toString()));
                                            editList.get(index).setSupplierName(addNewRaw_search_supplier_edit.getText().toString());
                                            editList.get(index).setGrade( gradeTextEdit[0]);

                                            editPageAdapter.notifyDataSetChanged();
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

    }

    int getGrade(String grade){
        int position=-1;

        for(int i=0;i<gradeList.size();i++){

            if(gradeList.get(i).equals(grade)){
                position=i;
                break;
            }
        }

        return position;
    }

    void supplierDialog (){
        suppliers.clear();
        isCamera = false;
        new JSONTask().execute();

        searchDialog = new Dialog(this);
        searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        searchDialog.setContentView(R.layout.search_supplier_dialog);
        searchDialog.setCancelable(false);

        SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
        TextView close = searchDialog.findViewById(R.id.search_supplier_close);

        recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SuppliersAdapter(null, suppliers, this, null,1);
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.editPage_acceptRow_back:
               // acceptRowLayout.setVisibility(View.GONE);
                acceptRowButton.setBackgroundResource(R.drawable.frame_shape_2);
                mainInfoButton.setBackgroundResource(R.drawable.frame_shape_3);

               // Animation animation1 = AnimationUtils.loadAnimation(this, R.anim.fade_out);
                //headerLayout.setVisibility(View.VISIBLE);
               // headerLayout.startAnimation(animation1);
                thickness.requestFocus();
                break;
            case R.id.editPage_acceptRow_done:
                doneButtonMethod();
                break;
            case R.id.editPage_acceptance_date:
                new DatePickerDialog(EditPage.this, openDatePickerDialog(0), myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.editPage_acceptRaw_button:
               rejectAdd();
                break;
            case R.id.editPage_add_button:
                addButtonMethod();
                rejectAdd();
                break;
            case R.id.editPage_add_photo:
                openCamera();
                break;
            case R.id.editPage_add_supplier:
                Intent intent = new Intent(EditPage.this, AddNewSupplier.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
            case R.id.editPage_search_supplier:
                suppliers.clear();
                isCamera = false;
                new JSONTask().execute();

                searchDialog = new Dialog(this);
                searchDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                searchDialog.setContentView(R.layout.search_supplier_dialog);
                searchDialog.setCancelable(false);

                SearchView searchView = searchDialog.findViewById(R.id.search_supplier_searchView);
                TextView close = searchDialog.findViewById(R.id.search_supplier_close);

                recyclerView = searchDialog.findViewById(R.id.search_supplier_recyclerView);
                recyclerView.setLayoutManager(new LinearLayoutManager(this));
                adapter = new SuppliersAdapter(null, suppliers, this, null,0);
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
            case R.id.editPage_image1:
                isEditImage = true;
                editImageNo = 1;
                openCamera();
                break;
            case R.id.editPage_image2:
                isEditImage = true;
                editImageNo = 2;
                openCamera();
                break;
            case R.id.editPage_image3:
                isEditImage = true;
                editImageNo = 3;
                openCamera();
                break;
            case R.id.editPage_image4:
                isEditImage = true;
                editImageNo = 4;
                openCamera();
                break;
            case R.id.editPage_image5:
                isEditImage = true;
                editImageNo = 5;
                openCamera();
                break;
            case R.id.editPage_image6:
                isEditImage = true;
                editImageNo = 6;
                openCamera();
                break;
            case R.id.editPage_image7:
                isEditImage = true;
                editImageNo = 7;
                openCamera();
                break;
            case R.id.editPage_image8:
                isEditImage = true;
                editImageNo = 8;
                openCamera();
                break;
        }

    }

    void rejectAdd(){

        //headerLayout.setVisibility(View.GONE);
        acceptRowButton.setBackgroundResource(R.drawable.frame_shape_3);
        mainInfoButton.setBackgroundResource(R.drawable.frame_shape_2);

        //Animation animation = AnimationUtils.loadAnimation(this, R.anim.fade_in);
        //acceptRowLayout.setVisibility(View.VISIBLE);
        //acceptRowLayout.startAnimation(animation);
        truckNo.requestFocus();

        netRejectedString = 0;
        netBundlesString = 0;
        Log.e("fromedit11", "" + editList.size());
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

        totalRejected.setText("" + netRejectedString);
        totalBundles.setText("" + netBundlesString);

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
        adapter = new SuppliersAdapter(null, suppliers, this, null,0);
        recyclerView.setAdapter(adapter);
    }

    void addButtonMethod() {
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
                            if (!TextUtils.isEmpty(noOfRejectedLocal) && (!checkValidData(noOfRejectedLocal)))
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

                                    rowInfo.setSerial(editSerial);
                                    editList.add(rowInfo);
                                    editPageAdapter.notifyDataSetChanged();

//                                    if (headerTableLayout.getChildCount() == 0)
//                                        addTableHeader(headerTableLayout);
//
//                                    tableRow = new TableRow(this);
//                                    if (edieFlag == 10 && tableLayout.getChildCount() < 2) { //Report 2
//                                        tableLayout.removeAllViews();
//                                        newRowList.clear();
//                                        rowInfo.setSerial(editSerial);
//                                        Log.e("reportTwo", rowInfo.getSerial());
//
//                                        fillTableRow(tableRow, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal);
//                                        tableLayout.addView(tableRow);
//                                        supplierName = "";
//                                    } else
//
//                                        if (edieFlag == 11 && tableLayout.getChildCount() > 0) { //truck Report
//
////                                        boolean isOverflowRaw = false;
////                                        for (int m = 0; m < editList.size(); m++)
////                                            if (oldNewRowInfo.getThickness() == editList.get(m).getThickness()
////                                                    && oldNewRowInfo.getWidth() == editList.get(m).getWidth()
////                                                    && oldNewRowInfo.getLength() == editList.get(m).getLength()
////                                                    && oldNewRowInfo.getNoOfPieces() == editList.get(m).getNoOfPieces()
////                                                    && oldNewRowInfo.getNoOfRejected() == editList.get(m).getNoOfRejected()
////                                                    && oldNewRowInfo.getNoOfBundles() == editList.get(m).getNoOfBundles()
////                                            ) {
////                                                isOverflowRaw = true;
////                                                editList.remove(m);
////                                            }
////
////                                        if (isOverflowRaw) {
//                                        rowInfo.setSerial(editSerial);//oldNewRowInfo.getSerial());
//                                        oldNewRowInfo = new NewRowInfo();
//                                        editList.add(rowInfo);
//                                        tableLayout.removeAllViews();
//                                        fillDataFromReport1();
//                                        Log.e("fromedit11", "2:" + editList.size());
////                                        } else {
////                                            rowInfo = null;
////                                            Toast.makeText(this, "Please choose the raw first!", Toast.LENGTH_SHORT).show();
////                                        }
//                                    } else {
//                                        fillTableRow(tableRow, thicknessLocal, widthLocal, lengthLocal, noOfPiecesLocal, noOfRejectedLocal, noOfBundlesLocal);
//                                        tableLayout.addView(tableRow);
//                                    }
//                                    if (!(rowInfo == null))
//                                        newRowList.add(rowInfo);

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

    void doneButtonMethod() {
        String truckNoLocal = truckNo.getText().toString();
        String acceptorLocal = acceptor.getText().toString();
        String ttnNoLocal = ttnNo.getText().toString();
//        String totalBundelsLocal = totalBundles.getText().toString();
        String acceptanceDateLocal = acceptanceDate.getText().toString();
//        String locationLocal = acceptanceLocation.getText().toString();
//        String totalRejectedLocal = totalRejected.getText().toString();

//        Log.e("newRowList", " size" + newRowList.size());

        //                        if (!TextUtils.isEmpty(totalBundelsLocal) && (!checkValidData(totalBundelsLocal)))
//        if (!TextUtils.isEmpty(totalRejectedLocal) && (!checkValidData(totalRejectedLocal)))

        if (edieFlag == 11 && editList.size() > 0)
            newRowList = editList;
        Log.e("ddddddddddddddddddddd", "" + netBundlesString);

        if (newRowList.size() > 0) {
            if (!TextUtils.isEmpty(truckNoLocal)) {
                if (!TextUtils.isEmpty(acceptorLocal))
                    if (!TextUtils.isEmpty(ttnNoLocal))
                        if (!TextUtils.isEmpty(acceptanceDateLocal)) {
                            truckNo.setError(null);
                            ttnNo.setError(null);
                            acceptor.setError(null);

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

                            masterData = new JSONObject();
                            newRowList.get(0).setImageOne(imagesRowInfo.getImageOne());
                            newRowList.get(0).setImageTwo(imagesRowInfo.getImageTwo());
                            newRowList.get(0).setImageThree(imagesRowInfo.getImageThree());
                            newRowList.get(0).setImageFour(imagesRowInfo.getImageFour());
                            newRowList.get(0).setImageFive(imagesRowInfo.getImageFive());
                            newRowList.get(0).setImageSix(imagesRowInfo.getImageSix());
                            newRowList.get(0).setImageSeven(imagesRowInfo.getImageSeven());
                            newRowList.get(0).setImageEight(imagesRowInfo.getImageEight());

                            newRowList.get(0).setImage9(imagesRowInfo.getImage9());
                            newRowList.get(0).setImage10(imagesRowInfo.getImage10());
                            newRowList.get(0).setImage11(imagesRowInfo.getImage11());
                            newRowList.get(0).setImage12(imagesRowInfo.getImage12());
                            newRowList.get(0).setImage13(imagesRowInfo.getImage13());
                            newRowList.get(0).setImage14(imagesRowInfo.getImage14());
                            newRowList.get(0).setImage15(imagesRowInfo.getImage15());


                            masterData = newRowList.get(0).getJsonDataMaster();
//                                Log.e("newRowList", "" + newRowList.get(0).getTruckNo());

                            new JSONTask2().execute();// update

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
            Toast.makeText(this, "Please add rows firs!", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void openCamera() {
        if (imageNo < 8) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            } else {
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, 1888);
//            imageNo = i;
            }
            isCamera = false;
        } else {
            showSnackbar("Reached maximum size of images!", false);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int permission = ActivityCompat.checkSelfPermission(EditPage.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    EditPage.this,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }

        if (requestCode == 1888 && resultCode == RESULT_OK) {
            Bundle intent = data.getExtras();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = getResizedBitmap(bitmap, 100, 100);
            File pictureFile;

            int check;
            if (!isEditImage) {
                imageNo++;
                check = imageNo;
            } else {
                check = editImageNo;
            }

//            Log.e("checkvalue", "" + check);
            if (intent != null) {
                switch (check) {
                    case 1:
                        image1.setVisibility(View.VISIBLE);
                        image1.setImageBitmap(bitmap);
                        imagesList.add(0, bitMapToString(bitmap));
                        break;
                    case 2:
                        image2.setVisibility(View.VISIBLE);
                        image2.setImageBitmap(bitmap);
                        imagesList.add(1, bitMapToString(bitmap));
                        break;
                    case 3:
                        image3.setVisibility(View.VISIBLE);
                        image3.setImageBitmap(bitmap);
                        imagesList.add(2, bitMapToString(bitmap));
                        break;
                    case 4:
                        image4.setVisibility(View.VISIBLE);
                        image4.setImageBitmap(bitmap);
                        imagesList.add(3, bitMapToString(bitmap));
                        break;
                    case 5:
                        image5.setVisibility(View.VISIBLE);
                        image5.setImageBitmap(bitmap);
                        imagesList.add(4, bitMapToString(bitmap));
                        break;
                    case 6:
                        image6.setVisibility(View.VISIBLE);
                        image6.setImageBitmap(bitmap);
                        imagesList.add(5, bitMapToString(bitmap));
                        break;
                    case 7:
                        image7.setVisibility(View.VISIBLE);
                        image7.setImageBitmap(bitmap);
                        imagesList.add(6, bitMapToString(bitmap));
                        break;
                    case 8:
                        image8.setVisibility(View.VISIBLE);
                        image8.setImageBitmap(bitmap);
                        imagesList.add(7, bitMapToString(bitmap));
                        break;
                    case 9:
                        image9.setVisibility(View.VISIBLE);
                        image9.setImageBitmap(bitmap);
                        imagesList.add(8, bitMapToString(bitmap));
                        break;
                    case 10:
                        image10.setVisibility(View.VISIBLE);
                        image10.setImageBitmap(bitmap);
                        imagesList.add(9, bitMapToString(bitmap));
                        break;
                    case 11:
                        image11.setVisibility(View.VISIBLE);
                        image11.setImageBitmap(bitmap);
                        imagesList.add(10, bitMapToString(bitmap));
                        break;
                    case 12:
                        image12.setVisibility(View.VISIBLE);
                        image12.setImageBitmap(bitmap);
                        imagesList.add(11, bitMapToString(bitmap));
                        break;

                    case 13:
                        image13.setVisibility(View.VISIBLE);
                        image13.setImageBitmap(bitmap);
                        imagesList.add(12, bitMapToString(bitmap));
                        break;
                    case 14:
                        image14.setVisibility(View.VISIBLE);
                        image14.setImageBitmap(bitmap);
                        imagesList.add(13, bitMapToString(bitmap));
                        break;
                    case 15:
                        image15.setVisibility(View.VISIBLE);
                        image15.setImageBitmap(bitmap);
                        imagesList.add(14, bitMapToString(bitmap));
                        break;
                }
            }

            isEditImage = false;
        }
        isCamera = true;
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

    public String bitMapToString(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
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
        if ((word.length() == 1) && (word.contains(".")))
            return true;
        else if (((word.length() > 0) && Double.parseDouble(word)==0))
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

    public void getSearchSupplierInfo(String supplierNameLocal, String supplierNoLocal,int updateFlag) {
        supplierName = supplierNameLocal;
        searchSupplier.setText(supplierName);
        searchSupplier.setError(null);
        searchDialog.dismiss();

        if(updateFlag==1) {
            supplierTextTemp.setText(supplierNameLocal);
        }

    }

    void addTableHeader(TableLayout tableLayout) {
        TableRow tableRow = new TableRow(this);
        int max = 8;
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
                    TableRow.LayoutParams deleteParam = new TableRow.LayoutParams(40, 40);
                    imageView.setPadding(0, 10, 0, 10);
                    imageView.setLayoutParams(deleteParam);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_delete_black_24dp));
                    imageView.setBackgroundResource(R.color.orange);
                    tableRow.addView(imageView);
                    break;
                case 9:
                    TableRow.LayoutParams editParam = new TableRow.LayoutParams(40, TableRow.LayoutParams.WRAP_CONTENT);
                    imageView.setPadding(0, 10, 0, 10);
                    imageView.setLayoutParams(editParam);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_edit_24dp));
                    imageView.setBackgroundResource(R.color.orange);
                    tableRow.addView(imageView);
                    break;
            }

        }
        tableLayout.addView(tableRow);
//        bundlesTable.addView(tableRow);
    }

    void fillTableRow(TableRow tableRow, String thicknessText, String widthText, String lengthText, String noOfPiecesText
            , String noOfRejectedText, String noBundleText) {
        int max = 8;
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
                    textView.setText(gradeText);
                    tableRow.addView(textView);
                    break;
                case 8:
                    ImageView imageView2 = new ImageView(this);
                    TableRow.LayoutParams deleteParam = new TableRow.LayoutParams(40, TableRow.LayoutParams.WRAP_CONTENT);
                    deleteParam.setMargins(1, 5, 1, 1);
                    imageView2.setPadding(0, 10, 0, 10);
                    imageView2.setLayoutParams(deleteParam);
                    imageView2.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_delete_forever));
                    imageView2.setBackgroundResource(R.color.light_orange);
                    tableRow.addView(imageView2);
                    break;
                case 9:
                    ImageView imageView = new ImageView(this);
                    TableRow.LayoutParams editParam = new TableRow.LayoutParams(40, 40);
                    editParam.setMargins(1, 5, 1, 1);
                    imageView.setPadding(0, 10, 0, 10);
                    imageView.setLayoutParams(editParam);
                    imageView.setImageDrawable(getResources().getDrawable(R.drawable.ic_baseline_edit_o));
                    imageView.setBackgroundResource(R.color.light_orange);
                    tableRow.addView(imageView);
                    break;
            }
//            tableRow.addView(textView);
        }

    }

    public DatePickerDialog.OnDateSetListener openDatePickerDialog(final int flag) {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

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
//        Log.d("tag", "config changed");
        super.onConfigurationChanged(newConfig);

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
//            int rowcount = tableLayout.getChildCount();
//            for (int i = 0; i < rowcount; i++) {
//                TableRow row = (TableRow) tableLayout.getChildAt(i);
//                tableRows.add(row);
//            }
            outState.putSerializable("table", (Serializable) tableRows);
        }
        outState.putSerializable("list", (Serializable) imagesList);
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
        imagesList.clear();
        imagesList.addAll((Collection<? extends String>) savedInstanceState.getSerializable("list"));
//        Log.e("size a", "" +savedInstanceState.getSerializable("list"));
//        Log.e("size aa", "" +imagesList.size());
        if (isCamera) {
            isCamera = false;
            List<TableRow> tableRows = (List<TableRow>) savedInstanceState.getSerializable("table");

            if (tableRows.size() > 0)
//                addTableHeader(headerTableLayout);

                for (int i = 0; i < tableRows.size(); i++) {
                    if (tableRows.get(i).getParent() != null) {
                        ((ViewGroup) tableRows.get(i).getParent()).removeView(tableRows.get(i)); // <- fix
                    }
//                tableLayout.addView(tableRows.get(i));
                }
        }
        isEditImage = false;
        imageNo = imagesList.size();
        for (int i = 0; i < imagesList.size(); i++)
            switch (i) {
                case 0:
                    image1.setVisibility(View.VISIBLE);
                    image1.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 1:
                    image2.setVisibility(View.VISIBLE);
                    image2.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 2:
                    image3.setVisibility(View.VISIBLE);
                    image3.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 3:
                    image4.setVisibility(View.VISIBLE);
                    image4.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 4:
                    image5.setVisibility(View.VISIBLE);
                    image5.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 5:
                    image6.setVisibility(View.VISIBLE);
                    image6.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 6:
                    image7.setVisibility(View.VISIBLE);
                    image7.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 7:
                    image8.setVisibility(View.VISIBLE);
                    image8.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;

                case 8:
                    image9.setVisibility(View.VISIBLE);
                    image9.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 9:
                    image10.setVisibility(View.VISIBLE);
                    image10.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 10:
                    image11.setVisibility(View.VISIBLE);
                    image11.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 11:
                    image12.setVisibility(View.VISIBLE);
                    image12.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 12:
                    image13.setVisibility(View.VISIBLE);
                    image13.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 13:
                    image14.setVisibility(View.VISIBLE);
                    image14.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;
                case 14:
                    image15.setVisibility(View.VISIBLE);
                    image15.setImageBitmap(stringToBitMap(imagesList.get(i)));
                    break;


            }


        super.onRestoreInstanceState(savedInstanceState);
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
                adapter.notifyDataSetChanged();

            } else {
                Toast.makeText(EditPage.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
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
                nameValuePairs.add(new BasicNameValuePair("UPDATE_RAW_INFO_TWO", "1"));// list
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
//                    tableLayout.removeAllViews();
                    newRowList.clear();

                    Intent it;
                    if (edieFlag == 10)
                        it = new Intent(EditPage.this, AcceptanceInfoReport.class);
                    else
                        it = new Intent(EditPage.this, AcceptanceReport.class);
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
                Toast.makeText(EditPage.this, "Failed to export data Please check internet connection", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ******************************************** GET DETAILS ***************************************************
    private class JSONTask3 extends AsyncTask<String, String, List<NewRowInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected List<NewRowInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
//                http://10.0.0.22/woody/import.php?FLAG=2
                URL url = new URL("http://" + generalSettings.getIpAddress() + "/import.php?FLAG=15&RAW_SERIAL=" + editSerial);

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
                Log.e("JSONTask3", url + " : " + finalJson);

                editList.clear();
                Gson gson = new Gson();
                NewRowInfo list = gson.fromJson(finalJson, NewRowInfo.class);
                if (list != null)
                    editList.addAll(list.getDetails());
                else
                    editList = null;

            } catch (MalformedURLException e) {
                Log.e("Customer", "********ex1");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Customer", e.getMessage().toString());
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
            return editList;
        }

        @Override
        protected void onPostExecute(List<NewRowInfo> s) {
            super.onPostExecute(s);
            if (s == null)
                Log.e("tag", "JSONTask3/Failed to export data Please check internet connection");
            else {
                editPageAdapter = new EditPageAdapter(EditPage.this, editList);
                rowsRecyclerView.setAdapter(editPageAdapter);
                rejectAdd();
            }

        }
    }

}