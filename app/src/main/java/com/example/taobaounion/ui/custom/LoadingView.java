package com.example.taobaounion.ui.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.example.taobaounion.R;

public class LoadingView extends AppCompatImageView {

    private float mDegress = 30;
    private boolean mNeedRotate = true;

    public LoadingView(Context context) {
        this(context,null);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs) {
        this(context,attrs,0);
    }

    public LoadingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setImageResource(R.mipmap.loading);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mNeedRotate = true;
        startRotate();      
    }

    private void startRotate() {
        post(new Runnable() {
            @Override
            public void run() {
                mDegress += 10;
                if (mDegress >= 360) {
                    mDegress = 0;
                }
                invalidate();
                //判断是否继续旋转
                if (getVisibility() != VISIBLE && !mNeedRotate) {
                    removeCallbacks(this);
                }else {
                    postDelayed(this,10);
                }
            }
        });
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        stopRotate();
    }

    private void stopRotate() {
        mNeedRotate = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.rotate(mDegress,getWidth()/2,getHeight()/2);
        super.onDraw(canvas);
    }
}
