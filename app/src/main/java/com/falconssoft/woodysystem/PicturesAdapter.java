package com.falconssoft.woodysystem;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.models.BundleInfo;

import java.util.ArrayList;
import java.util.List;

public class PicturesAdapter extends BaseAdapter {

    private Context context;
    private static List<Bitmap> itemsList;

    public PicturesAdapter(Context context, List<Bitmap> itemsList) {
        this.context = context;
        this.itemsList = itemsList;
    }

    public PicturesAdapter() {

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
        ImageView pic;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.pic_row, null);

        holder.pic = view.findViewById(R.id.pic);
        if (itemsList.get(i) != null)
            holder.pic.setImageBitmap(itemsList.get(i));

        return view;
    }

}
