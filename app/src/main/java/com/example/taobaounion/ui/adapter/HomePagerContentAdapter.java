package com.example.taobaounion.ui.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.HomePagerContent;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomePagerContentAdapter extends RecyclerView.Adapter<HomePagerContentAdapter.MyViewHolder> {

    List<HomePagerContent.DataBean> mData = new ArrayList<>();
    private onListItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_home_pager_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        HomePagerContent.DataBean dataBean = mData.get(position);
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItemClickListener != null) {
                    HomePagerContent.DataBean item = mData.get(position);
                    mItemClickListener.onItemClick(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size()> 0 ? mData.size() : 0;
    }

    public void setData(List<HomePagerContent.DataBean> contents) {
        mData.clear();
        mData.addAll(contents);
        notifyDataSetChanged();
    }

    public void addData(List<HomePagerContent.DataBean> contents) {
        //添加之前拿到原来的size
        int olderSize = mData.size();
        mData.addAll(contents);
        //更新ui
        notifyItemRangeChanged(olderSize,contents.size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.goods_cover)
        public ImageView cover;

        @BindView(R.id.goods_title)
        public TextView title;

        @BindView(R.id.goods_off_prise)
        public TextView offPriceTv;

        @BindView(R.id.goods_after_off_prise)
        public TextView finalPriceTv;

        @BindView(R.id.good_original_prise)
        public TextView originalPriceTv;

        @BindView(R.id.goods_sell_count)
        public TextView sellCountTv;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }


        public void setData(HomePagerContent.DataBean dataBean) {
            Context context = itemView.getContext();
            title.setText(dataBean.getTitle());
            ViewGroup.LayoutParams layoutParams = cover.getLayoutParams();
            Glide.with(context).load(UrlUtils.getCoverPath(dataBean.getPict_url())).into(cover);
            int couponAmount = dataBean.getCoupon_amount();
            String finalPrice = dataBean.getZk_final_price();
            float resultPrise = Float.parseFloat(finalPrice )- couponAmount;
            finalPriceTv.setText(String.format("%.2f",resultPrise));
            offPriceTv.setText(String.format(context.getString(R.string.text_goods_off_prise), dataBean.getCoupon_amount()));
            originalPriceTv.setText(String.format(context.getString(R.string.text_goods_original_prise), finalPrice));
            originalPriceTv.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            sellCountTv.setText(String.format(context.getString(R.string.text_sell_count), dataBean.getVolume()));
        }
    }

    public void setonListItemClickListener(onListItemClickListener listener){
        this.mItemClickListener = listener;
    }


    public interface onListItemClickListener{
        void onItemClick(HomePagerContent.DataBean item);
    }
}
