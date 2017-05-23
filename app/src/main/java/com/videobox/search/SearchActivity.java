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
import android.view.ViewGroup;
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

import q.rorbin.badgeview.Badge;
import q.rorbin.badgeview.QBadgeView;
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
        mHistoryDatabase = SearchHistoryTable.getSearchHistoryTable(mContext);

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
        mSearchView.setVersionMargins(SearchView.VERSION_MARGINS_TOOLBAR_SMALL);
        mSearchView.setArrowOnly(true);
        mSearchView.setOnMenuClickListener(new SearchView.OnMenuClickListener() {
            @Override
            public void onMenuClick() {
                SearchActivity.this.finish();
            }
        });
    }

    private void initSearchViewAdapter() {
        new AsyncTask<Void, Void ,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                List<SearchItem> searchItems = mHistoryDatabase.getAllItems(null);
                if (searchItems != null) {
                    suggestionsList.addAll(searchItems);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mSearchAdapter = new SearchAdapter(mContext);
                mSearchAdapter.setSuggestionsList(suggestionsList);
                mSearchAdapter.setOnSearchItemClickListener(new SearchAdapter.OnSearchItemClickListener() {

                    @Override
                    public void onSearchItemClick(View view, int i) {
                        TextView textView = (TextView) view.findViewById(R.id.textView);
                        String query = textView.getText().toString();
                        mSearchView.setTextOnly(query);
                        submitSearch(query);
                    }
                });
                mSearchView.setAdapter(mSearchAdapter);
                mSearchView.open(true);
            }
        }.execute();
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
                switch (tab.getPosition()) {
                    case 0:
                        mSearchAppBar.setBackgroundColor(ContextCompat.
                                getColor(mContext, R.color.dailymotion_color));
                        StatusBarColorCompat.setColorNoTranslucent(SearchActivity.this,
                                ContextCompat.getColor(mContext, R.color.dailymotion_color));
                        break;
                    case 1:
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
        UIThreadHelper.getInstance().runViewUIThread(mSearchTabLayout, new Runnable() {
            @Override
            public void run() {
                setDailyMotionTabBadge(0);
                setYouTubeTabBadge(0);
            }
        });
    }

    private void initSearchSuggestions() {
        if (mSearchAdapter == null) {
            initSearchViewAdapter();
        }
    }

    @Override
    public boolean onQueryTextSubmit(final String query) {
        submitSearch(query);
        return true;
    }

    Badge dailyMotionBadge;
    Badge youtubeBadge;

    public void setDailyMotionTabBadge(int num) {
        if (dailyMotionBadge == null) {
            View dialyMotionView = ((ViewGroup) mSearchTabLayout.getChildAt(0)).getChildAt(0);
            QBadgeView qBadgeView = new QBadgeView(mContext);
            dailyMotionBadge = qBadgeView.bindTarget(dialyMotionView);
            dailyMotionBadge.setExactMode(true);
            dailyMotionBadge.hide(true);
        }
        if (num > 0) {
            dailyMotionBadge.hide(false);
            dailyMotionBadge.setBadgeNumber(num);
        } else {
            dailyMotionBadge.hide(true);
        }
    }

    public void setYouTubeTabBadge(int num) {
        if (youtubeBadge == null) {
            View view = ((ViewGroup) mSearchTabLayout.getChildAt(0)).getChildAt(1);
            QBadgeView qBadgeView = new QBadgeView(mContext);
            youtubeBadge = qBadgeView.bindTarget(view);
            youtubeBadge.setExactMode(true);
            youtubeBadge.hide(true);
        }
        if (num > 0) {
            youtubeBadge.hide(false);
            youtubeBadge.setBadgeNumber(num);
        } else {
            youtubeBadge.hide(true);
        }
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
                    }
                });
    }

    public static void launch(Context context) {
        Intent intent = new Intent(context, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

}
