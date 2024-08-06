package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/7 - 1:20
 * @className OrderService
 * @since 1.0
 */
public interface OrderService {

    /**
     * 提交订单业务
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);

}
