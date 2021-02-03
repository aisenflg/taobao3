package com.example.taobaounion.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taobaounion.R;
import com.example.taobaounion.model.domain.SelectedContent;
import com.example.taobaounion.utils.Constants;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SelectedPageRightAdapter extends RecyclerView.Adapter<SelectedPageRightAdapter.MyViewHolder> {

    private List<SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean> mData = new ArrayList<>();
    private onSelectedPageContentListener mListener = null;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_select_page_right, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean dataBean = mData.get(position);
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean item = mData.get(position);
                    mListener.onItemRightClickListener(item);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size() > 0 ? mData.size() : 0;
    }

    public void setData(SelectedContent content) {
        if (content.getCode() == Constants.SUCCESS_CODE) {
            List<SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean> uatm_tbk_item = content.getData().getTbk_dg_optimus_material_response().getResult_list().getUatm_tbk_item();
            mData.clear();
            mData.addAll(uatm_tbk_item);
            notifyDataSetChanged();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.selected_cover)
        public ImageView cover;
        @BindView(R.id.selected_off_prise)
        public TextView offPrise;
        @BindView(R.id.selected_title)
        public TextView title;
        @BindView(R.id.selected_buy_btn)
        public TextView buyBtn;
        @BindView(R.id.selected_original_prise)
        public TextView originalPrise;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void setData(SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean dataBean) {
            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(dataBean.getPict_url())).into(cover);
            title.setText(dataBean.getTitle());
            if (TextUtils.isEmpty(dataBean.getCoupon_click_url())) {
                originalPrise.setText("晚了,没有优惠券了!");
                buyBtn.setVisibility(View.GONE);
            }else {
                originalPrise.setText("原价: "+ dataBean.getZk_final_price());
                buyBtn.setVisibility(View.VISIBLE);
            }

            if (TextUtils.isEmpty(dataBean.getCoupon_info())) {
                offPrise.setVisibility(View.GONE);
            }else {
                offPrise.setVisibility(View.VISIBLE);
                offPrise.setText(dataBean.getCoupon_info());
            }

        }
    }

    public void onItemClickListener(onSelectedPageContentListener listener){
        this.mListener = listener;
    }
    public interface onSelectedPageContentListener{
        void onItemRightClickListener(SelectedContent.DataBean.TbkUatmFavoritesItemGetResponseBean.ResultsBean.UatmTbkItemBean data);
    }

}
