package com.falconssoft.woodysystem;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.SpinnerModel;
import com.falconssoft.woodysystem.reports.InventoryReport;
import com.falconssoft.woodysystem.reports.InventoryReportAdapter;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.falconssoft.woodysystem.ItemsListAdapter.selectedBundles;
import static com.falconssoft.woodysystem.Stage3.flagOpenJ;

public class LoadingOrder extends AppCompatActivity implements View.OnClickListener {

    private ImageButton deleteBarcode;
    private GridView items;
    private Button done, barcode, addBundle;
    private EditText searchViewTh, searchViewW, searchViewL, searchViewPices;
    private DatabaseHandler DHandler;
    public static List<BundleInfo> bundles, filteredList;//, selectedBundle;
    private String f1 = "", f2 = "", f3 = "", f4 = "", barcodeValue = "", loc, widthField = "All", lengthField = "All", thicknessField = "All";
    public static ItemsListAdapter adapter; // original list
    private Activity activity;
    public static TextView searchBar;
    private int no = 0;
    public static HashMap<String, BundleInfo> motherList = new HashMap<>();
    private TextView resetThicknessList, resetWidthList, resetLengthList;

    static ListView listView2;
    static HorizontalListView listView;
    private ItemsListAdapter5 adapter2;// choosed bundles
    private final String STATE_VISIBILITY = "state-visibility";
    private final String LENGTH_LIST = "length-list";
    private final String WIDTH_LIST = "width-list";
    private final String THICK_LIST = "thick-list";

    private boolean mState = false;
    private ProgressDialog progressDialog;
    private List<BundleInfo> tempList = new ArrayList<>();

    private Spinner thicknessSpinner, widthSpinner, lengthSpinner;
    private SpinnerCustomAdapter thicknessAdapter;
    private SpinnerCustomAdapter widthAdapter;
    private SpinnerCustomAdapter lengthAdapter;
    private List<SpinnerModel> thicknessChecked = new ArrayList<>();
    private List<SpinnerModel> lengthChecked = new ArrayList<>();
    private List<SpinnerModel> widthChecked = new ArrayList<>();
    private List<Double> doubleList;

    private List<String> thickness = new ArrayList<>();
    private List<String> length = new ArrayList<>();
    private List<String> width = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order);

        Log.e("oncreate", "test");

        items = findViewById(R.id.items);
        searchViewTh = findViewById(R.id.mSearchTh);
        searchViewW = findViewById(R.id.mSearchW);
        searchViewL = findViewById(R.id.mSearchL);
        searchViewPices = findViewById(R.id.mSearchPieces);
        done = findViewById(R.id.done);
        barcode = findViewById(R.id.barcode);
        addBundle = findViewById(R.id.add);
        deleteBarcode = findViewById(R.id.deletebaarcode);
        listView2 = findViewById(R.id.verticalListView);
        listView = findViewById(R.id.listview);
        resetThicknessList = findViewById(R.id.loading_order_thick_reset);
        resetWidthList = findViewById(R.id.loading_order_width_reset);
        resetLengthList = findViewById(R.id.loading_order_length_reset);
        adapter = new ItemsListAdapter();

        lengthSpinner = findViewById(R.id.loading_order_length_spinner);
        widthSpinner = findViewById(R.id.loading_order_width_spinner);
        thicknessSpinner = findViewById(R.id.loading_order_thick_spinner);
        progressDialog = new ProgressDialog(this, R.style.MyAlertDialogStyle);
        progressDialog.setMessage("Please Waiting...");

        DHandler = new DatabaseHandler(LoadingOrder.this);
        loc = DHandler.getSettings().getStore();
//        bundles = DHandler.getBundleInfo();
        searchBar = findViewById(R.id.searchBar);
        selectedBundles = new ArrayList<>();

        resetThicknessList.setOnClickListener(this);
        resetWidthList.setOnClickListener(this);
        resetLengthList.setOnClickListener(this);

        if (flagOpenJ == 0) {
            bundles = new ArrayList<>();
            new JSONTask().execute();
            flagOpenJ = 1;
        } else {
            filter();
        }

        filteredList = new ArrayList<>();


        activity = this;

        barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//
                IntentIntegrator intentIntegrator = new IntentIntegrator(activity);
                intentIntegrator.setDesiredBarcodeFormats(intentIntegrator.ALL_CODE_TYPES);
                intentIntegrator.setBeepEnabled(true);
                intentIntegrator.setCameraId(0);
                intentIntegrator.setOrientationLocked(true);
                intentIntegrator.setPrompt("SCAN");
                intentIntegrator.setBarcodeImageEnabled(false);
                intentIntegrator.initiateScan();

//                Intent intent=new Intent(LoadingOrder.this,QrReader.class);
//                startActivity(intent);
            }
        });


        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listContainsItems()) {
                    Intent intent = new Intent(LoadingOrder.this, LoadingOrder2.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(LoadingOrder.this, "No item selected !", Toast.LENGTH_LONG).show();
                }
            }
        });

        deleteBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filter();
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles, true);
//                items.setAdapter(adapter);

            }
        });

        addBundle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(LoadingOrder.this, AddToInventory.class);
                intent.putExtra("flag", "1");
                startActivity(intent);
            }
        });

        searchViewTh.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f1 = String.valueOf(s);
                filter();
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f1 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchBar.getText().toString().equals("1")) {
                    searchByBundleNo(barcodeValue);
                } else if (searchBar.getText().toString().equals("2")) {
                    Intent i = new Intent(LoadingOrder.this, LoadingOrder.class);
                    finish();
                    overridePendingTransition(0, 0);
                    startActivity(i);
                    overridePendingTransition(0, 0);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchViewW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f2 = String.valueOf(s);
                filter();
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewW.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f2 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });

        searchViewL.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f3 = String.valueOf(s);
                filter();
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
//        searchViewL.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String query) {
//
//                f3 = query;
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
//
//                return false;
//            }
//        });
        searchViewPices.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                f4 = String.valueOf(s);
                filter();
//                filteredList = filter(bundles, f1, f2, f3);
//                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList, false);
//                items.setAdapter(adapter);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    void filter() {
        tempList.clear();
//        if (flag == 0) {
////            tempList.addAll(bundles);
//            adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//            items.setAdapter(adapter);
//        } else {

        for (int k = 0; k < bundles.size(); k++) {
            bundles.get(k).setIndex(k);
            if (
                    (("" + bundles.get(k).getThickness()).toUpperCase().startsWith(f1) || f1.equals("")) &&
                            (("" + bundles.get(k).getWidth()).toUpperCase().startsWith(f2) || f2.equals("")) &&
                            (("" + bundles.get(k).getLength()).toUpperCase().startsWith(f3) || f3.equals("")) &&
                            (("" + bundles.get(k).getNoOfPieces()).toUpperCase().startsWith(f4) || f4.equals("")))
                if ((thicknessChecked.size() == 0) || checkIfItemChecked(thicknessChecked, String.valueOf(bundles.get(k).getThickness())))
                    if ((widthChecked.size() == 0) || checkIfItemChecked(widthChecked, String.valueOf(bundles.get(k).getWidth())))
                        if ((lengthChecked.size() == 0) || checkIfItemChecked(lengthChecked, String.valueOf(bundles.get(k).getLength()))) {
                            bundles.get(k).setFoucoseColor("0");
                            tempList.add(bundles.get(k));
                        }
        }


        adapter = new ItemsListAdapter(LoadingOrder.this, tempList, false);
        items.setAdapter(adapter);

//        return tempList;
    }

    boolean listContainsItems() {
//        ItemsListAdapter obj = new ItemsListAdapter();
//        List<BundleInfo> bundles = adapter.getSelectedItems();
        selectedBundles = adapter.getSelectedItems();
        Log.e("nowSize", "" + selectedBundles.size());
        boolean test = false;
        for (int i = 0; i < selectedBundles.size(); i++) {
            if (selectedBundles.get(i).getChecked()) {
                test = true;
                break;
            }
        }
        return test;
    }

    void searchByBundleNo(String Bundul) {

        Log.e("searchByBundleNo ", "" + barcodeValue + "\n" + "th =" + Bundul);

        no = 0;

        if (!barcodeValue.equals("cancelled")) {
            for (int k = 0; k < bundles.size(); k++) {
                if ((bundles.get(k).getBundleNo()).equals(Bundul)) {
                    no = k;
//                    try {
//                        Log.e("searchByBundleNo_13 ", "" + barcodeValue + "\n" + "th =" + no );
//                        Thread.sleep(120);
////                        items.setSelection(no);
//                        items.requestFocusFromTouch();
//                        items.setSelection(no);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }


                    items.clearFocus();
//                    items.post(new Runnable() {
//                        @Override
//                        public void run() {
                    bundles.get(no).setFoucoseColor("1");
                    items.setAdapter(adapter);
                    filter();
                    items.setSelection(no);
                    items.requestFocusFromTouch();
                    items.setSelection(no);

//                    items.smoothScrollToPosition(no);

//                        }
//                    });


                    break;
                }
            }

        } else {
            filter();
//            ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//            items.setAdapter(adapter);
        }

    }

    @Override
    public void onClick(View v) {
        int flag = 0;
        switch (v.getId()) {
            case R.id.loading_order_width_reset:
                widthChecked = new ArrayList<>();
                List<SpinnerModel> widthCheckList = new ArrayList<>();
                for (int i = 0; i < width.size(); i++) {
                    SpinnerModel spinnerModel = new SpinnerModel();
                    spinnerModel.setTitle(width.get(i));
                    spinnerModel.setChecked(false);
                    widthCheckList.add(spinnerModel);
                }
                widthCheckList.add(0, new SpinnerModel("       ", false));
                widthAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, widthCheckList, 2, 3);
                widthSpinner.setAdapter(widthAdapter);
                filter();
                break;
            case R.id.loading_order_length_reset:
                lengthChecked = new ArrayList<>();
                List<SpinnerModel> lengthCheckList = new ArrayList<>();
                for (int i = 0; i < length.size(); i++) {
                    SpinnerModel spinnerModel = new SpinnerModel();
                    spinnerModel.setTitle(length.get(i));
                    spinnerModel.setChecked(false);
                    lengthCheckList.add(spinnerModel);
                }
                lengthCheckList.add(0, new SpinnerModel("       ", false));
                lengthAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, lengthCheckList, 3, 3);
                lengthSpinner.setAdapter(lengthAdapter);
                filter();
                break;
            case R.id.loading_order_thick_reset:
                thicknessChecked = new ArrayList<>();
                List<SpinnerModel> thicknessCheckList = new ArrayList<>();
                for (int i = 0; i < thickness.size(); i++) {
                    SpinnerModel spinnerModel = new SpinnerModel();
                    spinnerModel.setTitle(thickness.get(i));
                    spinnerModel.setChecked(false);
                    thicknessCheckList.add(spinnerModel);
                }
                thicknessCheckList.add(0, new SpinnerModel("       ", false));
                thicknessAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, thicknessCheckList, 1, 3);
                thicknessSpinner.setAdapter(thicknessAdapter);
                filter();
                break;
        }

    }

    public void fillSpinnerAdapter(List<String> thicknessList, List<String> widthList, List<String> lengthList) {

        List<SpinnerModel> thicknessCheckList = new ArrayList<>();
        List<SpinnerModel> widthCheckList = new ArrayList<>();
        List<SpinnerModel> lengthCheckList = new ArrayList<>();

//        showLog("fillSpinnerAdapter", "thickness size", "" + thicknessList.size());
        removeDuplicate(thicknessList);
        removeDuplicate(widthList);
        removeDuplicate(lengthList);
//        showLog("fillSpinnerAdapter", "thickness size", "" + thicknessList.size());

//        lengthList.add(0, "All");
//        lengthAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, lengthList);
//        lengthAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        lengthSpinner.setAdapter(lengthAdapter);
        for (int i = 0; i < lengthList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(lengthList.get(i));
            spinnerModel.setChecked(false);
            lengthCheckList.add(spinnerModel);
        }
        lengthCheckList.add(0, new SpinnerModel("       ", false));
        lengthAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, lengthCheckList, 3, 3);
        lengthSpinner.setAdapter(lengthAdapter);

//        widthList.add(0, "All");
//        widthAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, widthList);
//        widthAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        widthSpinner.setAdapter(widthAdapter);
        for (int i = 0; i < widthList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(widthList.get(i));
            spinnerModel.setChecked(false);
            widthCheckList.add(spinnerModel);
        }
        widthCheckList.add(0, new SpinnerModel("       ", false));
        widthAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, widthCheckList, 2, 3);
        widthSpinner.setAdapter(widthAdapter);

//        thicknessList.add(0, "All");
//        thicknessAdapter = new ArrayAdapter<>(this, R.layout.spinner_layout, thicknessList);
//        thicknessAdapter.setDropDownViewResource(R.layout.spinner_drop_down_layout);
//        thicknessSpinner.setAdapter(thicknessAdapter);
        for (int i = 0; i < thicknessList.size(); i++) {
            SpinnerModel spinnerModel = new SpinnerModel();
            spinnerModel.setTitle(thicknessList.get(i));
            spinnerModel.setChecked(false);
            thicknessCheckList.add(spinnerModel);
//            this.thicknessList.add(thicknessList.get(i));
        }
        thicknessCheckList.add(0, new SpinnerModel("       ", false));
        thicknessAdapter = new SpinnerCustomAdapter(LoadingOrder.this, 0, thicknessCheckList, 1, 3);
        thicknessSpinner.setAdapter(thicknessAdapter);

    }

    void removeDuplicate(List<String> list) {
        Set<String> set = new HashSet<>(list);
        list.clear();
        list.addAll(set);
        doubleList = new ArrayList<>();
        for (int n = 0; n < list.size(); n++)
            doubleList.add(Double.valueOf(list.get(n)));

        Collections.sort(doubleList);
        list.clear();
        for (int n = 0; n < doubleList.size(); n++)
            list.add(String.valueOf(doubleList.get(n)));

    }

    boolean checkIfItemChecked(List<SpinnerModel> list, String value) {
        for (int i = 0; i < list.size(); i++)
            if (value.equals(list.get(i).getTitle()))
                return true;

        return false;
    }

    public void sendOtherLists(List<SpinnerModel> list, int flag) {
        switch (flag) {
            case 1://thickness
                if (list != null || list.size() != 0) {
                    thicknessChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            thicknessChecked.add(list.get(i));
                    Log.e("follow/", "size3/thicknessChecked/" + thicknessChecked.size());
                    filter();
                }
                break;
            case 2://width
                if (list != null || list.size() != 0) {
                    widthChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            widthChecked.add(list.get(i));
                    Log.e("follow/", "size3/widthChecked/" + widthChecked.size());
                    filter();
                }
                break;
            case 3://length
                if (list != null || list.size() != 0) {
                    lengthChecked = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++)
                        if (list.get(i).isChecked())
                            lengthChecked.add(list.get(i));
                    Log.e("follow/", "size3/lengthChecked/" + lengthChecked.size());
                    filter();
                }
                break;
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (Result != null) {
            if (Result.getContents() == null) {
                Log.d("MainActivity", "cancelled scan");
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                barcodeValue = "cancelled";
                searchBar.setText("0");
            } else {
                Log.d("MainActivity", "Scanned");
                Toast.makeText(this, "Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();

                barcodeValue = Result.getContents();
//                String[] arrayString = barcodeValue.split(" ");

//Log.e("barcode_value ",""+barcodeValue+"\n"+"th ="+arrayString[0]+"\n"+"w ="+arrayString[1]+"\n"+"l ="
//        +arrayString[2]+"\n"+"grad ="+arrayString[3]);
//                searchByBundleNo(barcodeValue);
                searchBar.setText("1");
//                searchByBundleNo(barcodeValue);
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
//        Intent intent = new Intent(LoadingOrder.this, Stage3.class);
//        startActivity(intent);
        flagOpenJ = 0;
        finish();
    }

    private class JSONTask extends AsyncTask<String, String, List<BundleInfo>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.show();
        }

        @Override
        protected List<BundleInfo> doInBackground(String... params) {
            URLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL("http://" + DHandler.getSettings().getIpAddress() + "/import.php?FLAG=3");

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
                // Log.e("finalJson*********", finalJson);

                JSONObject parentObject = new JSONObject(finalJson);

                try {
                    JSONArray parentArrayOrders = parentObject.getJSONArray("BUNDLE_INFO");
                    motherList = new HashMap<>();
                    thickness.clear();
                    length.clear();
                    width.clear();

                    for (int i = 0; i < parentArrayOrders.length(); i++) {
                        JSONObject innerObject = parentArrayOrders.getJSONObject(i);

                        if (innerObject.getInt("ORDERED") == 0
                                && innerObject.getString("LOCATION").equals(loc)) {

                            BundleInfo bundleInfo = new BundleInfo();
                            bundleInfo.setThickness(innerObject.getDouble("THICKNESS"));
                            bundleInfo.setWidth(innerObject.getDouble("WIDTH"));
                            bundleInfo.setLength(innerObject.getDouble("LENGTH"));
                            bundleInfo.setGrade(innerObject.getString("GRADE"));
                            bundleInfo.setNoOfPieces(innerObject.getDouble("PIECES"));
                            bundleInfo.setBundleNo(innerObject.getString("BUNDLE_NO"));
                            bundleInfo.setLocation(innerObject.getString("LOCATION"));
                            bundleInfo.setArea(innerObject.getString("AREA"));
                            bundleInfo.setBarcode(innerObject.getString("BARCODE"));
                            bundleInfo.setOrdered(innerObject.getInt("ORDERED"));
                            bundleInfo.setAddingDate(innerObject.getString("BUNDLE_DATE"));
                            bundleInfo.setBackingList(innerObject.getString("BACKING_LIST"));
                            bundleInfo.setCustomer(innerObject.getString("CUSTOMER"));
                            bundleInfo.setSerialNo(innerObject.getString("B_SERIAL"));
                            bundleInfo.setChecked(false);
                            bundleInfo.setFoucoseColor("0");
//                            bundleInfo.setIndex(i);

                            bundles.add(bundleInfo);
                            motherList.put(innerObject.getString("BUNDLE_NO"), bundleInfo);
                            thickness.add(String.valueOf(innerObject.getDouble("THICKNESS")));
                            width.add(String.valueOf(innerObject.getDouble("WIDTH")));
                            length.add(String.valueOf(innerObject.getDouble("LENGTH")));
                        }
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
            return bundles;
        }


        @Override
        protected void onPostExecute(final List<BundleInfo> result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            if (result != null) {
                fillSpinnerAdapter(thickness, width, length);

                Log.e("result", "*****************" + result.size());
                filter();
//                adapter = new ItemsListAdapter(LoadingOrder.this, bundles, false);
//                items.setAdapter(adapter);

                adapter2 = new ItemsListAdapter5(LoadingOrder.this, selectedBundles);
                listView2.setAdapter(adapter2);
                listView.setAdapter(adapter2);

            } else {
                Toast.makeText(LoadingOrder.this, "Not able to fetch data from server, please check url.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(STATE_VISIBILITY, mState);
        outState.putStringArrayList(LENGTH_LIST, (ArrayList<String>) length);
        outState.putStringArrayList(WIDTH_LIST, (ArrayList<String>) width);
        outState.putStringArrayList(THICK_LIST, (ArrayList<String>) thickness);

//        outState.putSerializable("selectedBundle", (Serializable) selectedBundle);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        filter();
        selectedBundles = adapter.getSelectedItems();
        Log.e("now", "onRestoreInstanceState: " + selectedBundles.size());
        adapter2 = new ItemsListAdapter5(LoadingOrder.this, selectedBundles);
        listView2.setAdapter(adapter2);
        listView.setAdapter(adapter2);
        length.clear();
        length.addAll(savedInstanceState.getStringArrayList(LENGTH_LIST));
        width.clear();
        width.addAll(savedInstanceState.getStringArrayList(WIDTH_LIST));
        thickness.clear();
        thickness.addAll(savedInstanceState.getStringArrayList(THICK_LIST));
        fillSpinnerAdapter(thickness, width, length);

        super.onRestoreInstanceState(savedInstanceState);
    }

    public void notifyAdapter(BundleInfo bundle, Context context) {

//        if (bundle.getChecked()) {
//            selectedBundle.add(bundle);
//        } else {
//
//            for (int i = 0; i < selectedBundle.size(); i++) {
//                if (bundle.getBundleNo().equals(selectedBundle.get(i).getBundleNo())) {
//                    selectedBundle.remove(i);
//                    break;
//                }
//            }
//        }

        ItemsListAdapter obj = new ItemsListAdapter();
        selectedBundles = obj.getSelectedItems();
        adapter2 = new ItemsListAdapter5(context, selectedBundles);
        listView2.setAdapter(adapter2);
        listView.setAdapter(adapter2);


    }

}
