package com.falconssoft.woodysystem.stage_one;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuppliersAdapter extends RecyclerView.Adapter<SuppliersViewHolder> {

    private Context context;
    private List<SupplierInfo> list;
    private ArrayList<SupplierInfo> arraylist;

    public SuppliersAdapter(Context context, List<SupplierInfo> supplierInfoList) {
        this.context = context;
        this.list = supplierInfoList;
        this.arraylist = new ArrayList<SupplierInfo>();
        this.arraylist.addAll(list);
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.suppliers_row, viewGroup);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder suppliersViewHolder, int i) {
        SuppliersViewHolder viewHolder = new SuppliersViewHolder(null);
        viewHolder.supplierNo.setText("" + list.get(i).getSupplierNo());
        viewHolder.supplierName.setText("" + list.get(i).getSupplierName());

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        list.clear();
        if (charText.length() == 0) {
            list.addAll(arraylist);
        } else {
            for (SupplierInfo wp : arraylist) {
                if (wp.getSupplierNo().toLowerCase(Locale.getDefault()).contains(charText)) {
                    list.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }
}

class SuppliersViewHolder extends RecyclerView.ViewHolder {

    TextView supplierNo, supplierName;

    public SuppliersViewHolder(@NonNull View itemView) {
        super(itemView);

        supplierNo = itemView.findViewById(R.id.supplier_row_supplier_no);
        supplierName = itemView.findViewById(R.id.supplier_row_supplier_name);

    }
}