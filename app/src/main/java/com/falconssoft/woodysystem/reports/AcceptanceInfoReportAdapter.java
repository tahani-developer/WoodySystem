package com.falconssoft.woodysystem.reports;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;

import java.util.ArrayList;
import java.util.List;

public class AcceptanceInfoReportAdapter extends BaseAdapter {

    private List<NewRowInfo> list =  new ArrayList<>();

    public AcceptanceInfoReportAdapter(List<NewRowInfo> list) {
        this.list = list;
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

        viewHolder.bundleNo.setText("" + list.get(position).getNoOfBundles());
        viewHolder.grade.setText("" + list.get(position).getGrade());
        viewHolder.length.setText("" + list.get(position).getLength());
//        viewHolder.location.setText("" + list.get(position).getLocationOfAcceptance());
        viewHolder.noOfPieces.setText("" + list.get(position).getNoOfPieces());
        viewHolder.rejectedPieces.setText("" + list.get(position).getNoOfRejected());
        viewHolder.supplier.setText("" + list.get(position).getSupplierName());
        viewHolder.thickness.setText("" + list.get(position).getThickness());
        viewHolder.width.setText("" + list.get(position).getWidth());

        return convertView;
    }

    class AcceptanceInfoViewHolder{

        TextView supplier, bundleNo, thickness, width, length, noOfPieces, grade, location, rejectedPieces, pic;

    }
}
