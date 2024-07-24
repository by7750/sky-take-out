package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 1:47
 * @className DishFlavorMapper
 * @since 1.0
 */
@Mapper
public interface DishFlavorMapper {

    /**
     * 插入多条数据
     *
     * @param flavors
     * @return
     */
    int insertBatch(List<DishFlavor> flavors);

    /**
     * 根据dishId删除
     *
     * @param dishId
     * @return
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    int deleteByDishId(Long dishId);


    /**
     * 根据多个dishId删除
     *
     * @param dishIds
     * @return
     */
    int deleteByDishIds(List<Long> dishIds);

    /**
     * 根据菜品id查找口味
     *
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> selectByDishId(Long dishId);
}
