package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.SetmealVO;

/**
 * 套餐业务接口
 * @author yao
 * @version 1.0
 * @date 2024/7/25 - 1:09
 * @className SetmealService
 * @since 1.0
 */
public interface SetmealService {
    /**
     * 新增套餐业务
     * @param setmealDTO
     */
    void save(SetmealDTO setmealDTO);

    /**
     * 套餐分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
     * 根据id查询
     * @param id 套餐id
     * @return
     */

    SetmealVO getByIdWithDish(Long id);

    /**
     * 批量删除套餐
     * @param ids
     */
    void removeBatch(Long[] ids);
}
