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
        TextView th, w, l, grade, pcs, bundle, rejected, supplier, cubic;
        ImageView image;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final ViewHolder holder = new ViewHolder();
        view = View.inflate(context, R.layout.item_row4, null);

        holder.th = view.findViewById(R.id.th);
        holder.w = view.findViewById(R.id.w);
        holder.l = view.findViewById(R.id.l);
        holder.grade = view.findViewById(R.id.grade);
        holder.pcs = view.findViewById(R.id.pcs);
        holder.bundle = view.findViewById(R.id.bundles);
        holder.rejected = view.findViewById(R.id.rej);
        holder.supplier = view.findViewById(R.id.supplier);
        holder.image = view.findViewById(R.id.image);
        holder.cubic = view.findViewById(R.id.item_row4_cubic);

        holder.th.setText("" + (int) itemsList.get(i).getThickness());
        holder.w.setText("" + (int) itemsList.get(i).getWidth());
        holder.l.setText("" + (int) itemsList.get(i).getLength());
        holder.grade.setText("" + itemsList.get(i).getGrade());
        holder.pcs.setText("" + (int) itemsList.get(i).getNoOfPieces());
        holder.bundle.setText("" + (int) itemsList.get(i).getNoOfBundles());
        holder.rejected.setText("" + (int) itemsList.get(i).getNoOfRejected());
        holder.supplier.setText("" + itemsList.get(i).getSupplierName());
        holder.cubic.setText("" + itemsList.get(i).getCubic());

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
