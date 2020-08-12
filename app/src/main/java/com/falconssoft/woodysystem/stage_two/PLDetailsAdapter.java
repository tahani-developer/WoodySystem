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
import com.falconssoft.woodysystem.models.PlannedPL;
import com.falconssoft.woodysystem.models.SupplierInfo;

import java.util.List;

public class PLDetailsAdapter extends RecyclerView.Adapter<PLDetailsAdapter.SuppliersViewHolder> {

    private LoadPackingList plannedPL;

    private List<PlannedPL> PList;
    private int flag;

    public PLDetailsAdapter(int flag, Context plannedPL, List<PlannedPL> PList) {
        this.plannedPL = (LoadPackingList) plannedPL;
        this.flag = flag;
        this.PList = PList;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.pl_details_row, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {
        holder.thick.setText("" + PList.get(i).getThickness());
        holder.width.setText("" + PList.get(i).getWidth());
        holder.length.setText("" + PList.get(i).getLength());
        holder.pieces.setText("" + PList.get(i).getNoOfPieces());
        holder.bundles.setText("" + PList.get(i).getNoOfCopies());
    }

    @Override
    public int getItemCount() {
        return PList.size();
    }

    class SuppliersViewHolder extends RecyclerView.ViewHolder {

        TextView thick, width , length , pieces , bundles;
        LinearLayout linearLayout;

        public SuppliersViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.supplier_row_linear);
            thick = itemView.findViewById(R.id.thick);
            width = itemView.findViewById(R.id.width);
            length = itemView.findViewById(R.id.length);
            pieces = itemView.findViewById(R.id.pieces);
            bundles = itemView.findViewById(R.id.no_bundles);
        }
    }
}