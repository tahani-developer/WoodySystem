package com.falconssoft.woodysystem;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohd darras on 15/04/2018.
 */

public class ItemsListAdapter2 extends BaseAdapter {

//    private Context context;
    private LoadingOrder2 loadingOrder2;
    private List<BundleInfo> mOriginalValues;
    private static List<BundleInfo> itemsList;
    private static List<BundleInfo> selectedBundles;

    public ItemsListAdapter2(LoadingOrder2 loadingOrder2, List<BundleInfo> itemsList) {
        this.loadingOrder2 = loadingOrder2;
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
        TextView th, w, l, grade, pcs, bundle, location, area;
        ImageView image;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(loadingOrder2, R.layout.item_row2, null);

        holder.th = (TextView) view.findViewById(R.id.th);
        holder.w = (TextView) view.findViewById(R.id.w);
        holder.l = (TextView) view.findViewById(R.id.l);
        holder.grade = (TextView) view.findViewById(R.id.grade);
        holder.pcs = (TextView) view.findViewById(R.id.pcs);
        holder.bundle = (TextView) view.findViewById(R.id.bundle);
        holder.location = (TextView) view.findViewById(R.id.location);
        holder.area = (TextView) view.findViewById(R.id.area);
        holder.image = (ImageView) view.findViewById(R.id.image);

        holder.th.setText("" + itemsList.get(i).getThickness());
        holder.w.setText("" + itemsList.get(i).getWidth());
        holder.l.setText("" + itemsList.get(i).getLength());
        holder.grade.setText("" + itemsList.get(i).getGrade());
        holder.pcs.setText("" + itemsList.get(i).getNoOfPieces());
        holder.bundle.setText("" + itemsList.get(i).getBundleNo());
        holder.location.setText("" + itemsList.get(i).getLocation());
        holder.area.setText("" + itemsList.get(i).getArea());

        if (itemsList.get(i).getPicture() == null)
            holder.image.setImageDrawable(loadingOrder2.getResources().getDrawable(R.drawable.pic));
        else
            holder.image.setImageBitmap(itemsList.get(i).getPicture());
//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View view) {
//
//                LoadingOrder2 obj = new LoadingOrder2();
//                obj.openCamera(i);
//
//            }
//        });

//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.M)
//            @Override
//            public void onClick(View v) {
//                loadingOrder2.imageClickListener(i);
//            }
//        });
        return view;
    }


    public Filter getFilter() {
        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                itemsList = (ArrayList<BundleInfo>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
//                Log.e("here" , "*********1" + itemsList.get(0).getCustName());
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<BundleInfo> FilteredArrList = new ArrayList<BundleInfo>();

                if (mOriginalValues == null) {
                    mOriginalValues = new ArrayList<BundleInfo>(itemsList); // saves the original data in mOriginalValues
                }

                /********
                 *
                 *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                 *  else does the Filtering and returns FilteredArrList(Filtered)
                 *
                 ********/
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    results.count = mOriginalValues.size();
                    results.values = mOriginalValues;
                } else {
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mOriginalValues.size(); i++) {
                        String data = "" + mOriginalValues.get(i).getThickness();
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilteredArrList.add(new BundleInfo(mOriginalValues.get(i).getThickness()
                                    , mOriginalValues.get(i).getWidth()
                                    , mOriginalValues.get(i).getLength()
                                    , mOriginalValues.get(i).getGrade()
                                    , mOriginalValues.get(i).getNoOfPieces()
                                    , mOriginalValues.get(i).getBundleNo()
                                    , mOriginalValues.get(i).getLocation()
                                    , mOriginalValues.get(i).getArea()
                                    , ""
                                    , mOriginalValues.get(i).getPicture()));
                            Log.e("here", "*********2" + constraint + "*" + data);
                        }
                    }
                    // set the Filtered result to return
                    results.count = FilteredArrList.size();
                    results.values = FilteredArrList;

//                    Log.e("here" , "*********3" + FilteredArrList.get(0).getCustName());
                }
                return results;
            }
        };
        return filter;
    }
}
