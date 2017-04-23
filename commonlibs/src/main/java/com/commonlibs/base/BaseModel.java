package com.commonlibs.base;

import com.commonlibs.integration.IRepositoryManager;
import com.commonlibs.themvp.model.IModel;


/**
 * Created by jess on 8/5/16 12:55
 * contact with jess.yan.effort@gmail.com
 */
public class BaseModel implements IModel {
    protected IRepositoryManager mRepositoryManager;//用于管理网络请求层,以及数据缓存层

    public BaseModel(IRepositoryManager repositoryManager) {
        this.mRepositoryManager = repositoryManager;
    }

    @Override
    public void onDestroy() {
        mRepositoryManager = null;
    }
}
