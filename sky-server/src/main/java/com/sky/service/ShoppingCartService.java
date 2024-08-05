package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/6 - 0:49
 * @className ShoppingCartService
 * @since 1.0
 */
public interface ShoppingCartService {

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    boolean addShoppingCart(ShoppingCartDTO shoppingCartDTO);

    /**
     * 查看购物车
     *
     * @return
     */
    List<ShoppingCart> getShoppingCart();
}
