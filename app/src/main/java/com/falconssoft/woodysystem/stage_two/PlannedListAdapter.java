package com.falconssoft.woodysystem.stage_two;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
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

    private PlannedPackingList plannedPL;
    private List<PlannedPL> PlannedPL;

    public PlannedListAdapter(Context plannedPL, List<PlannedPL> PlannedPL) {

        this.plannedPL = (PlannedPackingList) plannedPL;
        this.PlannedPL = PlannedPL;
    }

    @NonNull
    @Override

    public SuppliersViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.planned_list_row, viewGroup, false);
        return new SuppliersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuppliersViewHolder holder, int i) {

        holder.serial.setText("" + (i + 1));
        holder.cust.setText(PlannedPL.get(i).getCustName());
        holder.pl.setText(PlannedPL.get(i).getPackingList());
        holder.dest.setText(PlannedPL.get(i).getDestination());
        holder.order.setText(PlannedPL.get(i).getOrderNo());
        holder.supplier.setText(PlannedPL.get(i).getSupplier());
        holder.thick.setText("" + (int) PlannedPL.get(i).getThickness());
        holder.width.setText("" + (int) PlannedPL.get(i).getWidth());
        holder.length.setText("" + (int) PlannedPL.get(i).getLength());
        holder.pieces.setText("" + (int) PlannedPL.get(i).getNoOfPieces());
        holder.copies.setText("" + PlannedPL.get(i).getNoOfCopies());

        if (PlannedPL.get(i).getExist().equals("Exist")) {
            holder.isExist.setText("Exist(" + PlannedPL.get(i).getNoOfExixt() + ")");
            holder.isExist.setTextColor(ContextCompat.getColor(plannedPL, R.color.colorPrimary));
        } else {
            holder.isExist.setText("Not Exist");
            holder.isExist.setTextColor(ContextCompat.getColor(plannedPL, R.color.preview));
        }

        if (PlannedPL.get(i).getExist().equals("null"))
            holder.isExist.setText("");

        int index = i;
        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plannedPL.editItemDialog(index);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plannedPL.deleteItemDialog(index);
            }
        });
        holder.copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                plannedPL.editNoOfItemsDialog(index);
            }
        });


        holder.isExist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (PlannedPL.get(i).getExist().equals("Not Exist"))
                    plannedPL.closedResultsDialog(index);
                }
            });


        }

        @Override
        public int getItemCount () {
            return PlannedPL.size();
        }

        class SuppliersViewHolder extends RecyclerView.ViewHolder {

            TextView serial, cust, pl, dest, order, supplier, thick, width, length, pieces, copies, isExist, edit, delete, copy;
            LinearLayout linearLayout;

            public SuppliersViewHolder(@NonNull View itemView) {
                super(itemView);
                linearLayout = itemView.findViewById(R.id.linear);

                serial = itemView.findViewById(R.id.serial);
                cust = itemView.findViewById(R.id.cust);
                pl = itemView.findViewById(R.id.pl);
                dest = itemView.findViewById(R.id.dest);
                order = itemView.findViewById(R.id.order_no);
                supplier = itemView.findViewById(R.id.supplier);
                thick = itemView.findViewById(R.id.thick);
                width = itemView.findViewById(R.id.width);
                length = itemView.findViewById(R.id.length);
                pieces = itemView.findViewById(R.id.pieces);
                copies = itemView.findViewById(R.id.copies);
                isExist = itemView.findViewById(R.id.is_exist);
                edit = itemView.findViewById(R.id.edit);
                delete = itemView.findViewById(R.id.delete);
                copy = itemView.findViewById(R.id.copy);
            }
        }
    }