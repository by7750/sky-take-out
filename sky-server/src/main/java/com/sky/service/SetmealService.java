package com.sky.service;

import com.sky.dto.SetmealDTO;

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
}
