package com.videobox.player.youtube;

/**
 * Created by liyanju on 2017/5/13.
 */

public interface IPlayItemUpdate {

    void onUpdatePlayListItmes(int index);

    void onUpdatePlayListItmes();

    void onUpdateItems(int postion);

    void onUpateAll();

    void onUpdateError();

}
