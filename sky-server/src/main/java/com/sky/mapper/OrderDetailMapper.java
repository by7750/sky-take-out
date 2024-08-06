package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/7 - 1:24
 * @className OrderDetailMapper
 * @since 1.0
 */
@Mapper
public interface OrderDetailMapper {
    /**
     * 批量插入订单明细
     *
     * @param orderDetailList
     * @return
     */
    int insertBatch(List<OrderDetail> orderDetailList);

}
