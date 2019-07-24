package com.zben.eshop.cache.model;

import lombok.Data;

/**
 * @DESC: 店铺信息
 * @author: jhon.zhou
 * @date: 2019/7/24 0024 10:38
 */
@Data
public class ShopInfo {

    private Long id;
    private String name;
    private Integer level;
    private Double goodCommentRate;

}
