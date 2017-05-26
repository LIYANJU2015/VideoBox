package com.commonlibs.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by jess on 2015/11/24.
 */
public abstract class BaseHolder<T> extends RecyclerView.ViewHolder implements View.OnClickListener {
    protected OnViewClickListener mOnViewClickListener = null;
    protected final String TAG = this.getClass().getSimpleName();

    private int viewType;

    public int getViewType() {
        return viewType;
    }

    public BaseHolder(View itemView) {
        this(itemView, true, 0);
    }

    public BaseHolder(View itemView, boolean isClick, int viewType) {
        super(itemView);
        this.viewType = viewType;
        if (isClick) {
            itemView.setOnClickListener(this);//点击事件
        }
    }



    /**
     * 设置数据
     * 刷新界面
     *
     * @param
     * @param position
     */
    public abstract void setData(T data, int position);


    /**
     * 释放资源
     */
    public void onRelease(){

    }

    @Override
    public void onClick(View view) {
        if (mOnViewClickListener != null) {
            mOnViewClickListener.onViewClick(view, this.getPosition());
        }
    }

    public interface OnViewClickListener {
        void onViewClick(View view, int position);
    }

    public void setOnItemClickListener(OnViewClickListener listener) {
        this.mOnViewClickListener = listener;
    }
}
