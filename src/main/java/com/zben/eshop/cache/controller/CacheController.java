package com.zben.eshop.cache.controller;

import com.alibaba.fastjson.JSONObject;
import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.model.ShopInfo;
import com.zben.eshop.cache.rebuild.RebuildCacheQueue;
import com.zben.eshop.cache.service.CacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @DESC: 缓存controller
 * @author: jhon.zhou
 * @date: 2019/7/23 0023 17:54
 */
@RestController
public class CacheController {

    @Autowired
    private CacheService cacheService;

    @GetMapping("/saveCache")
    public ProductInfo saveCache(ProductInfo productInfo) {
        return cacheService.saveProductInfo(productInfo);
    }

    @GetMapping("/findById/{id}")
    public ProductInfo findById(@PathVariable Integer id) {
        return cacheService.findById(id);
    }

    /**
     * 获取商品信息
     * @param productId
     * @return
     */
    @GetMapping("/getProductInfo")
    public ProductInfo getProductInfo(Long productId) {
        ProductInfo productInfo = null;

        productInfo = cacheService.getProductInfoFromRedis(productId);
        System.out.println("=================从redis中获取缓存，商品信息=" + productInfo);

        if(productInfo == null) {
            productInfo = cacheService.getProductInfoFromLocalCache(productId);
            System.out.println("=================从ehcache中获取缓存，商品信息=" + productInfo);
        }

        if(productInfo == null) {
            // 就需要从数据源重新拉去数据，重建缓存，但是这里先不讲
            String productInfoJSON = "{\"id\": 16, \"name\": \"iphone7手机\", \"price\": 5599, \"pictureList\":\"a.jpg,b.jpg\", \"specification\": \"iphone7的规格\", \"service\": \"iphone7的售后服务\", \"color\": \"红色,白色,黑色\", \"size\": \"5.5\", \"shopId\": 1, \"modifyTime\": \"2019-07-29 16:31:00\"}";
            productInfo = JSONObject.parseObject(productInfoJSON, ProductInfo.class);
            // 将数据推送到一个内存队列中
            RebuildCacheQueue rebuildCacheQueue = RebuildCacheQueue.getInstance();
            rebuildCacheQueue.putToQueue(productInfo);
        }

        return productInfo;
    }

    /**
     * 获取店铺信息
     * @param shopId
     * @return
     */
    @GetMapping("/getShopInfo")
    public ShopInfo getShopInfo(Long shopId) {
        //从redis中获取店铺信息
        ShopInfo shopInfo = cacheService.getShopInfoFromRedis(shopId);
        System.out.println("========从redis中获取的店铺："+shopInfo);
        if (shopInfo == null) {
            //从本地缓存获取店铺信息
            shopInfo = cacheService.getShopInfoFromLocalCache(shopId);
            System.out.println("========从本地缓存中获取的店铺："+shopInfo);
        }
        return shopInfo;
    }
}
