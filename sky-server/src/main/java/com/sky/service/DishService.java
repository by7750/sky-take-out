package com.sky.service;

import com.sky.dto.DishDTO;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/24 - 1:28
 * @className DishService
 * @since 1.0
 */
public interface DishService {

    /**
     * 新增菜品和对应口味数据
     * @param dishDTO
     */
    void saveWithFlavor(DishDTO dishDTO);
}
