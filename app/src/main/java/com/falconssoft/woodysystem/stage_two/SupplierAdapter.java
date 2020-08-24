package com.falconssoft.woodysystem.stage_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.CustomerInfo;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.List;

public class SupplierAdapter extends RecyclerView.Adapter<SupplierAdapter.SuppliersViewHolder> {

    private PlannedPackingList plannedPL;
    private UnloadPackingList plannedPL2;
    private LoadPackingList loadPackingList;

    private List<SupplierInfo> customerInfoList;
    private int flag;

    public SupplierAdapter(int flag, Context plannedPL, List<SupplierInfo> customerInfoList) {
        if (flag == 1)
            this.plannedPL = (PlannedPackingList) plannedPL;
        else if (flag == 2)
            this.plannedPL2 = (UnloadPackingList) plannedPL;
        else
            this.loadPackingList = (LoadPackingList) plannedPL;

        this.flag = flag;
        this.customerInfoList = customerInfoList;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suppliers_row2, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {
        holder.supplierNo.setText("" + customerInfoList.get(i).getSupplierNo());
        holder.supplierName.setText("" + customerInfoList.get(i).getSupplierName());

        int index = i;
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1)
                    plannedPL.getSearchSupplierInfo(customerInfoList.get(index).getSupplierName()
                            , customerInfoList.get(index).getSupplierNo());
                else if (flag == 2)
                    plannedPL2.getSearchSupplierInfo(customerInfoList.get(index).getSupplierName()
                            , customerInfoList.get(index).getSupplierNo());
                else
                    loadPackingList.getSearchSupplierInfo(customerInfoList.get(index).getSupplierName()
                            , customerInfoList.get(index).getSupplierNo());
            }
        });

    }

    @Override
    public int getItemCount() {
        return customerInfoList.size();
    }

    class SuppliersViewHolder extends RecyclerView.ViewHolder {

        TextView supplierNo, supplierName;
        LinearLayout linearLayout;

        public SuppliersViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.supplier_row_linear);
            supplierNo = itemView.findViewById(R.id.supplier_row_supplier_no);
            supplierName = itemView.findViewById(R.id.supplier_row_supplier_name);

        }
    }
}