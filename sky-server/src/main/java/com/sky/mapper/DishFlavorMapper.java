package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Mapper;

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

    int insertBatch(List<DishFlavor> flavors);
}
