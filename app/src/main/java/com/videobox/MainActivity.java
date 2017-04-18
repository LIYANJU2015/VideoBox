package com.videobox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.videobox.bean.YTBVideoPageBean;
import com.videobox.data.APIConstant;
import com.videobox.data.youtube.YouTubeFetcher;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.videobox.data.APIConstant.YouTube.sSearchMap;

/**
 * dailymotion
 *
 * https://api.dailymotion.com/playlists?search=rr
 *
 * https://api.dailymotion.com/videos?sort=recent?fields=description,
 *
 * https://api.dailymotion.com/channels?sort=popular
 * https://api.dailymotion.com/channel/music/videos
 *
 */

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Call<YTBCategoriesBean> call = youTubeService.getYouTubeCategories(APIConstant.sCategoriesMap);
//                Log.v("xx", " urlurl :: " + call.request().url());
//                call.enqueue(new Callback<YTBCategoriesBean>() {
//                    @Override
//                    public void onResponse(Call<YTBCategoriesBean> call, Response<YTBCategoriesBean> response) {
//                        Log.v("xx", "" + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<YTBCategoriesBean> call, Throwable t) {
//
//                    }
//                });
                Log.v("xx", " start ");
//                Call<YTBLanguagesBean> call = youTubeService.getYouTubeLanguages();
//                Log.v("xx", " urlurl :: " + call.request().url());
//                call.enqueue(new Callback<YTBLanguagesBean>() {
//                    @Override
//                    public void onResponse(Call<YTBLanguagesBean> call, Response<YTBLanguagesBean> response) {
//                        Log.v("xx", "" + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<YTBLanguagesBean> call, Throwable t) {
//
//                    }
//               });
                sSearchMap.put(APIConstant.YouTube.QUERY_CONTNET, "毛泽东");
                Call<YTBVideoPageBean> call = YouTubeFetcher.getInstance().getSearchVideos(APIConstant.YouTube.sSearchMap);
                Log.v("xx", " urlurl :: " + call.request().url());
                call.enqueue(new Callback<YTBVideoPageBean>() {
                    @Override
                    public void onResponse(Call<YTBVideoPageBean> call, Response<YTBVideoPageBean> response) {
                        Log.v("xx", "" + response.body());
                    }

                    @Override
                    public void onFailure(Call<YTBVideoPageBean> call, Throwable t) {

                    }
                });

            }
        });
    }

}
