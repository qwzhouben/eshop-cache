package com.zben.eshop.cache.service;

import com.zben.eshop.cache.model.ProductInfo;

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
}
