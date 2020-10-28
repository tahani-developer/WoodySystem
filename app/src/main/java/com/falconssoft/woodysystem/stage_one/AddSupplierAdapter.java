package com.falconssoft.woodysystem.stage_one;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.ArrayList;
import java.util.List;

public class AddSupplierAdapter extends RecyclerView.Adapter<AddSupplierAdapter.AddSupplierViewHolder> {

    private List<SupplierInfo> list = new ArrayList<>();
    private AddNewSupplier context;

    public AddSupplierAdapter(AddNewSupplier context, List<SupplierInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public AddSupplierViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suppliers_row, viewGroup, false);
        return new AddSupplierViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddSupplierViewHolder viewHolder, int i) {
        viewHolder.supplierName.setText(list.get(i).getSupplierName());
        viewHolder.supplierNo.setText(list.get(i).getSupplierNo());
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteSupplier(i);
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
