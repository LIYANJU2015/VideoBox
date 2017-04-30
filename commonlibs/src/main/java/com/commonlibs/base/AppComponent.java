package com.commonlibs.base;

import android.app.Application;

import com.commonlibs.integration.AppManager;
import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.rxerrorhandler.core.RxErrorHandler;
import com.commonlibs.widget.imageloader.ImageLoader;
import com.google.gson.Gson;

import java.io.File;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * Created by liyanju on 2017/4/23.
 */

public interface AppComponent {

    //用于管理网络请求层,以及数据缓存层
    IRepositoryManager repositoryManager();

    OkHttpClient okHttpClient();

    //图片管理器,用于加载图片的管理类,默认使用glide,使用策略模式,可替换框架
    ImageLoader imageLoader();

    //缓存文件根目录(RxCache和Glide的的缓存都已经作为子文件夹在这个目录里),应该将所有缓存放到这个根目录里,便于管理和清理,可在GlobeConfigModule里配置
    File cacheFile();

    //用于管理所有activity
    AppManager appManager();

    RxErrorHandler rxErrorHandler();
}
