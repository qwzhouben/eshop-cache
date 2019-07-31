package com.zben.eshop.cache.kafka;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.model.ShopInfo;
import com.zben.eshop.cache.service.CacheService;
import com.zben.eshop.cache.zk.ZookeeperSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * @date
 * @author
 */
@Component
public class KafkaConsumerService {

    @Autowired
    CacheService cacheService;
    private static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @KafkaListener(topics = "eshop-cache-message")
    public void processMessage(String content) {
        System.out.println("消息被消费"+content);
        JSONObject messageJSONObject = JSON.parseObject(content);

        // 从这里提取出消息对应的服务的标识
        String serviceId = messageJSONObject.getString("serviceId");

        // 如果是商品信息服务
        if("productInfoService".equals(serviceId)) {
            processProductInfoChangeMessage(messageJSONObject);
        } else if("shopInfoService".equals(serviceId)) {
            processShopInfoChangeMessage(messageJSONObject);
        }
    }


    /**
     * 处理商品信息变更的消息
     * @param messageJSONObject
     */
    private void processProductInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long productId = messageJSONObject.getLong("productId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        String productInfoJSON = "{\"id\": 16, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifyTime\":\"2019-07-29 16:30:00\"}";
        ProductInfo productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
        //将数据写到缓存之前，先要获取zk的锁
        ZookeeperSession zkSession = ZookeeperSession.getInstance();
        zkSession.acquireDistributedLock(productId);
        //获取到了锁, 从redis中读取数据
        ProductInfo existProductInfo = cacheService.getProductInfoFromRedis(productId);
        if (existProductInfo != null) {
            try {
                // 比较当前数据的时间版本比已有数据的时间版本是新还是旧
                if (df.parse(productInfo.getModifyTime()).before(df.parse(existProductInfo.getModifyTime()))) {
                    System.out.println("current date[" + productInfo.getModifyTime() + "] is before existed date[" + existProductInfo.getModifyTime() + "]");
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("current date[" + productInfo.getModifyTime() + "] is after existed date[" + existProductInfo.getModifyTime() + "]");
        } else {
            System.out.println("existed product info is null......");
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        cacheService.saveProductInfo2LocalCache(productInfo);
        System.out.println("===================获取刚保存到本地缓存的商品信息：" + cacheService.getProductInfoFromLocalCache(productId));
        cacheService.saveProductInfo2RedisCache(productInfo);
        zkSession.releaseDistributedLock(productId);
    }

    /**
     * 处理店铺信息变更的消息
     * @param messageJSONObject
     */
    private void processShopInfoChangeMessage(JSONObject messageJSONObject) {
        // 提取出商品id
        Long shopId = messageJSONObject.getLong("shopId");

        // 调用商品信息服务的接口
        // 直接用注释模拟：getProductInfo?productId=1，传递过去
        // 商品信息服务，一般来说就会去查询数据库，去获取productId=1的商品信息，然后返回回来

        String shopInfoJSON = "{\"id\": 1, \"name\": \"小王的手机店\", \"level\": 5, \"goodCommentRate\":0.99}";
        ShopInfo shopInfo = JSONObject.parseObject(shopInfoJSON, ShopInfo.class);
        cacheService.saveShopInfo2LocalCache(shopInfo);
        System.out.println("===================获取刚保存到本地缓存的店铺信息：" + cacheService.getShopInfoFromLocalCache(shopId));
        cacheService.saveShopInfo2RedisCache(shopInfo);
    }
}
