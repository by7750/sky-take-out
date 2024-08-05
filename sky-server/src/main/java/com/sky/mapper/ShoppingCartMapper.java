package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

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
    List<ShoppingCart> list(ShoppingCart shoppingCart);

    /**
     * 根据id更新数量和价格
     *
     * @param cart
     * @return
     */
    @Update("update shopping_cart set number = #{number}, amount = #{amount} where id = #{id}")
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
     * 根据用户ID查找购物车信息
     *
     * @param currentId
     * @return
     */
    @Select("select id, name, image, user_id, dish_id, setmeal_id, dish_flavor, number, amount, create_time from shopping_cart where user_id = #{c}")
    List<ShoppingCart> selectByUserId(Long currentId);

}
