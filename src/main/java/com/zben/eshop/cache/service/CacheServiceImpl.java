package com.zben.eshop.cache.service;

import com.zben.eshop.cache.model.ProductInfo;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @DESC: ehcache service 实现
 * @author: jhon.zhou
 * @date: 2019/7/23 0023 17:50
 */
@Service
public class CacheServiceImpl implements CacheService {

    private static final String CACHE_NAME = "local";

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
}
