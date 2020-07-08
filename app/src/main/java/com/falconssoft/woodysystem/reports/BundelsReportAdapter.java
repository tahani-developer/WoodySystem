package com.falconssoft.woodysystem.reports;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class BundelsReportAdapter extends BaseAdapter {

    private Context context;
    private static List<BundleInfo> itemsList;
    private static List<BundleInfo> selectedBundles ;

    public BundelsReportAdapter(Context context, List<BundleInfo> itemsList) {
        this.context = context;
//        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        selectedBundles = new ArrayList<>();
    }
    @Override
    public int getCount() {
        return itemsList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        final BundelsReportAdapter.ViewHolder holder = new BundelsReportAdapter.ViewHolder();
        view = View.inflate(context, R.layout.bundels_report_row, null);

        holder.checkBox = (CheckBox) view.findViewById(R.id.checkBoxPrinter);
        holder.serial = (TextView) view.findViewById(R.id.bundlesReport_row_serial);
        holder.th = (TextView) view.findViewById(R.id.bundlesReport_row_thickness);
        holder.w = (TextView) view.findViewById(R.id.bundlesReport_row_width);
        holder.l = (TextView) view.findViewById(R.id.bundlesReport_row_length);
        holder.grade = (TextView) view.findViewById(R.id.bundlesReport_row_grade);
        holder.pcs = (TextView) view.findViewById(R.id.bundlesReport_row_noOfPieces);
        holder.bundle = (TextView) view.findViewById(R.id.bundlesReport_row_bundleNo);
        holder.location = (TextView) view.findViewById(R.id.bundlesReport_row_location);
        holder.area = (TextView) view.findViewById(R.id.bundlesReport_row_area);
        holder.pL = (TextView) view.findViewById(R.id.bundlesReport_row_packingList);

        holder.serial.setText("" + itemsList.get(i).getSerialNo());
        holder.th.setText("" + (int) itemsList.get(i).getThickness());
        holder.w.setText("" + (int) itemsList.get(i).getWidth());
        holder.l.setText("" +  (int)itemsList.get(i).getLength());
        holder.grade.setText("" + itemsList.get(i).getGrade());
        holder.pcs.setText("" + (int) itemsList.get(i).getNoOfPieces());
        holder.bundle.setText("" + itemsList.get(i).getBundleNo());
        holder.location.setText("" + itemsList.get(i).getLocation());
        holder.area.setText("" + itemsList.get(i).getArea());
        holder.pL.setText("" + itemsList.get(i).getBackingList());

        if (itemsList.get(i).getChecked())
            holder.checkBox.setChecked(true);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    itemsList.get(i).setChecked(true);
                else
                    itemsList.get(i).setChecked(false);
            }
        });

        return view;
    }

    public List<BundleInfo> getSelectedItems() {

        selectedBundles.clear();
        for(int i = 0 ; i< itemsList.size() ; i++)
            if(itemsList.get(i).getChecked())
                selectedBundles.add(itemsList.get(i));

        return selectedBundles;
    }

    private class ViewHolder {
        CheckBox checkBox;
        TextView serial, bundle, th, w, l, grade, pcs, location, area, pL;
    }
}
