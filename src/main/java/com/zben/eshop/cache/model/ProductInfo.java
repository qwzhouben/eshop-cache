package com.zben.eshop.cache.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @DESC: 商品信息
 * @author: jhon.zhou
 * @date: 2019/7/23 0023 17:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInfo {

    private Integer id;
    private String name;
    private Double price;
}
