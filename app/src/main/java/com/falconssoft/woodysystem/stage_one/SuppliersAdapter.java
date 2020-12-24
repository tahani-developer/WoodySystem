package com.falconssoft.woodysystem.stage_one;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.SupplierInfo;
import com.falconssoft.woodysystem.reports.AcceptanceReport;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SuppliersAdapter extends RecyclerView.Adapter<SuppliersAdapter.SuppliersViewHolder> {

    private AddNewRaw addNewRaw;
    private List<SupplierInfo> supplierInfoList;
    private EditPage editPage;
    private AcceptanceReport truckReport;

    public SuppliersAdapter(AddNewRaw addNewRaw, List<SupplierInfo> supplierInfoList, EditPage editPage, AcceptanceReport truckReport) {
        this.addNewRaw = addNewRaw;
        this.supplierInfoList = supplierInfoList;
        this.editPage = editPage;
        this.truckReport = truckReport;

    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.suppliers_row2, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {
        Log.e("size2", "" + supplierInfoList.size());
        holder.supplierNo.setText("" + supplierInfoList.get(i).getSupplierNo());
        holder.supplierName.setText("" + supplierInfoList.get(i).getSupplierName());
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addNewRaw != null)
                    addNewRaw.getSearchSupplierInfo(supplierInfoList.get(i).getSupplierName()
                            , supplierInfoList.get(i).getSupplierNo());
                else if (editPage != null)
                    editPage.getSearchSupplierInfo(supplierInfoList.get(i).getSupplierName()
                            , supplierInfoList.get(i).getSupplierNo());
                else
                    truckReport.getSearchSupplierInfo(supplierInfoList.get(i).getSupplierName()
                            , supplierInfoList.get(i).getSupplierNo());
            }
        });

    }

    @Override
    public int getItemCount() {
        return supplierInfoList.size();
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