package com.commonlibs.integration;

/**
 * Created by jess on 17/03/2017 11:15
 * Contact with jess.yan.effort@gmail.com
 */

public interface IRepositoryManager {

    /**
     * 注入RetrofitService
     * @param services
     */
    void setRetrofitService(Class<?>... services);


    /**
     * 注入CacheService
     * @param services
     */
    void setCacheService(Class<?>... services);


    /**
     * 根据传入的Class获取对应的Retrift service
     *
     * @param service
     * @param <T>
     * @return
     */
    <T> T obtainRetrofitService(Class<T> service);

    /**
     * 根据传入的Class获取对应的RxCache service
     *
     * @param cache
     * @param <T>
     * @return
     */
    <T> T obtainCacheService(Class<T> cache);

}
