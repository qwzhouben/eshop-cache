package com.zben.eshop.cache.controller;

import com.zben.eshop.cache.model.ProductInfo;
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
}
