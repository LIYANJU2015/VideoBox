package com.videobox;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.example.myapplication.R;
import com.videobox.bean.YouTubeVideoPageBean;
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

//                Call<YouTubeCategories> call = youTubeService.getYouTubeCategories(APIConstant.sCategoriesMap);
//                Log.v("xx", " urlurl :: " + call.request().url());
//                call.enqueue(new Callback<YouTubeCategories>() {
//                    @Override
//                    public void onResponse(Call<YouTubeCategories> call, Response<YouTubeCategories> response) {
//                        Log.v("xx", "" + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<YouTubeCategories> call, Throwable t) {
//
//                    }
//                });
                Log.v("xx", " start ");
//                Call<YouTubeLanguages> call = youTubeService.getYouTubeLanguages();
//                Log.v("xx", " urlurl :: " + call.request().url());
//                call.enqueue(new Callback<YouTubeLanguages>() {
//                    @Override
//                    public void onResponse(Call<YouTubeLanguages> call, Response<YouTubeLanguages> response) {
//                        Log.v("xx", "" + response.body());
//                    }
//
//                    @Override
//                    public void onFailure(Call<YouTubeLanguages> call, Throwable t) {
//
//                    }
//               });
                sSearchMap.put(APIConstant.YouTube.QUERY_CONTNET, "毛泽东");
                Call<YouTubeVideoPageBean> call = YouTubeFetcher.getInstance().getSearchVideos(APIConstant.YouTube.sSearchMap);
                Log.v("xx", " urlurl :: " + call.request().url());
                call.enqueue(new Callback<YouTubeVideoPageBean>() {
                    @Override
                    public void onResponse(Call<YouTubeVideoPageBean> call, Response<YouTubeVideoPageBean> response) {
                        Log.v("xx", "" + response.body());
                    }

                    @Override
                    public void onFailure(Call<YouTubeVideoPageBean> call, Throwable t) {

                    }
                });

            }
        });
    }

}
