package com.zben.eshop.cache.prewarm;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.service.CacheService;
import com.zben.eshop.cache.spring.SpringContext;
import com.zben.eshop.cache.zk.ZookeeperSession;
import org.springframework.stereotype.Component;

/**
 * @DESC:
 * @author: jhon.zhou
 * @date: 2019/8/7 0007 10:46
 */
@Component
public class ProductCachePreWarmThread extends Thread {

    /**
     * 1. 服务启动的时候，进行缓存预热
     * 2. 从zk中读取taskid列表
     * 3. 依次遍历每个taskid，尝试获取分布式锁，如果获取不到，快速报错不要等待，因为说明已经有其他服务实例在预热
     * 4. 直接尝试获取下一个taskid的分布式锁
     * 5. 即使获取到了分布式锁，也要检查一下这个taskid的预热状态，如果已经预热过了，就不要再预热
     * 6. 执行了预热操作，遍历productId列表，查询数据，然后写ehcache和redis
     * 7. 预热完成后，设置taskid对应的预热状态
     */
    @Override
    public void run() {
        CacheService cacheService = (CacheService) SpringContext.
                getApplicationContext().getBean("cacheService");
        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        String taskidList = zkSession.getNodeData("/taskid-list");

        System.out.println("【CachePrewarmThread获取到taskid列表】taskidList=" + taskidList);

        if(taskidList != null && !"".equals(taskidList)) {
            String[] taskidListSplited = taskidList.split(",");
            for(String taskid : taskidListSplited) {
                String taskidLockPath = "/taskid-lock-" + taskid;

                boolean result = zkSession.acquireFastFailDistributedLock(taskidLockPath);
                if(!result) {
                    continue;
                }

                String taskidStatusLockPath = "/taskid-status-lock-" + taskid;
                zkSession.acquireDistributedLock(taskidStatusLockPath);

                String taskidStatus = zkSession.getNodeData("/taskid-status-" + taskid);
                System.out.println("【CachePrewarmThread获取task的预热状态】taskid=" + taskid + ", status=" + taskidStatus);

                if("".equals(taskidStatus)) {
                    String productidList = zkSession.getNodeData("/task-hot-product-list-" + taskid);
                    System.out.println("【CachePrewarmThread获取到task的热门商品列表】productidList=" + productidList);
                    JSONArray productidJSONArray = JSONArray.parseArray(productidList);

                    for(int i = 0; i < productidJSONArray.size(); i++) {
                        Long productId = productidJSONArray.getLong(i);
                        String productInfoJSON = "{\"id\": " + productId + ", \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifiedTime\": \"2017-01-01 12:00:00\"}";
                        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
                        cacheService.saveProductInfo2LocalCache(productInfo);
                        System.out.println("【CachePrwarmThread将商品数据设置到本地缓存中】productInfo=" + productInfo);
                        cacheService.saveProductInfo2RedisCache(productInfo);
                        System.out.println("【CachePrwarmThread将商品数据设置到redis缓存中】productInfo=" + productInfo);
                    }

                    zkSession.createNode("/taskid-status-" + taskid);
                    zkSession.setNodeData("/taskid-status-" + taskid, "success");
                }

                zkSession.releaseDistributedLock(taskidStatusLockPath);

                zkSession.releaseDistributedLock(taskidLockPath);
            }
        }
    }
}
