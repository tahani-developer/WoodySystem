package com.falconssoft.woodysystem.reports;

import android.arch.paging.PagedListAdapter;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.falconssoft.woodysystem.R;
import com.falconssoft.woodysystem.models.Orders;

import java.util.List;

class TempLoadingAdapter extends PagedListAdapter<Orders, TempLoadingAdapter.LoadingViewHolder> {

    private LoadingOrderReport context;
    //    private List<BundleInfo> mOriginalValues;
    private static List<Orders> itemsList;
    private static List<Orders> bundles;

//    private Context mCtx;

//    ItemAdapter(Context mCtx) {
//        super(DIFF_CALLBACK);
//        this.mCtx = mCtx;
//    }
     public TempLoadingAdapter(LoadingOrderReport context, List<Orders> itemsList, List<Orders> bundles) {
         super(DIFF_CALLBACK);
         this.context = context;
//        this.mOriginalValues = itemsList;
        this.itemsList = itemsList;
        this.bundles = bundles;
//        selectedBundles = new ArrayList<>();
    }


    //    class ItemViewHolder extends RecyclerView.ViewHolder {
//
//        TextView textView;
//        ImageView imageView;
//
//        public ItemViewHolder(View itemView) {
//            super(itemView);
//            textView = itemView.findViewById(R.id.textViewName);
//            imageView = itemView.findViewById(R.id.imageView);
//        }
//    }
     class LoadingViewHolder extends RecyclerView.ViewHolder {
        ImageView pic;
        Button bundleNo;
        TextView orderNo, truckNo, containerNo, date, destination;
        ImageView edit;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);

            orderNo =  itemView.findViewById(R.id.order);
            truckNo =  itemView.findViewById(R.id.truck);
            containerNo =  itemView.findViewById(R.id.container);
            date =  itemView.findViewById(R.id.date);
            destination =  itemView.findViewById(R.id.destination);
            bundleNo =  itemView.findViewById(R.id.bundleNo);
            pic =  itemView.findViewById(R.id.pic);
            edit = itemView.findViewById(R.id.loading_order_raw_edit);

        }
    }


    @NonNull
    @Override
    public LoadingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.loading_order_report_row, parent, false);
        return new LoadingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoadingViewHolder holder, int i) {
        holder.orderNo.setText(itemsList.get(i).getOrderNo());
        holder.truckNo.setText(itemsList.get(i).getPlacingNo());
        holder.containerNo.setText(itemsList.get(i).getContainerNo());
        holder.date.setText(itemsList.get(i).getDateOfLoad());
        holder.destination.setText(itemsList.get(i).getDestination());

        LoadingOrderReport obj = new LoadingOrderReport();

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                context.goToEditPage(itemsList.get(i));
                Log.e("showwwwwwwwwwww1", itemsList.get(i).getPlacingNo());

            }
        });

        holder.bundleNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                ArrayList<BundleInfo> bundleInfos = new ArrayList<>();
//
//                for (int k = 0; k < bundles.size(); k++) {
////                                    Log.e("ooo  " , ""+ orders.get(index).getOrderNo() + "  " + bundles.get(i).getBundleNo());
//                    if (itemsList.get(i).getOrderNo().equals(bundles.get(k).getOrderNo()) &&
//                            itemsList.get(i).getPlacingNo().equals(bundles.get(k).getPlacingNo()) &&
//                            itemsList.get(i).getContainerNo().equals(bundles.get(k).getContainerNo()) &&
//                            itemsList.get(i).getDateOfLoad().equals(bundles.get(k).getDateOfLoad())) {
//
//                        bundleInfos.add(new BundleInfo(
//                                bundles.get(k).getThickness(),
//                                bundles.get(k).getWidth(),
//                                bundles.get(k).getLength(),
//                                bundles.get(k).getGrade(),
//                                bundles.get(k).getNoOfPieces(),
//                                bundles.get(k).getBundleNo(),
//                                bundles.get(k).getLocation(),
//                                bundles.get(k).getArea(),
//                                "",
//                                bundles.get(k).getPicture()));
//                    }
//                }
                obj.previewLinear(i, context, itemsList);

            }
        });

        holder.pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                obj.previewPics(i, context);
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Log.e("rawtttttttttt", "position" + i );

                context.deleteOrder(itemsList, i);
                return false;
            }
        });
    }

//    DIFF_CALLBACK implementation that we are passing to the super().
//    This callback is used to differentiate two items in a List.
    private static DiffUtil.ItemCallback<Orders> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<Orders>() {
                @Override
                public boolean areItemsTheSame(Orders oldItem, Orders newItem) {
                    return oldItem.getBundleNo() == newItem.getBundleNo();
                }

                @Override
                public boolean areContentsTheSame(Orders oldItem, Orders newItem) {
                    return oldItem.equals(newItem);
                }
            };

}
