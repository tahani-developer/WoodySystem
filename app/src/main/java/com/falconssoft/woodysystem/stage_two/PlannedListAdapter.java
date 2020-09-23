package com.falconssoft.woodysystem.stage_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.CustomerInfo;
import com.falconssoft.woodysystem.models.PlannedPL;

import java.util.List;

public class PlannedListAdapter extends RecyclerView.Adapter<PlannedListAdapter.SuppliersViewHolder> {

    private PlannedPackingList context;
    private List<PlannedPL> PlannedPL;

    public PlannedListAdapter(Context context, List<PlannedPL> PlannedPL) {

        this.context = (PlannedPackingList) context;
        this.PlannedPL = PlannedPL;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
//        if (PlannedPL.get(i).getHide() == 1) {
//            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.planned_list_row2, viewGroup, false);
//        } else {
        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.planned_list_row, viewGroup, false);
//        }
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {
        Log.e("save", "adapter" + PlannedPL.get(i).getNoOfCopies());
        holder.serial.setText("" + (i + 1));
        holder.cust.setText(PlannedPL.get(i).getCustName());
        holder.pl.setText(PlannedPL.get(i).getPackingList());
        holder.dest.setText(PlannedPL.get(i).getDestination());
        holder.order.setText(PlannedPL.get(i).getOrderNo());
        holder.supplier.setText(PlannedPL.get(i).getSupplier());
        holder.grade.setText(PlannedPL.get(i).getGrade());
        holder.thick.setText("" + (int) PlannedPL.get(i).getThickness());
        holder.width.setText("" + (int) PlannedPL.get(i).getWidth());
        holder.length.setText("" + (int) PlannedPL.get(i).getLength());
        holder.pieces.setText("" + (int) PlannedPL.get(i).getNoOfPieces());
        holder.copies.setText("" + PlannedPL.get(i).getNoOfCopies());

        if (PlannedPL.get(i).getHide() == 1) {
            holder.serial.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.cust.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.pl.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.dest.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.order.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.supplier.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.grade.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.thick.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.width.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.length.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.pieces.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.copies.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.isExist.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.linEdit.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
            holder.linCopy.setBackgroundColor(context.getResources().getColor(R.color.gray_un_editable));
        } else {
//            holder.serial.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.cust.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.pl.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.dest.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.order.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.supplier.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.grade.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.thick.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.width.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.length.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.pieces.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.copies.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.isExist.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.linEdit.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
            holder.linCopy.setBackgroundColor(context.getResources().getColor(R.color.light_orange));
        }

        if (PlannedPL.get(i).getExist().equals("Exist")) {
            holder.isExist.setText("Exist(" + PlannedPL.get(i).getNoOfExixt() + ")");
            holder.isExist.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        } else {
            holder.isExist.setText("Not Exist");
            holder.isExist.setTextColor(ContextCompat.getColor(context, R.color.preview));
        }

        if (PlannedPL.get(i).getExist().equals("null"))
            holder.isExist.setText("");

        if (PlannedPL.get(i).getExist().equals("Planned")) {
            holder.isExist.setText("Planned Exist(" + PlannedPL.get(i).getNoOfExixt() + ")");
            holder.isExist.setTextColor(ContextCompat.getColor(context, R.color.orange));
        }

        if (PlannedPL.get(i).getHide() == 1) {
            holder.isExist.setText("Ordered");
        }

        int index = i;
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlannedPL.get(i).getHide() != 1)
                    context.editItemDialog(index);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.deleteItemDialog(index);
            }
        });

//        holder.copy.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (PlannedPL.get(i).getHide() != 1)
//                    context.editNoOfItemsDialog(index);
//            }
//        });

        holder.isExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // if (PlannedPL.get(i).getExist().equals("Not Exist"))
                if (PlannedPL.get(i).getHide() != 1)
                    context.closedResultsDialog(index);
            }
        });

    }

    @Override
    public int getItemCount() {
        return PlannedPL.size();
    }

    class SuppliersViewHolder extends RecyclerView.ViewHolder {

        TextView serial, cust, pl, dest, order, supplier, grade, thick, width, length, pieces, copies, isExist, edit, delete, copy;
        LinearLayout linearLayout, linEdit, linCopy;

        public SuppliersViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.linear);

            serial = itemView.findViewById(R.id.serial);
            cust = itemView.findViewById(R.id.cust);
            pl = itemView.findViewById(R.id.pl);
            dest = itemView.findViewById(R.id.dest);
            order = itemView.findViewById(R.id.order_no);
            supplier = itemView.findViewById(R.id.supplier);
            grade = itemView.findViewById(R.id.grade);
            thick = itemView.findViewById(R.id.thick);
            width = itemView.findViewById(R.id.width);
            length = itemView.findViewById(R.id.length);
            pieces = itemView.findViewById(R.id.pieces);
            copies = itemView.findViewById(R.id.copies);
            isExist = itemView.findViewById(R.id.is_exist);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
//            copy = itemView.findViewById(R.id.copy);
            linEdit = itemView.findViewById(R.id.lin_edit);
            linCopy = itemView.findViewById(R.id.lin_copy);
        }
    }
}