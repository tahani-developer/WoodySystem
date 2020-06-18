package com.falconssoft.woodysystem.reports;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.ItemsListAdapter2;
import com.falconssoft.woodysystem.LoadingOrder2;
import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class LoadingOrderReportAdapter2 extends BaseAdapter {

    private Context context;
    private LoadingOrder2 loadingOrder2;
    private List<BundleInfo> mOriginalValues;
    private static List<BundleInfo> itemsList;
    private static List<BundleInfo> selectedBundles;

    public LoadingOrderReportAdapter2(Context context, List<BundleInfo> itemsList) {
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
        TextView th, w, l, grade, pcs, bundle, location, area;
        ImageView image;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final LoadingOrderReportAdapter2.ViewHolder holder = new LoadingOrderReportAdapter2.ViewHolder();
        view = View.inflate(context, R.layout.loading_order_report_slot, null);//item_row2

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

//        if (itemsList.get(i).getPicBitmap() == null) {
//            holder.image.setImageDrawable(context.getDrawable(R.drawable.pic));
//        }
//        else {
//            holder.image.setImageBitmap(itemsList.get(i).getPicBitmap());
//
//        }
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


    public Bitmap StringToBitMap(String image) {
        try {
            byte[] encodeByte = Base64.decode(image, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

}
