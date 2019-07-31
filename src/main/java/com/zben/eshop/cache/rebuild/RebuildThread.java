package com.zben.eshop.cache.rebuild;

import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.service.CacheService;
import com.zben.eshop.cache.spring.SpringContext;
import com.zben.eshop.cache.zk.ZookeeperSession;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/7/29 0029 16:44
 */

public class RebuildThread implements Runnable {

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void run() {
        RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        CacheService cacheService = (CacheService) SpringContext.getApplicationContext()
                .getBean("cacheService");

        while(true) {
            ProductInfo productInfo = rebuildCacheQueue.take();

            zkSession.acquireDistributedLock(productInfo.getId());

            ProductInfo existedProductInfo = cacheService.getProductInfoFromRedis(productInfo.getId());
            System.out.println("======productInfo: " + productInfo);
            System.out.println("======existedProductInfo: " + existedProductInfo);
            if(existedProductInfo != null) {
                // 比较当前数据的时间版本比已有数据的时间版本是新还是旧
                try {
                    Date date = sdf.parse(productInfo.getModifyTime());
                    Date existedDate = sdf.parse(existedProductInfo.getModifyTime());

                    if(date.before(existedDate)) {
                        System.out.println("current date[" + productInfo.getModifyTime() + "] is before existed date[" + existedProductInfo.getModifyTime() + "]");
                        continue;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("current date[" + productInfo.getModifyTime() + "] is after existed date[" + existedProductInfo.getModifyTime() + "]");
            } else {
                System.out.println("existed product info is null......");
            }

            cacheService.saveProductInfo2LocalCache(productInfo);
            cacheService.saveProductInfo2RedisCache(productInfo);

            zkSession.releaseDistributedLock(productInfo.getId());
        }
    }
}
