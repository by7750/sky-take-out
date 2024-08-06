package com.sky.mapper;

import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/7 - 1:23
 * @className OrderMapper
 * @since 1.0
 */
@Mapper
public interface OrderMapper {


    /**
     * 插入订单
     * @param orders
     * @return
     */
    int insert(Orders orders);
}
