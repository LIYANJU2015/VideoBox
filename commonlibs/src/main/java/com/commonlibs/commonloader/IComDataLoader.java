package com.commonlibs.commonloader;

/**
 * Created by liyanju on 16/8/17.
 *
 * 通用的异步数据加载的接口 接口必须实现
 *
 */
public interface IComDataLoader<T>{

    public void setLoadSuccess();

    public boolean isLoadSuccess();

    /**
     * 返回唯一的id值
     * @return
     */
    public String getId();

    /**
     * 具体处理加载任务
     * @param listener 根据实际需要可传null
     * @return
     */
    T onHandleSelfData(ComDataLoadTask.AsyncHandleListener listener);

    /**
     * 设置加载出来的数据
     * @param t
     */
    void setLoadDataObj(T t);

    /**
     * 获取加载出来的数据
     * @param t
     */
    T getLoadDataObj();
}
