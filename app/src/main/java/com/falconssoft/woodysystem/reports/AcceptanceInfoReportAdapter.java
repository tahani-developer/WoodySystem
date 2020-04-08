package com.falconssoft.woodysystem.reports;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;

import java.util.ArrayList;
import java.util.List;

import static com.falconssoft.woodysystem.stage_one.AddNewRaw.truckNoBeforeUpdate;

public class AcceptanceInfoReportAdapter extends BaseAdapter {

    private static List<NewRowInfo> list = new ArrayList<>();
    private static List<NewRowInfo> selectedItems = new ArrayList<>();
    private AcceptanceInfoReport infoReport;

    public AcceptanceInfoReportAdapter(AcceptanceInfoReport infoReport, List<NewRowInfo> list) {
        this.list = list;
        this.infoReport = infoReport;
    }

    public AcceptanceInfoReportAdapter() {
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(int position, View convertView, ViewGroup parent) {

        AcceptanceInfoViewHolder viewHolder = new AcceptanceInfoViewHolder();
        convertView = View.inflate(parent.getContext(), R.layout.acceptance_info_report_raw, null);

        viewHolder.bundleNo = convertView.findViewById(R.id.acceptanceInfo_raw_bundle);
        viewHolder.grade = convertView.findViewById(R.id.acceptanceInfo_raw_grade);
        viewHolder.length = convertView.findViewById(R.id.acceptanceInfo_raw_length);
//        viewHolder.location = convertView.findViewById(R.id.acceptanceInfo_raw_location);
        viewHolder.noOfPieces = convertView.findViewById(R.id.acceptanceInfo_raw_pcs);
        viewHolder.rejectedPieces = convertView.findViewById(R.id.acceptanceInfo_raw_rejected);
        viewHolder.supplier = convertView.findViewById(R.id.acceptanceInfo_raw_supplier);
        viewHolder.thickness = convertView.findViewById(R.id.acceptanceInfo_raw_thick);
        viewHolder.width = convertView.findViewById(R.id.acceptanceInfo_raw_width);
        viewHolder.pic = convertView.findViewById(R.id.acceptanceInfo_raw_pic);
        viewHolder.checkBox = convertView.findViewById(R.id.acceptanceInfo_raw_checkBox);
        viewHolder.edit = convertView.findViewById(R.id.acceptanceInfo_raw_edit);

        viewHolder.bundleNo.setText("" + (int) list.get(position).getNoOfBundles());
        viewHolder.grade.setText("" + list.get(position).getGrade());
        viewHolder.length.setText("" + (int) list.get(position).getLength());
//        viewHolder.location.setText("" + list.get(position).getLocationOfAcceptance());
        viewHolder.noOfPieces.setText("" + (int) list.get(position).getNoOfPieces());
        viewHolder.rejectedPieces.setText("" + (int) list.get(position).getNoOfRejected());
        viewHolder.supplier.setText("" + list.get(position).getSupplierName());
        viewHolder.thickness.setText("" + (int) list.get(position).getThickness());
        viewHolder.width.setText("" + (int) list.get(position).getWidth());

        if (list.get(position).getChecked())
            viewHolder.checkBox.setChecked(true);

        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked)
                    list.get(position).setChecked(true);
                else {
                    list.get(position).setChecked(false);
                    infoReport.changeCheckBoxState();
                }

            }
        });

        viewHolder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                truckNoBeforeUpdate = list.get(position).getTruckNo();
                infoReport.goToEditPage(list.get(position));
            }
        });

        return convertView;
    }

    public List<NewRowInfo> getSelectedItems() {

        selectedItems.clear();
        for (int i = 0; i < list.size(); i++)
            if (list.get(i).getChecked())
                selectedItems.add(list.get(i));
        Log.e("selected", "" + selectedItems.size());
        return selectedItems;
    }

    class AcceptanceInfoViewHolder {

        TextView supplier, bundleNo, thickness, width, length, noOfPieces, grade, location, rejectedPieces, pic, edit;
        CheckBox checkBox;

    }
}
