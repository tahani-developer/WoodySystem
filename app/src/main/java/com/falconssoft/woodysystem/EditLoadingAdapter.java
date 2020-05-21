package com.falconssoft.woodysystem;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class EditLoadingAdapter extends BaseAdapter {

    /**
     *     <LinearLayout
     *         android:paddingVertical="5dp"
     *         android:paddingHorizontal="10dp"
     *         android:layout_marginTop="2dp"
     *         android:orientation="horizontal"
     *         android:background="@color/light_orange"
     *         android:layout_width="match_parent"
     *         android:layout_height="wrap_content">
     *
     *         <TextView
     *             android:id="@+id/bundle"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="2"/>
     *
     *         <EditText
     *             android:id="@+id/th"
     *             android:layout_width="0dp"
     *             android:layout_height="wrap_content"
     *             android:layout_weight="1"
     *             android:textColor="@color/orange"
     *             android:textSize="15sp" />
     *
     *         <EditText
     *             android:id="@+id/w"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *         <EditText
     *             android:id="@+id/l"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *         <Spinner
     *             android:id="@+id/grade"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *         <EditText
     *             android:id="@+id/pcs"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *         <TextView
     *             android:id="@+id/location"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *         <Spinner
     *             android:id="@+id/area"
     *             android:textSize="15sp"
     *             android:textColor="@color/orange"
     *             android:layout_width="0dp"
     *             android:layout_height="match_parent"
     *             android:layout_weight="1"/>
     *
     *
     *     </LinearLayout>
     * */

    private EditLoadingReport context;
    private List<BundleInfo> mOriginalValues;
    private static List<BundleInfo> itemsList;
    private static List<BundleInfo> selectedBundles;
    private List<String> gradeList = new ArrayList<>();
    private List<String> areaList = new ArrayList<>();
    private ArrayAdapter<String> gradeAdapter, areaAdapter;


    public EditLoadingAdapter(EditLoadingReport context, List<BundleInfo> itemsList) {
        this.context = context;
        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        selectedBundles = new ArrayList<>();
    }


    public void setItemsList(List<BundleInfo> itemsList) {
        this.itemsList = itemsList;
    }

    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    private class ViewHolder {
        TextView bundle,location;
        EditText th, w, l, pcs;
        Spinner grade, area;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final EditLoadingAdapter.ViewHolder holder = new EditLoadingAdapter.ViewHolder();
        view = View.inflate(context, R.layout.edit_loading_report_slot, null);//item_row2

        holder.th = view.findViewById(R.id.th);
        holder.w = view.findViewById(R.id.w);
        holder.l = view.findViewById(R.id.l);
        holder.pcs = view.findViewById(R.id.pcs);
        holder.bundle = view.findViewById(R.id.bundle);
        holder.grade = view.findViewById(R.id.grade);
        holder.location = view.findViewById(R.id.location);
        holder.area = view.findViewById(R.id.area);


        holder.th.setText("" + itemsList.get(i).getThickness());
        holder.w.setText("" + itemsList.get(i).getWidth());
        holder.l.setText("" + itemsList.get(i).getLength());
        holder.pcs.setText("" + itemsList.get(i).getNoOfPieces());
        holder.bundle.setText("" + itemsList.get(i).getBundleNo());
        holder.location.setText("" + itemsList.get(i).getLocation());
        fillSpinnerAdapter(holder.grade, holder.area);
//        holder.grade.setText("" + itemsList.get(i).getGrade());
//        holder.area.setText("" + itemsList.get(i).getArea());

        return view;
    }

    void fillSpinnerAdapter(Spinner gradeSpinner, Spinner areaSpinner) {
        gradeList.clear();
        areaList.clear();

        gradeList.add("Fresh");
        gradeList.add("BS");
        gradeList.add("Reject");
        gradeList.add("KD");
        gradeList.add("KD Blue Stain");
        gradeList.add("Second Sort");
        gradeAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, gradeList);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gradeSpinner.setAdapter(gradeAdapter);
        gradeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        areaList.add("Zone 1");
        areaList.add("Zone 2");
        areaList.add("Zone 3");
        areaList.add("Zone 4");
        areaList.add("Zone 5");
        areaAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        areaSpinner.setAdapter(areaAdapter);
        areaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }


}
