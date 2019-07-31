package com.zben.eshop.cache.rebuild;


import com.zben.eshop.cache.model.ProductInfo;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @DESC: 重建缓存队列
 * @author: jhon.zhou
 * @date: 2019/7/29 0029 16:39
 */
public class RebuildCacheQueue {

    private ArrayBlockingQueue<ProductInfo> queues = new ArrayBlockingQueue<ProductInfo>(1000);

    public void putToQueue(ProductInfo t) {
        try {
            queues.put(t);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public ProductInfo take() {
        try {
            return queues.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 内部单例类
     * @author Administrator
     *
     */
    private static class Singleton {

        private static RebuildCacheQueue instance;

        static {
            instance = new RebuildCacheQueue();
        }

        public static RebuildCacheQueue getInstance() {
            return instance;
        }

    }

    public static RebuildCacheQueue getInstance() {
        return Singleton.getInstance();
    }

    public static void init() {
        getInstance();
    }
}
