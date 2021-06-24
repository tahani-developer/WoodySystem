package com.falconssoft.woodysystem.stage_one;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.NewRowInfo;

import java.util.List;


public class AddNewAdapter extends RecyclerView.Adapter<AddNewAdapter.EditPageViewHolder> {

    private List<NewRowInfo> list;
    private AddNewRaw editPage;

    public AddNewAdapter(AddNewRaw editPage, List<NewRowInfo> list) {
        this.list = list;
        this.editPage = editPage;
    }

    @NonNull
    @Override
    public EditPageViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.edit_row, viewGroup, false);
        return new EditPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditPageViewHolder holder, @SuppressLint("RecyclerView") int i) {

        holder.supplier.setText(list.get(i).getSupplierName());
        holder.thickness.setText("" + list.get(i).getThickness());
        holder.width.setText("" + list.get(i).getWidth());
        holder.length.setText("" + list.get(i).getLength());
        holder.pieces.setText("" + list.get(i).getNoOfPieces());
        holder.rejected.setText("" + list.get(i).getNoOfRejected());
        holder.bundles.setText("" + list.get(i).getNoOfBundles());
        holder.grade.setText(list.get(i).getGrade());

        double truckCbm=0,rejCbm=0,acceptCbm=0;

        truckCbm=(list.get(i).getThickness()*
                list.get(i).getWidth()*
                list.get(i).getLength()*
                list.get(i).getNoOfPieces()*
                list.get(i).getNoOfBundles())/1000000000;

         rejCbm= (list.get(i).getThickness()*
                list.get(i).getWidth()*
                list.get(i).getLength()*
                list.get(i).getNoOfRejected())/1000000000;

        rejCbm=Double.parseDouble(String.format("%.3f", rejCbm));
        truckCbm=Double.parseDouble(String.format("%.3f", truckCbm));

         acceptCbm=truckCbm-rejCbm;
        acceptCbm=Double.parseDouble(String.format("%.3f", acceptCbm));

        list.get(i).setTruckCMB(""+truckCbm);
        list.get(i).setCbmRej(""+rejCbm);
        list.get(i).setCbmAccept(""+acceptCbm);

   editPage.fillCbmVal(i,truckCbm,rejCbm,acceptCbm);

        holder.truckCbm.setText(""+truckCbm);
        holder.rejCbm.setText(""+rejCbm);
        holder.acceptCbm.setText(""+acceptCbm);



        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editPage.EditDialog(list.get(i),i);

            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               editPage.deleteFlag(i);
            }
        });

//        holder.image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                editPage.previewPics(list.get(i), editPage);
//            }
//        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class EditPageViewHolder extends RecyclerView.ViewHolder {
        TextView supplier, thickness, width, length, pieces, rejected, bundles, grade,truckCbm,rejCbm,acceptCbm;
        ImageView delete,edit;

        public EditPageViewHolder(@NonNull View itemView) {
            super(itemView);

            supplier = itemView.findViewById(R.id.editRow_supplier);
            thickness = itemView.findViewById(R.id.editRow_thickness);
            width = itemView.findViewById(R.id.editRow_width);
            length = itemView.findViewById(R.id.editRow_length);
            pieces = itemView.findViewById(R.id.editRow_pieces);
            rejected = itemView.findViewById(R.id.editRow_rejected);
            bundles = itemView.findViewById(R.id.editRow_bundles);
            grade = itemView.findViewById(R.id.editRow_grade);
            delete = itemView.findViewById(R.id.editRow_delete);
            edit = itemView.findViewById(R.id.editRow_edit);


            truckCbm = itemView.findViewById(R.id.editRow_truckCbm);
            rejCbm = itemView.findViewById(R.id.editRow_cbmRej);
            acceptCbm = itemView.findViewById(R.id.editRow_cbmAccept);
        }
    }
}
