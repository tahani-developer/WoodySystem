package com.falconssoft.woodysystem.stage_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.PlannedPL;

import java.util.List;

public class PlannedUnPlannedAdapter extends RecyclerView.Adapter<PlannedUnPlannedAdapter.SuppliersViewHolder> {

    private PlannedUnplanned plannedPL;
    private List<PlannedPL> PlannedPL;

    public PlannedUnPlannedAdapter(Context plannedPL, List<PlannedPL> PlannedPL) {

        this.plannedPL = (PlannedUnplanned) plannedPL;
        this.PlannedPL = PlannedPL;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.planned_unplanned_row, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {

        Double CBM =  (PlannedPL.get(i).getThickness() * PlannedPL.get(i).getWidth() * PlannedPL.get(i).getLength() * PlannedPL.get(i).getNoOfPieces() * PlannedPL.get(i).getNoOfCopies());

        holder.grade.setText(PlannedPL.get(i).getGrade());
        holder.thick.setText("" + (int) PlannedPL.get(i).getThickness());
        holder.width.setText("" + (int) PlannedPL.get(i).getWidth());
        holder.length.setText("" + (int) PlannedPL.get(i).getLength());
        holder.pieces.setText("" + (int) PlannedPL.get(i).getNoOfPieces());
        holder.copies.setText("" + PlannedPL.get(i).getNoOfCopies());
        holder.cubic.setText("" + String.format("%.3f", (PlannedPL.get(i).getCubic() )));

    }

    @Override
    public int getItemCount() {
        return PlannedPL.size();
    }

    class SuppliersViewHolder extends RecyclerView.ViewHolder {

        TextView grade ,thick , width, length, pieces, copies, cubic;
        LinearLayout linearLayout;

        public SuppliersViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear);

            grade = itemView.findViewById(R.id.grade);
            thick = itemView.findViewById(R.id.thick);
            width = itemView.findViewById(R.id.width);
            length = itemView.findViewById(R.id.length);
            pieces = itemView.findViewById(R.id.pieces);
            copies = itemView.findViewById(R.id.copies);
            cubic = itemView.findViewById(R.id.cubic);
        }
    }
}