package com.falconssoft.woodysystem;

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

import com.falconssoft.woodysystem.models.BundleInfo;
import com.falconssoft.woodysystem.models.NewRowInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohd darras on 15/04/2018.
 */

public class ItemsListAdapter4 extends BaseAdapter {

    private Context context;
    private List<NewRowInfo> mOriginalValues;
    private static List<NewRowInfo> itemsList;
    private static List<NewRowInfo> selectedBundles;

    public ItemsListAdapter4(Context context, List<NewRowInfo> itemsList) {
        this.context = context;
        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        selectedBundles = new ArrayList<>();
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
        TextView th, w, l, grade, pcs, bundle, rejected, supplier;
        ImageView image;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.item_row4, null);

        holder.th = (TextView) view.findViewById(R.id.th);
        holder.w = (TextView) view.findViewById(R.id.w);
        holder.l = (TextView) view.findViewById(R.id.l);
        holder.grade = (TextView) view.findViewById(R.id.grade);
        holder.pcs = (TextView) view.findViewById(R.id.pcs);
        holder.bundle = (TextView) view.findViewById(R.id.bundles);
        holder.rejected = (TextView) view.findViewById(R.id.rej);
        holder.supplier = (TextView) view.findViewById(R.id.supplier);
        holder.image = (ImageView) view.findViewById(R.id.image);

        holder.th.setText("" + itemsList.get(i).getThickness());
        holder.w.setText("" + itemsList.get(i).getWidth());
        holder.l.setText("" + itemsList.get(i).getLength());
        holder.grade.setText("" + itemsList.get(i).getGrade());
        holder.pcs.setText("" + itemsList.get(i).getNoOfPieces());
        holder.bundle.setText("" + itemsList.get(i).getNoOfBundles());
        holder.rejected.setText("" + itemsList.get(i).getNoOfRejected());
        holder.supplier.setText("" + itemsList.get(i).getSupplierName());

//        if (itemsList.get(i).getPicture() == null) {
//            holder.image.setImageDrawable(context.getDrawable(R.drawable.pic));
//        }
//        else {
//            holder.image.setImageBitmap(StringToBitMap(itemsList.get(i).getPicture()));
//
//        }
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
