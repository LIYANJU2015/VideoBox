package com.videobox.search;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.commonlibs.base.AdapterViewPager;
import com.commonlibs.base.BaseActivity;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.SizeUtils;
import com.commonlibs.util.StatusBarColorCompat;
import com.commonlibs.util.ThreadPoolUtils;
import com.commonlibs.util.UIThreadHelper;
import com.lapism.searchview.SearchAdapter;
import com.lapism.searchview.SearchHistoryTable;
import com.lapism.searchview.SearchItem;
import com.lapism.searchview.SearchView;
import com.videobox.R;
import com.videobox.model.db.VideoBoxContract;

import java.util.ArrayList;
import java.util.List;

import rx.Emitter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static rx.Emitter.BackpressureMode.BUFFER;
import static rx.Emitter.BackpressureMode.LATEST;
import static rx.Emitter.BackpressureMode.NONE;

/**
 * Created by liyanju on 2017/5/6.
 */

public class SearchActivity extends BaseActivity implements SearchView.OnQueryTextListener {

    private ViewPager mSearchViewPager;
    private TabLayout mSearchTabLayout;
    private SearchView mSearchView;

    private AdapterViewPager mAdapter;

    private CharSequence mTitle[] = {"dailymotion", "youtube"};

    private DailyMotionSearchFragment mDailyMotionSearch;
    private YoutubeSearchFragment mYoutubeSearch;

    private List<SearchItem> suggestionsList = new ArrayList<>();

    private AppBarLayout mSearchAppBar;

    private Context mContext;

    private SearchAdapter mSearchAdapter;

    private SearchHistoryTable mHistoryDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        mContext = getApplicationContext();
        mHistoryDatabase = new SearchHistoryTable(this);

        mSearchAppBar = (AppBarLayout)findViewById(R.id.search_app_bar);
        mSearchAppBar.setBackgroundColor(ContextCompat.
                getColor(mContext, R.color.dailymotion_color));
        StatusBarColorCompat.setColorNoTranslucent(this,
                ContextCompat.getColor(mContext, R.color.dailymotion_color));

        initSearchViewPager();

        initSearchTab();

        initSearchView();

        UIThreadHelper.getInstance().runViewUIThread(mSearchView, new Runnable() {
            @Override
            public void run() {
                initSearchSuggestions();
            }
        });
    }

    private void initSearchViewPager() {
        mSearchViewPager = (ViewPager) findViewById(R.id.search_viewPager);
        mAdapter = new AdapterViewPager(getSupportFragmentManager());
        mDailyMotionSearch = new DailyMotionSearchFragment();
        mYoutubeSearch = new YoutubeSearchFragment();
        mAdapter.bindData(mTitle, mDailyMotionSearch, mYoutubeSearch);
        mSearchViewPager.setAdapter(mAdapter);
    }

    private void initSearchView() {
        mSearchView = (SearchView) findViewById(R.id.search_view);
        mSearchView.setHint(R.string.search);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setVoiceText(getString(R.string.voice_tips));
    }

    private void initSearchViewAdapter() {
        mSearchAdapter = new SearchAdapter(this, suggestionsList);
        mSearchAdapter.addOnItemClickListener(new SearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                TextView textView = (TextView) view.findViewById(R.id.textView_item_text);
                String query = textView.getText().toString();
                submitSearch(query);
                mSearchView.setTextOnly(query);
            }
        });
        mSearchView.setAdapter(mSearchAdapter);
    }

    private void initSearchTab() {
        mSearchTabLayout = (TabLayout) findViewById(R.id.search_tabLayout);
        mSearchTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mSearchTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        mSearchTabLayout.setupWithViewPager(mSearchViewPager);
        mSearchTabLayout.setSelectedTabIndicatorHeight(SizeUtils.dp2px(3));
        mSearchTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()){
                    case 0 : mSearchAppBar.setBackgroundColor(ContextCompat.
                            getColor(mContext, R.color.dailymotion_color));
                        StatusBarColorCompat.setColorNoTranslucent(SearchActivity.this,
                                ContextCompat.getColor(mContext, R.color.dailymotion_color));
                        break;
                    case 1 :
                        mSearchAppBar.setBackgroundColor(ContextCompat.
                                getColor(mContext, R.color.youtube_color));
                        StatusBarColorCompat.setColorNoTranslucent(SearchActivity.this,
                                ContextCompat.getColor(mContext, R.color.youtube_color));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void initSearchSuggestions() {
        if (mSearchAdapter == null) {
            initSearchViewAdapter();
            mSearchView.open(true);
        }

//        Observable.create(new Action1<Emitter<String[]>>() {
//            @Override
//            public void call(Emitter<String[]> emitter) {
//                LogUtils.v("Action1 call ");
//                String[] strings = VideoBoxContract.SearchHistory.getAllSearchHistory(mContext);
//                emitter.onNext(strings);
//            }
//        }, NONE).compose(this.<String[]>bindToLifecycle())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<String[]>() {
//                    @Override
//                    public void call(String[] strings) {
//                        LogUtils.v("call strings", strings.length);
//                        for (String suggestions : strings) {
//                            suggestionsList.add(new SearchItem(R.drawable.ic_history_black_24dp,
//                                    suggestions));
//                        }
//                        if (mSearchAdapter == null) {
//                            initSearchViewAdapter();
//                            mSearchAdapter.setSuggestionsList(suggestionsList);
//                            mSearchView.showSuggestions();
//                        }
//                    }
//                });
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        submitSearch(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void submitSearch(final String query) {
        LogUtils.v("submitSearch", " query " + query);
        mSearchView.close(false);
        mDailyMotionSearch.gotoSearchVideo(query);
        mYoutubeSearch.gotoSearchVideo(query);

        Observable.just(0).observeOn(Schedulers.io())
                .subscribe(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        mHistoryDatabase.addItem(new SearchItem(query));
                        VideoBoxContract.SearchHistory.insertNewHistroy(mContext,
                                VideoBoxContract.SearchHistory.createContentValue(query));
                    }
                });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
