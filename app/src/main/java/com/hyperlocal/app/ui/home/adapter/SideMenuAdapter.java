package com.hyperlocal.app.ui.home.adapter;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyperlocal.app.R;
import com.hyperlocal.app.ui.home.SideMenuItemModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author ${Umesh} on 05-04-2018.
 */

public class SideMenuAdapter extends RecyclerView.Adapter<SideMenuAdapter.MyViewHolder> {

    private List<SideMenuItemModel> itemModelArrayList;
    private SideMenuItemClickListener listener;
    private Context context;

    public SideMenuAdapter(Context context, List<SideMenuItemModel> itemModelArrayList, SideMenuItemClickListener listener) {
        this.context = context;
        this.itemModelArrayList = itemModelArrayList;
        this.listener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.side_menu_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.imageView.setImageDrawable(context.getDrawable(itemModelArrayList.get(position).getIcon()));
        holder.itemTextView.setText(itemModelArrayList.get(position).getItemName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              listener.onSideMenuItemClicked(holder.getAdapterPosition());
            }
        });


    }

    @Override
    public int getItemCount() {
        return itemModelArrayList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.image_view) ImageView imageView;
        @BindView(R.id.item_text_view) TextView itemTextView;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface SideMenuItemClickListener {
        void onSideMenuItemClicked(int position);
    }
}
