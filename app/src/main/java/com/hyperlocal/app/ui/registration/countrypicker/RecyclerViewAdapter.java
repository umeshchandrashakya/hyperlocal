package com.hyperlocal.app.ui.registration.countrypicker;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hyperlocal.app.R;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @Author ${Umesh} on 07/10/15.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>
    implements RecyclerViewFastScroller.BubbleTextGetter {

    private List<Object> mDataArray;
    private OnItemClickListener listener;

    public RecyclerViewAdapter(Context context, List<Object> arrayList,OnItemClickListener listener) {
        setHasStableIds(true);
        this.mDataArray = arrayList;
        this.listener = listener;

    }

    public void updateData(List<Object> arrayList){
        this.mDataArray = arrayList;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if (mDataArray == null)
            return 0;
        return mDataArray.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_view_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
            final Object object = mDataArray.get(position);
            if(object instanceof CountryDetails){
               holder.mTextView.setText(((CountryDetails) object).getCountryName());
               holder.tvCode.setText(((CountryDetails) object).getDialCode());
               holder.tvHolder.setVisibility(View.GONE);
               holder.tvHolder.setText("");
                holder.tvCode.setVisibility(View.VISIBLE);
                holder.mTextView.setVisibility(View.VISIBLE);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                  listener.geCountryItem(holder.getAdapterPosition());
                        //Toast.makeText(context, ""+((CountryDetails) object).getCountryName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }else {
                holder.tvHolder.setVisibility(View.VISIBLE);
                holder.tvHolder.setText(object.toString());
                holder.tvCode.setVisibility(View.GONE);
                holder.mTextView.setVisibility(View.GONE);
            }


    }

    @Override
    public String getTextToShowInBubble(int pos) {
        /*if (pos < 0 || pos >= mDataArray.size())
            return null;
        String arrayList = mDataArray.get(pos).getCountryName();

        if (arrayList == null || mDataArray.size() < 1){
            return null;
        }

        return mDataArray.get(pos).getCountryName().substring(0, 1);*/
       return  "";

    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_alphabet) TextView mTextView;
        @BindView(R.id.tv_code)TextView tvCode;
        @BindView(R.id.tv_header)TextView tvHolder;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


 public interface OnItemClickListener{
        void geCountryItem(int position);
 }
}
