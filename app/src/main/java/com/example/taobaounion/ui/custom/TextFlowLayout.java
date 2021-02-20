package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.taobaounion.R;

import java.util.ArrayList;
import java.util.List;

public class TextFlowLayout extends ViewGroup {

    private List<String> mTextList = new ArrayList<>();

    public TextFlowLayout(Context context) {
        super(context);
    }

    public TextFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTextList(List<String> textList){
        this.mTextList = textList;
        //遍历内容
        for (String text : mTextList) {
            //添加子View
            TextView itemView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.flow_text_view, this, false);
            itemView.setText(text);
            addView(itemView);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }
}
