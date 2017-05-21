package com.videobox;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.commonlibs.base.BaseApplication;
import com.commonlibs.base.GlobalConfig;
import com.commonlibs.http.GlobeHttpHandler;
import com.commonlibs.http.RequestInterceptor;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.integration.RepositoryManager;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.rxerrorhandler.handler.listener.ResponseErroListener;
import com.commonlibs.util.FileUtils;
import com.commonlibs.util.LogUtils;
import com.commonlibs.util.SPUtils;
import com.commonlibs.util.SnackbarUtils;
import com.commonlibs.util.Utils;
import com.tencent.bugly.crashreport.CrashReport;
import com.videobox.model.APIConstant;
import com.videobox.model.dailymotion.cache.DailyMotionCache;
import com.videobox.model.dailymotion.service.DailymotionService;
import com.videobox.model.youtube.YouTuBeModel;
import com.videobox.model.youtube.cache.YoutubeCache;
import com.videobox.model.youtube.entity.YTBLanguagesBean;
import com.videobox.model.youtube.entity.YTbRegionsBean;
import com.videobox.model.youtube.entity.YTbRegionsListBean;
import com.videobox.model.youtube.service.YouTubeService;

import java.util.Locale;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.schedulers.Schedulers;

import static com.videobox.model.APIConstant.DailyMontion.sWatchVideosMap;
import static com.videobox.model.APIConstant.YouTube.REGION_CODE;
import static com.videobox.model.APIConstant.YouTube.sMostPopularVideos;

/**
 * Created by liyanju on 2017/4/23.
 */

public class AppAplication extends BaseApplication implements ResponseErroListener {

    private static final String CANARO_EXTRA_BOLD_PATH = "fonts/canaro_extra_bold.otf";
    private static final String PROXIMA_NOVA_REGULAR_PATH = "fonts/Proxima_Nova_Regular.otf";
    public static Typeface sCanaroExtraBold;
    public static Typeface sProximaRegular;

    public static SPUtils spUtils;

    public static RxErrorHandler sRxErrorHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.init(getContext());
        CrashReport.initCrashReport(getApplicationContext());
        spUtils = new SPUtils("video_box");
        initTypeface();
        initYouTubeRegions();
        initYouTubeLanguages();
        sRxErrorHandler = getAppComponent().rxErrorHandler();
    }

    private void initYouTubeRegions() {
        YouTuBeModel youTuBeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        youTuBeModel.getYouTubeRegions(true)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        sMostPopularVideos.put(REGION_CODE, getCurrentRegions());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YTbRegionsListBean>() {
                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onNext(YTbRegionsListBean regionsBean) {
                        String currentCountry = Locale.getDefault().getCountry().toLowerCase();
                        if (currentCountry.equals("cn")) {
                            spUtils.put("region", "hk");
                        } if (regionsBean.items != null) {
                            for (YTbRegionsBean region : regionsBean.items) {
                                if (currentCountry.equals(region.id.toLowerCase())) {
                                    spUtils.put("region", currentCountry);
                                    break;
                                }
                            }
                        }
                        sMostPopularVideos.put(REGION_CODE, getCurrentRegions());
                    }
                });
    }

    private void initYouTubeLanguages() {
        YouTuBeModel youTuBeModel = new YouTuBeModel(getAppComponent().repositoryManager());
        youTuBeModel.getYouTubeLanguages(true)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        sWatchVideosMap.put("languages", getCurrentLanguage());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<YTBLanguagesBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(YTBLanguagesBean regionsBean) {
                        String currentLanguage = Locale.getDefault().getLanguage();
                        if (regionsBean != null && regionsBean.items != null) {
                            for (YTBLanguagesBean.Languages languages : regionsBean.items) {
                                if (currentLanguage.equals(languages.id)) {
                                    spUtils.put("language", currentLanguage);
                                    break;
                                }
                            }
                        }
                        sWatchVideosMap.put("languages", getCurrentLanguage());
                    }
                });
    }


    public static String getCurrentRegions() {
        return spUtils.getString("region", "us");
    }

    public static String getCurrentLanguage() {
        return spUtils.getString("language", "en");
    }

    private void initTypeface() {
        sCanaroExtraBold = Typeface.createFromAsset(getAssets(), CANARO_EXTRA_BOLD_PATH);
        sProximaRegular = Typeface.createFromAsset(getAssets(), PROXIMA_NOVA_REGULAR_PATH);
    }

    @Override
    public IRepositoryManager createRepositoryManager(GlobalConfig config) {
        OkHttpClient okHttpClient = config.getOkHttpClient();

        RepositoryManager repositoryManager = new RepositoryManager(this);
        Retrofit youtobeRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient)
                .baseUrl(APIConstant.YouTube.HOST_URL).build();
        repositoryManager.setRetrofitService(youtobeRetrofit, YouTubeService.class);

        Retrofit dmRetrofit = new RepositoryManager.RetrofitBuilder().client(okHttpClient)
                .baseUrl(APIConstant.DailyMontion.HOST_URL).build();
        repositoryManager.setRetrofitService(dmRetrofit, DailymotionService.class);

        repositoryManager.setCacheService(DailyMotionCache.class);
        repositoryManager.setCacheService(YoutubeCache.class);
        return repositoryManager;
    }


    @Override
    public void applyOptions(Context context, GlobalConfig.ConfigBuilder builder) {
        builder.addInterceptor(new RequestInterceptor(GlobeHttpHandler.EMPTY));
        builder.cacheFile(FileUtils.getCacheFile(this));
        builder.rxErrorHandler(RxErrorHandler.builder().with(getContext()).responseErroListener(this).build());
    }

    @Override
    public void handleResponseError(Context context, Exception e) {
        LogUtils.v("handleResponseError", e.getMessage());
        showShortSnackbar(getString(R.string.netwokr_error));
    }

    public void showShortSnackbar(String error) {
        try {
            View view = getAppComponent().appManager().getCurrentActivity()
                    .getWindow().getDecorView().findViewById(android.R.id.content);
            SnackbarUtils.showShortSnackbar(view, error,
                    ContextCompat.getColor(this, R.color.material_white),
                    ContextCompat.getColor(this, R.color.md_indigo500));
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }
}
