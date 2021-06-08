package com.falconssoft.woodysystem.reports;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.TransferBundle;
import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class TransferAdapter extends BaseAdapter {

    //    private Context context;
//    private List<BundleInfo> mOriginalValues;
    private static List<BundleInfo> itemsList;
    private static List<BundleInfo> selectedBundles;
    private TransferBundle transferBundle;

    public TransferAdapter(TransferBundle transferBundle, List<BundleInfo> itemsList) {
//        this.context = context;
        this.transferBundle = transferBundle;
//        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        selectedBundles = new ArrayList<>();
    }

    public TransferAdapter() {

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
        CheckBox checkBox;
        TextView /*serial,*/ oldBundle,newBundle, th, w, l, grade, pcs, location/*, area*/, pL;
        ImageView print;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(transferBundle, R.layout.transfer_row, null);

        holder.checkBox = (CheckBox) view.findViewById(R.id.checkBoxPrint);
        //holder.serial = (TextView) view.findViewById(R.id.serial);
        holder.th = (TextView) view.findViewById(R.id.th);
        holder.w = (TextView) view.findViewById(R.id.w);
        holder.l = (TextView) view.findViewById(R.id.l);
        holder.grade = (TextView) view.findViewById(R.id.grade);
        holder.pcs = (TextView) view.findViewById(R.id.pcs);
        holder.oldBundle = (TextView) view.findViewById(R.id.oldBundle);
        holder.newBundle = (TextView) view.findViewById(R.id.newBundle);

        holder.location = (TextView) view.findViewById(R.id.location);
       // holder.area = (TextView) view.findViewById(R.id.area);
        holder.pL = (TextView) view.findViewById(R.id.pl);
        holder.print = view.findViewById(R.id.transferRaw_print);

        //holder.serial.setText("" + itemsList.get(i).getSerialNo());
        holder.th.setText("" +(int) itemsList.get(i).getThickness());
        holder.w.setText("" + (int) itemsList.get(i).getWidth());
        holder.l.setText("" +  (int)itemsList.get(i).getLength());
        holder.grade.setText("" + itemsList.get(i).getGrade());
        holder.pcs.setText("" + (int) itemsList.get(i).getNoOfPieces());
        holder.newBundle.setText("" + itemsList.get(i).getNewBundleNo());
        holder.oldBundle.setText("" + itemsList.get(i).getBundleNo());
        holder.location.setText("" + itemsList.get(i).getNewLocation());
//        holder.area.setText("" + itemsList.get(i).getArea());
        holder.pL.setText("" + itemsList.get(i).getBackingList());
//        Log.e("adapter", "" + itemsList.get(i).getBackingList());

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

        holder.print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // transferBundle.editBundle(itemsList.get(i));

            }
        });

//        view.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View view) {
//                Log.e("show plist", ""  +itemsList.get(i).getBackingList().equals(null) + itemsList.get(i).getBackingList().equals("null"));
//                if (itemsList.get(i).getBackingList().equals("null"))
//                    inventoryReport.addBackingList(itemsList, i);
//                else
//                    inventoryReport.showPasswordDialog(itemsList, i);
//                    return false;
//                }
//            });

        return view;
        }

        public List<BundleInfo> getSelectedItems () {

            selectedBundles.clear();
            for (int i = 0; i < itemsList.size(); i++)
                if (itemsList.get(i).getChecked())
                    selectedBundles.add(itemsList.get(i));

            return selectedBundles;
        }
    }
