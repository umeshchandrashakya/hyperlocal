package com.hyperlocal.app.ui.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.hyperlocal.app.R;
import com.hyperlocal.app.model.ActivityItemDataModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author ${Umesh} on 18-04-2018.
 */

public class MySideMenuFirstAdapter extends RecyclerView.Adapter<MySideMenuFirstAdapter.MyViewHolder> {

    private Context context;
    private List<ActivityItemDataModel> arrayList;
    private ItemClickListener listener;

    public MySideMenuFirstAdapter(Context context, ItemClickListener listener) {
        this.arrayList = new ArrayList<>();
        this.context = context;
        this.listener = listener;
    }

    public void addItemIn(ArrayList<ActivityItemDataModel> arrayList1) {
        this.arrayList = arrayList1;
        notifyDataSetChanged();
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_item_row, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.itemView.setVisibility(View.VISIBLE);
        holder.titleTextView.setText(arrayList.get(position).getItem());
        if (arrayList.get(position).getUnreadCount() == null) {
            holder.unreadCount.setVisibility(View.GONE);
        } else  {
            if(Integer.parseInt(arrayList.get(position).getUnreadCount())>0){
                holder.unreadCount.setVisibility(View.VISIBLE);
                holder.unreadCount.setText("" + arrayList.get(position).getUnreadCount());
            }else {
                holder.unreadCount.setVisibility(View.INVISIBLE);
            }
        }if (arrayList.get(position).getRequestType().equalsIgnoreCase("0")) {
            holder.subTitleTextView.setText(arrayList.get(position).getType()+"-"+"outStanding Request");
        } else if (arrayList.get(position).getRequestType().equalsIgnoreCase("1")) {
            holder.subTitleTextView.setText(arrayList.get(position).getType()+"-"+"incoming");
        } else if (arrayList.get(position).getRequestType().equalsIgnoreCase("2")) {
            holder.subTitleTextView.setText(arrayList.get(position).getType()+"-"+"invite");
        }else if (arrayList.get(position).getRequestType().equalsIgnoreCase("3")) {
            holder.subTitleTextView.setText("Conversation");
        }
        long longTimeStamp = arrayList.get(position).getTimestamp();
        holder.timeTextView.setText(convertTimeStamp(longTimeStamp));
        holder.itemView.setOnClickListener(view -> listener.onItemClick(holder.getAdapterPosition()));
        holder.itemView.setOnLongClickListener(v -> {
            listener.onLongPress(position);
            return true;

        });
        if(arrayList.size()==0){
           holder.frameLayout.setVisibility(View.VISIBLE);
        }else {
            holder.frameLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return arrayList.size();

    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.text_title)
        TextView titleTextView;
        @BindView(R.id.text_sub_title)
        TextView subTitleTextView;
        @BindView(R.id.text_time)
        TextView timeTextView;
        @BindView(R.id.tv_unreadCount) TextView unreadCount;
        @BindView(R.id.frame_layout)FrameLayout frameLayout;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface ItemClickListener {
        void onItemClick(int position);
        void onLongPress(int position);
    }


    public String convertTimeStamp(long timeStamp) {
        Date currentDate = new Date(timeStamp);
        System.out.println("current Date: " + currentDate);
        DateFormat df = new SimpleDateFormat("HH:mm a");
        System.out.println("Milliseconds to Date: " + df.format(currentDate));
        return df.format(currentDate);
    }



}
