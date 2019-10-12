package com.falconssoft.woodysystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.support.v7.widget.SearchView;

import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

public class LoadingOrder extends AppCompatActivity {

    Button barcode;
    ImageButton deleteBarcode;
    String barcodeValue="";
    private GridView items;
    private Button done;
    private SearchView searchViewTh, searchViewW, searchViewL;
    private DatabaseHandler DHandler ;
    private List<BundleInfo> bundles ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading_order);

        items = (GridView) findViewById(R.id.items);
        searchViewTh = (SearchView) findViewById(R.id.mSearchTh);
        searchViewW = (SearchView) findViewById(R.id.mSearchW);
        searchViewL = (SearchView) findViewById(R.id.mSearchL);
        done = (Button) findViewById(R.id.done);
        barcode = (Button) findViewById(R.id.barcode);
        deleteBarcode = (ImageButton) findViewById(R.id.deletebaarcode);
        DHandler = new DatabaseHandler(LoadingOrder.this);
        bundles = DHandler.getBundleInfo();

        ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
        items.setAdapter(adapter);
        final Activity activity = this;

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

                if(listContainsItems()) {
                    Intent intent = new Intent(LoadingOrder.this, LoadingOrder2.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(LoadingOrder.this , "No item selected !" , Toast.LENGTH_LONG).show();
                }
            }
        });

        deleteBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                items.setAdapter(adapter);

            }
        });

        searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query != null && query.length() > 0) {
                    ArrayList<BundleInfo> filteredList = new ArrayList<>();
                    for (int k = 0; k < bundles.size(); k++) {
                        if (("" +bundles.get(k).getThickness()).toUpperCase().contains(query))
                            filteredList.add(bundles.get(k));
                    }
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                    items.setAdapter(adapter);
                } else {
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                    items.setAdapter(adapter);
                }
                return false;
            }
        });
        searchViewW.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query != null && query.length() > 0) {
                    ArrayList<BundleInfo> filteredList = new ArrayList<>();
                    for (int k = 0; k < bundles.size(); k++) {
                        if (("" +bundles.get(k).getWidth()).toUpperCase().contains(query))
                            filteredList.add(bundles.get(k));
                    }
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                    items.setAdapter(adapter);
                } else {
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                    items.setAdapter(adapter);
                }
                return false;
            }
        });

        searchViewL.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                if (query != null && query.length() > 0) {
                    ArrayList<BundleInfo> filteredList = new ArrayList<>();
                    for (int k = 0; k < bundles.size(); k++) {
                        if (("" +bundles.get(k).getLength()).toUpperCase().contains(query))
                            filteredList.add(bundles.get(k));
                    }
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                    items.setAdapter(adapter);
                } else {
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
                    items.setAdapter(adapter);
                }
                return false;
            }
        });
    }

    boolean listContainsItems(){
        ItemsListAdapter obj = new ItemsListAdapter();
        List<BundleInfo> bundles = obj.getSelectedItems();

        boolean test = false;
        for(int i=0 ; i<bundles.size(); i++){
            if(bundles.get(i).getChecked()) {
                test = true;
                break;
            }
        }
        return test;
    }

    void searchByBundleNo(String Bundul){

        Log.e("searchByBundleNo ",""+barcodeValue+"\n"+"th ="+Bundul);


        if (!barcodeValue.equals("cancelled")) {
            ArrayList<BundleInfo> filteredList = new ArrayList<>();
            for (int k = 0; k < bundles.size(); k++) {
                if ((bundles.get(k).getBundleNo()).equals(Bundul)) {
                    filteredList.add(bundles.get(k));
                    Log.e("searchByBundleNo ", "" + filteredList.get(filteredList.size() - 1).getBundleNo());
                }
            }
            ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
            items.setAdapter(adapter);
        } else {
            ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
            items.setAdapter(adapter);
        }

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        IntentResult Result = IntentIntegrator.parseActivityResult(requestCode , resultCode ,data);
        if(Result != null){
            if(Result.getContents() == null){
                Log.d("MainActivity" , "cancelled scan");
                Toast.makeText(this, "cancelled", Toast.LENGTH_SHORT).show();
                barcodeValue="cancelled";
            }
            else {
                Log.d("MainActivity" , "Scanned");
                Toast.makeText(this,"Scanned -> " + Result.getContents(), Toast.LENGTH_SHORT).show();

                barcodeValue=Result.getContents();
//                String[] arrayString = barcodeValue.split(" ");

//Log.e("barcode_value ",""+barcodeValue+"\n"+"th ="+arrayString[0]+"\n"+"w ="+arrayString[1]+"\n"+"l ="
//        +arrayString[2]+"\n"+"grad ="+arrayString[3]);
                searchByBundleNo(barcodeValue);

            }
        }
        else {
            super.onActivityResult(requestCode , resultCode , data);
        }
    }





}
