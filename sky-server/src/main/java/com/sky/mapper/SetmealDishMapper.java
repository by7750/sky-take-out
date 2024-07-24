package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    /**
     * 插入一条
     * @param setmealDish
     * @return
     */
    @Insert("insert into setmeal_dish( setmeal_id, dish_id, name, price, copies) " +
            "values (#{setmealId},#{dishId},#{name},#{price},#{copies})")
    int insert(SetmealDish setmealDish);

    /**
     * 批量插入
     * @param setmealDishes
     * @return
     */
    int insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id查询套餐菜品
     * @param id 套餐id
     * @return
     */
    @Select("select id, setmeal_id, dish_id, name, price, copies from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> selectBySetmealId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    int deleteBySetmealId(Long id);
}
