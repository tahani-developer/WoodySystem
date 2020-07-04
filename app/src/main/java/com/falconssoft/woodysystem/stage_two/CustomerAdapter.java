package com.falconssoft.woodysystem.stage_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.CustomerInfo;

import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.SuppliersViewHolder> {

    private PlannedPackingList plannedPL;
    private UnloadPackingList plannedPL2;
    private List<CustomerInfo> customerInfoList;
    private int flag;

    public CustomerAdapter(int flag, Context plannedPL, List<CustomerInfo> customerInfoList) {
        if (flag == 1)
            this.plannedPL = (PlannedPackingList) plannedPL;
        else
            this.plannedPL2 = (UnloadPackingList) plannedPL;

        this.flag = flag;
        this.customerInfoList = customerInfoList;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suppliers_row, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {
        holder.supplierNo.setText("" + customerInfoList.get(i).getCustNo());
        holder.supplierName.setText("" + customerInfoList.get(i).getCustName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 1)
                    plannedPL.getSearchCustomerInfo(customerInfoList.get(i).getCustName()
                            , customerInfoList.get(i).getCustNo());
                else
                    plannedPL2.getSearchCustomerInfo(customerInfoList.get(i).getCustName()
                            , customerInfoList.get(i).getCustNo());
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