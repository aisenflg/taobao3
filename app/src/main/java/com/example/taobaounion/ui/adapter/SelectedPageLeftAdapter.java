package com.example.taobaounion.ui.adapter;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.SelectedPageCategory;

import java.util.ArrayList;
import java.util.List;

public class SelectedPageLeftAdapter extends RecyclerView.Adapter<SelectedPageLeftAdapter.MyViewHolder> {

    private List<SelectedPageCategory.DataBean> mData = new ArrayList<>();
    private int mCurrentSelectedPosition = 0;
    private onLeftItemClickListener mOnLeftItemClickListener;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_page_left, parent, false);
        return new MyViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        TextView titleTv = holder.itemView.findViewById(R.id.left_category_title);
        if (mCurrentSelectedPosition == position) {
            titleTv.setBackgroundColor(titleTv.getResources().getColor(R.color.colorEFEEEE, null));
        }else {
            titleTv.setBackgroundColor(titleTv.getResources().getColor(R.color.white, null));
        }
        titleTv.setText(mData.get(position).getFavorites_title());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnLeftItemClickListener != null && mCurrentSelectedPosition != position) {
                    //修改当前选中的位置
                    mCurrentSelectedPosition = position;
                    mOnLeftItemClickListener.onLeftItemClick(mData.get(position));
                    notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size() != 0 ? mData.size() : 0;
    }

    public void setData(SelectedPageCategory result) {
        List<SelectedPageCategory.DataBean> data = result.getData();
        if (data != null) {
            mData.clear();
            mData.addAll(data);
            notifyDataSetChanged();
        }
        if (mData.size()>0) {
            mOnLeftItemClickListener.onLeftItemClick(mData.get(mCurrentSelectedPosition));
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public interface onLeftItemClickListener{
        void onLeftItemClick(SelectedPageCategory.DataBean data);
    }



    public void setItemClickListener(onLeftItemClickListener listener){
        mOnLeftItemClickListener = listener;
    }
}

