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
        this.itemsList = itemsList;
        this.bundles = bundles;
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
        ImageView pic, image;
        Button preview;
        TextView truckNo, acceptor, ttn, netBundle, date, noOfBundles, rejected, cubic, cubicRej, serial,acceptCubic;
        ImageView edit;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.acceptance_report_row, null);

//        holder.pic = (ImageView) view.findViewById(R.id.pic);
//        holder.netBundle = (TextView) view.findViewById(R.id.net);
        holder.preview = view.findViewById(R.id.preview);
        holder.truckNo = view.findViewById(R.id.truck_no);
        holder.acceptor = view.findViewById(R.id.name_of_person_to_accept); //supplier after edit not acceptor
        holder.date = view.findViewById(R.id.date_of_acceptance);
        holder.ttn = view.findViewById(R.id.ttn_no);
        holder.noOfBundles = view.findViewById(R.id.noOfBundles_acceptance);
        holder.rejected = view.findViewById(R.id.rejected_pieces);
        holder.edit = view.findViewById(R.id.acceptanceReport_edit);
        holder.cubic = view.findViewById(R.id.truck_report_cubic);
        holder.cubicRej = view.findViewById(R.id.truck_report_cubic_rej);
        holder.serial = view.findViewById(R.id.truck_report_serial);
        holder.image = view.findViewById(R.id.truckReport_image);
        holder.acceptCubic=view.findViewById(R.id.truck_report_cubic_accept);

        holder.serial.setText(itemsList.get(i).getSerial());
        holder.truckNo.setText(itemsList.get(i).getTruckNo());
        holder.acceptor.setText(findSupplier(itemsList.get(i)));//itemsList.get(i).getAcceptedPersonName());
        holder.date.setText(itemsList.get(i).getDate());
        holder.ttn.setText(itemsList.get(i).getTtnNo());
//        holder.netBundle.setText( itemsList.get(i).getNetBundles());
        holder.noOfBundles.setText("" + itemsList.get(i).getNetBundles());
        holder.rejected.setText(itemsList.get(i).getTotalRejectedNo());
        holder.cubic.setText("" + itemsList.get(i).getCubic());
        holder.cubicRej.setText("" + itemsList.get(i).getCubicRej());
        double accCubic= 0;
        accCubic= itemsList.get(i).getCubic()- itemsList.get(i).getCubicRej();
        accCubic=Double.parseDouble(String.format("%.3f", accCubic));
        holder.acceptCubic.setText(""+ accCubic);

        AcceptanceReport obj = new AcceptanceReport();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                truckNoBeforeUpdate2 = itemsList.get(i).getTruckNo();
                context.goToEditPage(itemsList.get(i));
            }
        });

        holder.preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewLinear(itemsList.get(i).getSerial(), context);

            }
        });

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.previewPics2(itemsList.get(i), context);
            }
        });

        return view;
    }

    String findSupplier(NewRowInfo newRowInfo) {
        for (int i = 0; i < bundles.size(); i++) {
            if (newRowInfo.getSerial().equals(bundles.get(i).getSerial()))
                return bundles.get(i).getSupplierName();
        }

        return "----";
    }

}
