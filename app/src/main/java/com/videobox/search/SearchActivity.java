package com.videobox.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseActivity;
import com.commonlibs.util.KeyboardUtils;
import com.commonlibs.util.LogUtils;
import com.videobox.R;
import com.videobox.model.db.VideoBoxContract;
import com.videobox.view.delegate.Contract;
import com.wang.avi.AVLoadingIndicatorView;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static rx.Emitter.BackpressureMode.NONE;

/**
 * Created by liyanju on 2017/5/6.
 */

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener,
        Filter.FilterListener, Contract.CommonHost {

    private ViewPager mSearchViewPager;
    private TabLayout mSearchTabLayout;
    private SearchView mSearchView;

    private AdapterViewPager mAdapter;

    private CharSequence mTitle[] = {"dailymotion", "youtube"};

    private DailyMotionSearchFragment mDailyMotionSearch;
    private YoutubeSearchFragment mYoutubeSearch;

    private ListView mSearchHistroyListView;

    private ArrayAdapter<String> mTextAdapter;

    private AVLoadingIndicatorView mLoadView;

    private boolean isClickSuggest;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        mSearchViewPager = (ViewPager) findViewById(R.id.search_viewPager);
        mAdapter = new AdapterViewPager(getSupportFragmentManager());
        mDailyMotionSearch = new DailyMotionSearchFragment();
        mYoutubeSearch = new YoutubeSearchFragment();
        mAdapter.bindData(mTitle, mDailyMotionSearch, mYoutubeSearch);
        mSearchViewPager.setAdapter(mAdapter);

        mSearchTabLayout = (TabLayout) findViewById(R.id.search_tabLayout);
        mSearchTabLayout.setupWithViewPager(mSearchViewPager);

        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint(getString(R.string.search));

        mSearchHistroyListView = (ListView) findViewById(R.id.search_history_listview);
        mSearchHistroyListView.setTextFilterEnabled(true);
        mSearchHistroyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                isClickSuggest = true;
                String searchContent = mTextAdapter.getItem(position);
                mSearchView.setQuery(searchContent, true);
                mSearchHistroyListView.setVisibility(View.GONE);
                isClickSuggest = false;
            }
        });

        initSearchContented();

        mSearchHistroyListView.setVisibility(View.GONE);

        mLoadView = (AVLoadingIndicatorView)findViewById(R.id.loading_view);

    }

    @Override
    public void showLoading() {
        if (mLoadView != null) {
            mLoadView.setVisibility(View.VISIBLE);
            mLoadView.show();
        }
    }

    @Override
    public void hideLoading() {
        if (mLoadView != null && mLoadView.isShown()) {
            mLoadView.hide();
        }
    }

    private void initSearchContented() {
        Observable.create(new Action1<Emitter<String[]>>() {
            @Override
            public void call(Emitter<String[]> emitter) {
                LogUtils.v("Action1 call ");
                emitter.onNext(VideoBoxContract.SearchHistory.getAllSearchHistory(mContext));
            }
        }, NONE).compose(this.<String[]>bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String[]>() {
                    @Override
                    public void call(String[] strings) {
                        LogUtils.v("call strings", strings.length);
                        if (mTextAdapter == null) {
                            mTextAdapter = new ArrayAdapter<>(mContext,
                                    R.layout.simple_list_item, strings);
                            mSearchHistroyListView.setAdapter(mTextAdapter);
                        } else {
                            mTextAdapter.addAll(strings);
                            mTextAdapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        LogUtils.v("onQueryTextSubmit", " query " + query);
        mDailyMotionSearch.gotoSearchVideo(query);
        mYoutubeSearch.gotoSearchVideo(query);

        KeyboardUtils.hideSoftInput(this);
        Observable.just(0).observeOn(Schedulers.io())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        VideoBoxContract.SearchHistory.insertNewHistroy(mContext,
                                VideoBoxContract.SearchHistory.createContentValue(query));
                    }
                });
        mTextAdapter.clear();
        initSearchContented();
        mSearchHistroyListView.setVisibility(View.GONE);
        mSearchView.clearFocus();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        LogUtils.v("onQueryTextChange", " isClickSuggest " + isClickSuggest);
        if (isClickSuggest) {
            return true;
        }
        if (mTextAdapter != null) {
            Filter filter = mTextAdapter.getFilter();
            if (newText == null || newText.length() == 0) {
                filter.filter(null);
            } else {
                filter.filter(newText, this);
            }
        }
        return true;
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onFilterComplete(int count) {
        LogUtils.v(" onFilterComplete ");
        if (count > 0) {
            mSearchHistroyListView.setVisibility(View.VISIBLE);
        } else {
            mSearchHistroyListView.setVisibility(View.GONE);
        }
    }
}
