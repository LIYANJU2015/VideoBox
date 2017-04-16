package com.videobox.commonlibs.network.retrofit;

import android.text.TextUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by liyanju on 2017/3/24.
 */

public class RetrofitManager {

    private static RetrofitManager sRetrofitManager;

    private Retrofit mRetrofit;

    private RetrofitManager(String serverUrl) {
        if (TextUtils.isEmpty(serverUrl)) {
            throw new NullPointerException("SERVER_URL is null !!!");
        }
        mRetrofit = new Retrofit.Builder().baseUrl(serverUrl)
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public <T> T create(Class<T> clazz) {
        return mRetrofit.create(clazz);
    }

    public static RetrofitManager create(String serverUrl) {
        return new RetrofitManager(serverUrl);
    }
}
