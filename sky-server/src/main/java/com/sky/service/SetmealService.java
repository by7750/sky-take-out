package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

/**
 * 套餐业务接口
 *
 * @author yao
 * @version 1.0
 * @date 2024/7/25 - 1:09
 * @className SetmealService
 * @since 1.0
 */
public interface SetmealService {
    /**
     * 新增套餐业务
     *
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     *
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询
     *
     * @param id 套餐id
     * @return
     */

    SetmealVO getByIdWithDish(Long id);

    /**
     * 批量删除套餐
     *
     * @param ids
     */
    void removeBatch(Long[] ids);

    /**
     * 修改套餐
     *
     * @param setmealDTO
     */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
     * 修改套餐状态
     *
     * @param status 状态码，1为上架，0为下架
     * @param id     套餐id
     */
    void changeStatus(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);


    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
