package com.videobox.view.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.commonlibs.base.BaseHolder;

import java.util.List;

/**
 * Created by jess on 2015/11/27.
 */
public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseHolder<T>> {
    protected List<T> mInfos;
    protected OnRecyclerViewItemClickListener mOnItemClickListener = null;
    private BaseHolder<T> mHolder;

    private AdViewWrapperAdapter mAdViewAdapter;

    public BaseRecyclerViewAdapter(List<T> infos) {
        super();
        this.mInfos = infos;
    }

    public void clearAdapter() {
        if (mInfos != null) {
            mInfos.clear();
        }
        if (mAdViewAdapter != null) {
            mAdViewAdapter.clearAdView();
        }
        notifyDataSetChanged();
    }

    public boolean isAddAdView() {
        if (mAdViewAdapter != null) {
            return mAdViewAdapter.isAddAdView();
        }
        return false;
    }

    public int getAddAdViewCount() {
        return mAdViewAdapter.getAddAdViewCount();
    }

    public void setAdViewAdapter(AdViewWrapperAdapter adViewAdapter) {
        mAdViewAdapter = adViewAdapter;
    }

    public void setData(List<T> infos){
        mInfos = infos;
    }

    /**
     * 创建Hodler
     *
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public BaseHolder<T> onCreateViewHolder(ViewGroup parent, final int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(getLayoutId(viewType), parent, false);
        mHolder = getHolder(view, viewType);
        mHolder.setViewType(viewType);
//        mHolder.setOnItemClickListener(new BaseHolder.OnViewClickListener() {//设置Item点击事件
//            @Override
//            public void onViewClick(View view, int position) {
//                if (mOnItemClickListener != null && mInfos.size() > 0) {
//                    mOnItemClickListener.onItemClick(view, viewType, mInfos.get(position), position);
//                }
//            }
//        });
        return mHolder;
    }

    /**
     * 绑定数据
     *
     * @param holder
     * @param
     */
    @Override
    public void onBindViewHolder(final BaseHolder<T> holder, final int newPosition) {
        if (mInfos.size() <= newPosition) {
            return;
        }
        holder.setData(mInfos.get(newPosition), newPosition);
        holder.setOnItemClickListener(new BaseHolder.OnViewClickListener() {//设置Item点击事件
            @Override
            public void onViewClick(View view, int position) {
                if (mOnItemClickListener != null && mInfos.size() > 0) {
                    mOnItemClickListener.onItemClick(view, holder.getViewType(), mInfos.get(newPosition), newPosition);
                }
            }
        });
    }


    /**
     * 数据的个数
     *
     * @return
     */
    @Override
    public int getItemCount() {
        return mInfos.size();
    }


    public List<T> getInfos() {
        return mInfos;
    }

    /**
     * 获得item的数据
     *
     * @param position
     * @return
     */
    public T getItem(int position) {
        return mInfos == null ? null : mInfos.get(position);
    }

    /**
     * 子类实现提供holder
     *
     * @param v
     * @param viewType
     * @return
     */
    public abstract BaseHolder<T> getHolder(View v, int viewType);

    /**
     * 提供Item的布局
     *
     * @param viewType
     * @return
     */
    public abstract int getLayoutId(int viewType);


    /**
     * 遍历所有hodler,释放他们需要释放的资源
     *
     * @param recyclerView
     */
    public static void releaseAllHolder(RecyclerView recyclerView) {
        if (recyclerView == null) return;
        for (int i = recyclerView.getChildCount() - 1; i >= 0; i--) {
            final View view = recyclerView.getChildAt(i);
            RecyclerView.ViewHolder viewHolder = recyclerView.getChildViewHolder(view);
            if (viewHolder != null && viewHolder instanceof BaseHolder) {
                ((BaseHolder) viewHolder).onRelease();
            }
        }
    }


    public interface OnRecyclerViewItemClickListener<T> {
        void onItemClick(View view, int viewType, T data, int position);
    }

    public void setOnItemClickListener(OnRecyclerViewItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
