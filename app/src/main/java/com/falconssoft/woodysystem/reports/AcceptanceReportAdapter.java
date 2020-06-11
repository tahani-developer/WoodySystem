package com.falconssoft.woodysystem.reports;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;

import java.util.List;

import static com.falconssoft.woodysystem.reports.AcceptanceReport.truckNoBeforeUpdate2;

public class AcceptanceReportAdapter extends BaseAdapter {

    private AcceptanceReport context;
    //    private List<BundleInfo> mOriginalValues;
    private static List<NewRowInfo> itemsList;
    private static List<NewRowInfo> bundles;
//    private static List<BundleInfo> selectedBundles ;

    public AcceptanceReportAdapter(AcceptanceReport context, List<NewRowInfo> itemsList, List<NewRowInfo> bundles) {
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
        Button preview;
        TextView truckNo, acceptor, ttn, netBundle, date, location, rejected;
        ImageView edit;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.acceptance_report_row, null);

//        holder.pic = (ImageView) view.findViewById(R.id.pic);
//        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.preview = (Button) view.findViewById(R.id.preview);
        holder.truckNo = (TextView) view.findViewById(R.id.truck_no);
        holder.acceptor = (TextView) view.findViewById(R.id.name_of_person_to_accept);
        holder.date = (TextView) view.findViewById(R.id.date_of_acceptance);
        holder.ttn = (TextView) view.findViewById(R.id.ttn_no);
        holder.location = (TextView) view.findViewById(R.id.location_of_acceptance);
        holder.rejected = (TextView) view.findViewById(R.id.rejected_pieces);
        holder.edit =  view.findViewById(R.id.acceptanceReport_edit);

        holder.truckNo.setText(itemsList.get(i).getTruckNo());
        holder.acceptor.setText(itemsList.get(i).getAcceptedPersonName());
        holder.date.setText(itemsList.get(i).getDate());
        holder.ttn.setText(itemsList.get(i).getTtnNo());
//        holder.netBundle.setText( itemsList.get(i).getNetBundles());
        holder.location.setText(itemsList.get(i).getLocationOfAcceptance());
        holder.rejected.setText(itemsList.get(i).getTotalRejectedNo());

        AcceptanceReport obj = new AcceptanceReport();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                truckNoBeforeUpdate2 = itemsList.get(i).getTruckNo();
                context.goToEditPage( itemsList.get(i));
            }
        });
        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewLinear(itemsList.get(i), context);

            }
        });

        return view;
    }
}
