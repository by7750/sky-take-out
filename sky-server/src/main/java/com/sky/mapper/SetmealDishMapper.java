package com.sky.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 22:31
 * @className SetmealDishMapper
 * @since 1.0
 */
@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品id查套餐id
     * @param dishIds
     * @return
     */
//    @Select("select setmeal_id from setmeal_dish where id in #{}")
    List<Long>  getSetmealIdsByDishIds(List<Long> dishIds);
}
