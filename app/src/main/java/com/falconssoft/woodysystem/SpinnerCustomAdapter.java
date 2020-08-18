package com.falconssoft.woodysystem;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.falconssoft.woodysystem.models.SpinnerModel;
import com.falconssoft.woodysystem.reports.InventoryReport;

import java.util.List;

public class SpinnerCustomAdapter extends ArrayAdapter<SpinnerModel> {

    private InventoryReport context;
    private List<SpinnerModel> list;
    private boolean isFromView = false;
    private int flag;


    public SpinnerCustomAdapter(@NonNull InventoryReport context, int resource, @NonNull List<SpinnerModel> list, int flag) {
        super(context, resource, list);
        this.context = context;
        this.list = list;
        this.flag = flag;
//        this.myAdapter = this;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    public View getCustomView(final int position, View convertView, ViewGroup parent) {

        SpinnerViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.spinner_with_checkbox, null);
            holder = new SpinnerViewHolder();
            holder.title = convertView.findViewById(R.id.spinner_checkbox_title);
            holder.checkBox = convertView.findViewById(R.id.spinner_checkbox_check);
            convertView.setTag(holder);
        } else {
            holder = (SpinnerViewHolder) convertView.getTag();
        }

        holder.title.setText(list.get(position).getTitle());

        // To check weather checked event fire from getview() or user input
        isFromView = true;
        holder.checkBox.setChecked(list.get(position).isChecked());
        isFromView = false;

        if ((position == 0)) {
            holder.checkBox.setVisibility(View.INVISIBLE);
        } else {
            holder.checkBox.setVisibility(View.VISIBLE);
        }
        holder.checkBox.setTag(position);

        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int getPosition = (Integer) buttonView.getTag();

                if (!isFromView) {
                    list.get(position).setChecked(isChecked);
                    context.sendOtherLists(list, flag);
                    Log.e("spinneradapter", list.get(position).getTitle() + list.get(position).isChecked());
                }
            }
        });
        return convertView;
    }

    class SpinnerViewHolder {
        TextView title;
        CheckBox checkBox;
    }


}
