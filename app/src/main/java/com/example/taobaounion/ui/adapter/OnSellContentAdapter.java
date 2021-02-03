package com.example.taobaounion.ui.adapter;

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
import com.example.taobaounion.model.domain.OnSellContent;
import com.example.taobaounion.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OnSellContentAdapter extends RecyclerView.Adapter<OnSellContentAdapter.MyViewHolder> {

    private List<OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean> mData = new ArrayList<>();
    private onSellItemClickListener mListener = null;

    @NonNull
    @Override
    public OnSellContentAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_on_sell_content, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OnSellContentAdapter.MyViewHolder holder, int position) {
        OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean dataBean = mData.get(position);
        holder.setData(dataBean);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onSellItemClickListener(mData.get(position));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();

    }

    public void setData(OnSellContent result) {
        mData.clear();
        mData.addAll(result.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        notifyDataSetChanged();
    }

    public void addData(OnSellContent moreResult) {
        //添加之前拿到原来的size
        int olderSize = mData.size();
        mData.addAll(moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data());
        //更新ui
        notifyItemRangeChanged(olderSize,moreResult.getData().getTbk_dg_optimus_material_response().getResult_list().getMap_data().size());
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.on_sell_cover)
        public ImageView cover;
        @BindView(R.id.on_sell_title)
        public TextView title;
        @BindView(R.id.on_sell_off_prise)
        public TextView offPrise;
        @BindView(R.id.on_sell_original_prise)
        public TextView originalPrise;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean dataBean) {
            Glide.with(itemView.getContext()).load(UrlUtils.getCoverPath(dataBean.getPict_url())).into(cover);
            title.setText(dataBean.getTitle());
            originalPrise.setText("¥ " + dataBean.getZk_final_price());
            originalPrise.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            offPrise.setText("卷后价: " + String.format("%.2f", Float.parseFloat(dataBean.getZk_final_price()) - dataBean.getCoupon_amount()));
        }
    }


    public void onItemClickListener(onSellItemClickListener listener) {
        this.mListener = listener;
    }

    public interface onSellItemClickListener {
        void onSellItemClickListener(OnSellContent.DataBean.TbkDgOptimusMaterialResponseBean.ResultListBean.MapDataBean item);
    }
}
