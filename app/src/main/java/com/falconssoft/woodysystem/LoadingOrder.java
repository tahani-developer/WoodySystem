package com.falconssoft.woodysystem;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.support.v7.widget.SearchView;

import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class LoadingOrder extends AppCompatActivity {

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


        DHandler = new DatabaseHandler(LoadingOrder.this);
        bundles = DHandler.getBundleInfo();

        ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
        items.setAdapter(adapter);

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
}
