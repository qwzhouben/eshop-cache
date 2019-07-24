package com.zben.eshop.cache.service;

import com.alibaba.fastjson.JSON;
import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.model.ShopInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisCluster;

/**
 * @DESC: ehcache service 实现
 * @author: jhon.zhou
 * @date: 2019/7/23 0023 17:50
 */
@Service("cacheService")
public class CacheServiceImpl implements CacheService {

    private static final String CACHE_NAME = "local";
    private static final String PRODUCT_KEY = "product_info_";
    private static final String SHOP_KEY = "shop_info_";

    @Autowired
    private JedisCluster jedisCluster;

    @Override
    @CachePut(value = CACHE_NAME, key = "'key_' + #productInfo.getId()")
    public ProductInfo saveProductInfo(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'key_' + #id")
    public ProductInfo findById(Integer id) {
        return null;
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'product_info_' + #productInfo.getId()")
    public ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo) {
        return productInfo;
    }

    @Override
    public void saveProductInfo2RedisCache(ProductInfo productInfo) {
        jedisCluster.set(PRODUCT_KEY + productInfo.getId(), JSON.toJSONString(productInfo));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'product_info_' + #productId")
    public ProductInfo getProductInfoFromLocalCache(Long productId) {
        return null;
    }

    @Override
    @CachePut(value = CACHE_NAME, key = "'shop_info_' + #shopInfo.getId()")
    public ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo) {
        return shopInfo;
    }

    @Override
    public void saveShopInfo2RedisCache(ShopInfo shopInfo) {
        jedisCluster.set(SHOP_KEY + shopInfo.getId(), JSON.toJSONString(shopInfo));
    }

    @Override
    @Cacheable(value = CACHE_NAME, key = "'shop_info_' + #shopId")
    public ShopInfo getShopInfoFromLocalCache(Long shopId) {
        return null;
    }
}
