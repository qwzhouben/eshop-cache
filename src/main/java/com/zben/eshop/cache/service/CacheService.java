package com.zben.eshop.cache.service;

import com.zben.eshop.cache.model.ProductInfo;
import com.zben.eshop.cache.model.ShopInfo;

/**
 * @DESC: ehcache service
 * @AUTHOR: jhon.zhou
 * @DATE: 2019/7/23 0023 17:47
 */
public interface CacheService {

    /**
     * 保存商品缓存
     * @param productInfo
     * @return
     */
    public ProductInfo saveProductInfo(ProductInfo productInfo);

    /**
     * 根据id获取商品缓存
     * @param id
     * @return
     */
    public ProductInfo findById(Integer id);

    /**
     * 保存商品信息到本地缓存
     * @param productInfo
     * @return
     */
    ProductInfo saveProductInfo2LocalCache(ProductInfo productInfo);

    /**
     * 保存商品信息到redis
     * @param productInfo
     */
    void saveProductInfo2RedisCache(ProductInfo productInfo);

    /**
     * 从本地缓存获取商品信息
     * @param productId
     * @return
     */
    ProductInfo getProductInfoFromLocalCache(Long productId);

    /**
     * 保存店铺信息到本地缓存
     * @param shopInfo
     * @return
     */
    ShopInfo saveShopInfo2LocalCache(ShopInfo shopInfo);

    /**
     * 保存店铺信息到redis
     * @param shopInfo
     */
    void saveShopInfo2RedisCache(ShopInfo shopInfo);

    /**
     * 从本地缓存获取店铺信息
     * @param shopId
     * @return
     */
    ShopInfo getShopInfoFromLocalCache(Long shopId);
}
