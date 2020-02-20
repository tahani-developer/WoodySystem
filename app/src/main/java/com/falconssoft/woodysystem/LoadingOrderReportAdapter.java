package com.falconssoft.woodysystem;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.Orders;
import com.falconssoft.woodysystem.models.Pictures;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;

import java.util.ArrayList;
import java.util.List;

public class LoadingOrderReportAdapter extends BaseAdapter {

    private Context context;
//    private List<BundleInfo> mOriginalValues;
    private static List<Orders> itemsList;
    private static List<Orders> bundles;
//    private static List<BundleInfo> selectedBundles ;

    public LoadingOrderReportAdapter(Context context, List<Orders> itemsList, List<Orders> bundles) {
        this.context = context;
//        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        this.bundles = bundles;
//        selectedBundles = new ArrayList<>();
    }

    public LoadingOrderReportAdapter() {

    }

    public void setItemsList(List<Orders> itemsList) {
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
        ImageView pic;
        Button bundleNo ;
        TextView orderNo, truckNo, containerNo, date, destination;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.loading_order_report_row, null);

        holder.orderNo = (TextView) view.findViewById(R.id.order);
        holder.truckNo = (TextView) view.findViewById(R.id.truck);
        holder.containerNo = (TextView) view.findViewById(R.id.container);
        holder.date = (TextView) view.findViewById(R.id.date);
        holder.destination = (TextView) view.findViewById(R.id.destination);
        holder.bundleNo = (Button) view.findViewById(R.id.bundleNo);
        holder.pic = (ImageView) view.findViewById(R.id.pic);

        holder.orderNo.setText(itemsList.get(i).getOrderNo());
        holder.truckNo.setText(itemsList.get(i).getPlacingNo());
        holder.containerNo.setText(itemsList.get(i).getContainerNo());
        holder.date.setText(itemsList.get(i).getDateOfLoad());
        holder.destination.setText( itemsList.get(i).getDestination());

        LoadingOrderReport obj = new LoadingOrderReport();


        holder.bundleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ArrayList<BundleInfo> bundleInfos = new ArrayList<>();
//
//                for (int k = 0; k < bundles.size(); k++) {
////                                    Log.e("ooo  " , ""+ orders.get(index).getOrderNo() + "  " + bundles.get(i).getBundleNo());
//                    if (itemsList.get(i).getOrderNo().equals(bundles.get(k).getOrderNo()) &&
//                            itemsList.get(i).getPlacingNo().equals(bundles.get(k).getPlacingNo()) &&
//                            itemsList.get(i).getContainerNo().equals(bundles.get(k).getContainerNo()) &&
//                            itemsList.get(i).getDateOfLoad().equals(bundles.get(k).getDateOfLoad())) {
//
//                        bundleInfos.add(new BundleInfo(
//                                bundles.get(k).getThickness(),
//                                bundles.get(k).getWidth(),
//                                bundles.get(k).getLength(),
//                                bundles.get(k).getGrade(),
//                                bundles.get(k).getNoOfPieces(),
//                                bundles.get(k).getBundleNo(),
//                                bundles.get(k).getLocation(),
//                                bundles.get(k).getArea(),
//                                "",
//                                bundles.get(k).getPicture()));
//                    }
//                }

                obj.previewLinear(i , context);

            }
        });

        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewPics(i , context);
            }
        });


        return view;
    }
}
