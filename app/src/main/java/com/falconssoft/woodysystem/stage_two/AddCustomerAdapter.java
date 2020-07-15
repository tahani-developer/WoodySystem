package com.falconssoft.woodysystem.stage_two;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.CustomerInfo;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.ArrayList;
import java.util.List;

public class AddCustomerAdapter extends RecyclerView.Adapter<AddCustomerAdapter.AddSupplierViewHolder> {

    private List<CustomerInfo> list = new ArrayList<>();
    private AddNewCustomer context;

    public AddCustomerAdapter(Context context, List<CustomerInfo> list) {
        this.list = list;
        this.context = (AddNewCustomer) context;
    }

    @NonNull
    @Override
    public AddSupplierViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suppliers_row, viewGroup, false);
        return new AddSupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddSupplierViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        viewHolder.supplierName.setText(list.get(i).getCustName());
        viewHolder.supplierNo.setText(list.get(i).getCustNo());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteCustomer(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class AddSupplierViewHolder extends RecyclerView.ViewHolder {
        TextView supplierNo, supplierName, delete;

        public AddSupplierViewHolder(@NonNull View itemView) {
            super(itemView);

            supplierNo = itemView.findViewById(R.id.supplier_row_supplier_no);
            supplierName = itemView.findViewById(R.id.supplier_row_supplier_name);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
