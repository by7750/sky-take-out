package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/6 - 1:02
 * @className ShoppingCartMapper
 * @since 1.0
 */
@Mapper
public interface ShoppingCartMapper {
    /**
     * 动态条件查询
     *
     * @param shoppingCart
     * @return
     */
    List<ShoppingCart> selectList(ShoppingCart shoppingCart);

    /**
     * 根据id更新数量和价格
     *
     * @param cart
     * @return
     */
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    int updateNumberById(ShoppingCart cart);


    /**
     * 插入购物车数据
     *
     * @param shoppingCart
     * @return
     */
    @Insert("insert into shopping_cart(id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time) " +
            "values (#{id}, #{name}, #{image}, #{userId}, #{dishId}, #{setmealId}, #{dishFlavor}, #{number}, #{amount}, #{createTime})")
    int insert(ShoppingCart shoppingCart);

    /**
     * 根据用户ID删除购物车
     *
     * @param userId
     * @return
     */
    @Delete("delete from shopping_cart where user_id = #{userId}")
    int deleteByUserId(Long userId);

    /**
     * 批量插入
     * @param shoppingCartList
     * @return
     */
    int insertBatch(List<ShoppingCart> shoppingCartList);
}
