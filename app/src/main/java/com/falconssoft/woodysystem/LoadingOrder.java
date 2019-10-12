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


    GridView items;
    Button done;
    SearchView searchViewTh, searchViewW, searchViewL;

    DatabaseHandler DHandler;
    List<BundleInfo> bundles, filteredList;

    String f1 = "", f2 = "", f3 = "";


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
        filteredList = new ArrayList<>();

        ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, bundles);
        items.setAdapter(adapter);

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

        searchViewTh.setOnQueryTextListener(new android.support.v7.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {

                    f1 = query;
                    List filteredList = filter (bundles, f1, f2, f3);
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                    items.setAdapter(adapter);

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

                    f2 = query;
                    List filteredList = filter (bundles, f1, f2, f3);
                    ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                    items.setAdapter(adapter);

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

                f3 = query;
                List filteredList = filter (bundles, f1, f2, f3);
                ItemsListAdapter adapter = new ItemsListAdapter(LoadingOrder.this, filteredList);
                items.setAdapter(adapter);

                return false;
            }
        });
    }

    List<BundleInfo> filter(List<BundleInfo> list, String s1, String s2, String s3){
        List<BundleInfo> tempList = new ArrayList<>();
        for (int k = 0; k < list.size(); k++) {
            if (
                    (("" + list.get(k).getThickness()).toUpperCase().startsWith(s1) || s1.equals("")) &&
                    (("" + list.get(k).getWidth()).toUpperCase().startsWith(s2) || s2.equals("")) &&
                    (("" + list.get(k).getLength()).toUpperCase().startsWith(s3) || s3.equals("")) )
                tempList.add(list.get(k));
        }

        return tempList;
    }

    boolean listContainsItems() {
        ItemsListAdapter obj = new ItemsListAdapter();
        List<BundleInfo> bundles = obj.getSelectedItems();

        boolean test = false;
        for (int i = 0; i < bundles.size(); i++) {
            if (bundles.get(i).getChecked()) {
                test = true;
                break;
            }
        }
        return test;
    }
}
