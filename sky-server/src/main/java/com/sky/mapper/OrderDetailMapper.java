package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
     * 根据订单id查询订单明细
     *
     * @param orderId 订单id
     * @return
     */
    @Select("select  * from order_detail where order_id = #{orderId}")
    List<OrderDetail> selectByOrderId(Long orderId);
}
