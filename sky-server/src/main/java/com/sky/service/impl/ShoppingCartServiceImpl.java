package com.sky.service.impl;

import com.sky.context.BaseContext;
import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.ShoppingCart;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/6 - 0:51
 * @className ShoppingCartServiceImpl
 * @since 1.0
 */
@Service
@Slf4j
public class ShoppingCartServiceImpl implements ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 添加购物车
     *
     * @param shoppingCartDTO
     * @return
     */
    @Override
    public boolean addShoppingCart(ShoppingCartDTO shoppingCartDTO) {

        Long currentId = BaseContext.getCurrentId();

        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(currentId);

        // 判断当前加入到购物车中的数据是否已经存在
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);


        if (list != null && list.size() > 0) {
            // 存在则修改
            ShoppingCart cart = list.get(0);
            Integer number = cart.getNumber();
            cart.setNumber(number + 1);
            cart.setAmount(cart.getAmount().divide(BigDecimal.valueOf(number)).multiply(BigDecimal.valueOf(cart.getNumber())));
            int i = shoppingCartMapper.updateNumberById(cart);
        } else {
            // 不存在则插入
            Long dishId = shoppingCart.getDishId();
            Long setmealId = shoppingCartDTO.getSetmealId();

            if (null == dishId) {
                Setmeal setmeal = setmealMapper.selectById(setmealId);
                shoppingCart.setSetmealId(setmeal.getId());
                shoppingCart.setName(setmeal.getName());
                shoppingCart.setAmount(setmeal.getPrice());
            } else {
                Dish dish = dishMapper.selectById(dishId);
                shoppingCart.setDishId(dish.getId());
                shoppingCart.setName(dish.getName());
                shoppingCart.setAmount(dish.getPrice());
            }
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCart.setNumber(1);
            int i = shoppingCartMapper.insert(shoppingCart);
        }


        return false;
    }
}
