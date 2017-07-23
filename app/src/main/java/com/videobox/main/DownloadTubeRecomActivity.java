package com.videobox.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gigamole.infinitecycleviewpager.HorizontalInfiniteCycleViewPager;
import com.videobox.R;

/**
 * Created by liyanju on 2017/7/23.
 */

public class DownloadTubeRecomActivity extends Activity {

    public static void launch(Context context) {
        Intent intent = new Intent(context, DownloadTubeRecomActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recom_layout);

        findViewById(R.id.download_tv).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        final HorizontalInfiniteCycleViewPager infiniteCycleViewPager =
                (HorizontalInfiniteCycleViewPager) findViewById(R.id.recom_viewpager);
        HorizontalPagerAdapter adapter = new HorizontalPagerAdapter(this);
        infiniteCycleViewPager.setAdapter(adapter);
    }


    public static class LibraryObject {

        private String mTitle = "";
        private int mRes;

        public LibraryObject(final int res, final String title) {
            mRes = res;
            mTitle = title;
        }

        public String getTitle() {
            return mTitle;
        }

        public void setTitle(final String title) {
            mTitle = title;
        }

        public int getRes() {
            return mRes;
        }

        public void setRes(final int res) {
            mRes = res;
        }
    }

    public class HorizontalPagerAdapter extends PagerAdapter {

        private final LibraryObject[] LIBRARIES = new LibraryObject[]{
                new LibraryObject(
                        R.drawable.screen1,
                        "Strategy"
                ),
                new LibraryObject(
                        R.drawable.screen2,
                        "Design"
                ),
                new LibraryObject(
                        R.drawable.screen3,
                        "Development"
                ),
                new LibraryObject(
                        R.drawable.screen4,
                        "Quality Assurance"
                ),
                new LibraryObject(
                        R.drawable.screen5,
                        "Quality Assurance"
                )
        };

        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public HorizontalPagerAdapter(final Context context) {
            mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return LIBRARIES.length;
        }

        @Override
        public int getItemPosition(final Object object) {
            return POSITION_NONE;
        }


        public void setupItem(final View view, final LibraryObject libraryObject) {
            final TextView txt = (TextView) view.findViewById(R.id.txt_item);
            txt.setText(libraryObject.getTitle());

            final ImageView img = (ImageView) view.findViewById(R.id.img_item);
            img.setImageResource(libraryObject.getRes());
        }

        @Override
        public Object instantiateItem(final ViewGroup container, final int position) {
            View view = mLayoutInflater.inflate(R.layout.recom_fragment, container, false);
            setupItem(view, LIBRARIES[position]);
            container.addView(view);
            return view;
        }

        @Override
        public boolean isViewFromObject(final View view, final Object object) {
            return view.equals(object);
        }

        @Override
        public void destroyItem(final ViewGroup container, final int position, final Object object) {
            container.removeView((View) object);
        }
    }
}
