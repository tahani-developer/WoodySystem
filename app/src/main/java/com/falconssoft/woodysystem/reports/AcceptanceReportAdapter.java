package com.falconssoft.woodysystem.reports;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;
import com.falconssoft.woodysystem.models.Orders;

import java.util.List;

public class AcceptanceReportAdapter extends BaseAdapter {

    private Context context;
//    private List<BundleInfo> mOriginalValues;
    private static List<NewRowInfo> itemsList;
    private static List<NewRowInfo> bundles;
//    private static List<BundleInfo> selectedBundles ;

    public AcceptanceReportAdapter(Context context, List<NewRowInfo> itemsList, List<NewRowInfo> bundles) {
        this.context = context;
//        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        this.bundles = bundles;
//        selectedBundles = new ArrayList<>();
    }

    public AcceptanceReportAdapter() {

    }

    public void setItemsList(List<NewRowInfo> itemsList) {
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
        Button preview ;
        TextView  truckNo, acceptor, ttn, netBundle, date,location, rejected;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.acceptance_report_row, null);

        holder.preview = (Button) view.findViewById(R.id.preview);
        holder.truckNo = (TextView) view.findViewById(R.id.truck_no);
        holder.acceptor = (TextView) view.findViewById(R.id.name_of_person_to_accept);
        holder.date = (TextView) view.findViewById(R.id.date_of_acceptance);
        holder.ttn = (TextView) view.findViewById(R.id.ttn_no);
        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.location = (TextView) view.findViewById(R.id.location_of_acceptance);
        holder.rejected = (TextView) view.findViewById(R.id.rejected_pieces);
//        holder.pic = (ImageView) view.findViewById(R.id.pic);

        holder.truckNo.setText(itemsList.get(i).getTruckNo());
        holder.acceptor.setText(itemsList.get(i).getAcceptedPersonName());
        holder.date.setText(itemsList.get(i).getDate());
        holder.ttn.setText( itemsList.get(i).getTtnNo());
        holder.netBundle.setText( itemsList.get(i).getNetBundles());
        holder.location.setText( itemsList.get(i).getLocationOfAcceptance());
        holder.rejected.setText( itemsList.get(i).getTotalRejectedNo());

        AcceptanceReport obj = new AcceptanceReport();


        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewLinear(i , context);

            }
        });

        return view;
    }
}
