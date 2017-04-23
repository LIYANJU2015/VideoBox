package com.commonlibs.widget.imageloader;

import android.content.Context;


/**
 * Created by jess on 8/5/16 15:57
 * contact with jess.yan.effort@gmail.com
 */
public final class ImageLoader {
    private BaseImageLoaderStrategy mStrategy;

    public ImageLoader(BaseImageLoaderStrategy strategy) {
        setLoadImgStrategy(strategy);
    }

    public <T extends ImageConfig> void loadImage(Context context, T config) {
        this.mStrategy.loadImage(context, config);
    }

    public <T extends ImageConfig> void clear(Context context, T config) {
        this.mStrategy.clear(context, config);
    }


    public void setLoadImgStrategy(BaseImageLoaderStrategy strategy) {
        this.mStrategy = strategy;
    }

}
