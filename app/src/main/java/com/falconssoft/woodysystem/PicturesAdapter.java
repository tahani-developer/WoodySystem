package com.falconssoft.woodysystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.falconssoft.woodysystem.reports.AcceptanceReport;
import com.falconssoft.woodysystem.reports.LoadingOrderReport;
import com.github.chrisbanes.photoview.PhotoView;

import java.util.List;

public class PicturesAdapter extends BaseAdapter {

    //    private Context context;
    private static List<Bitmap> itemsList;
    private LoadingOrderReport loadingOrderReport;
    private AcceptanceReport acceptanceReport;

    public PicturesAdapter(List<Bitmap> itemsList, LoadingOrderReport loadingOrderReport, AcceptanceReport acceptanceReport) {
        this.loadingOrderReport = loadingOrderReport;
        this.acceptanceReport = acceptanceReport;
        this.itemsList = itemsList;
    }

    public void setItemsList(List<Bitmap> itemsList) {
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
        PhotoView pic;
        LinearLayout linearLayout;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(viewGroup.getContext(), R.layout.pic_row, null);

        holder.pic = view.findViewById(R.id.pic);
        if (itemsList.get(i) != null)
            holder.pic.setImageBitmap(itemsList.get(i));

        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("bitmap", "3");

//                if (acceptanceReport == null)
//                    loadingOrderReport.zoomImage(itemsList.get(i));
//                else
                loadingOrderReport.zoomImage(itemsList.get(i));
            }
        });


        return view;
    }

}
