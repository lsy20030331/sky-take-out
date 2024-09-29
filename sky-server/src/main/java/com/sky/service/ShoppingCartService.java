package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {

    /**
     * 购物车新增商品的相关操作
     * @param shoppingCartDTO
     * @return
     */
    void add(ShoppingCartDTO shoppingCartDTO);

    /**
     * 调查商品操作
     * @return
     */
    List<ShoppingCart> query();

    /**
     * 清空购物车
     */
    void cleanShoppingCart();

    /**
     * 删除单个商品
     * @param shoppingCartDTO
     */
    void cleanOneItem(ShoppingCartDTO shoppingCartDTO);
}
