package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author yao
 * @version 1.0
 * @date 2024/8/7 - 1:22
 * @className OrderServiceImpl
 * @since 1.0
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    /**
     * 提交订单
     *
     * @param ordersSubmitDTO
     * @return
     */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {

        // 处理各种业务异常（地址簿为空，购物车为空，注入对应的mapper）
        AddressBook addressBook = addressBookMapper.selectById(ordersSubmitDTO.getAddressBookId());
        if (addressBook == null) {
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        Long currentUser = BaseContext.getCurrentId();
        List<ShoppingCart> list = shoppingCartMapper.selectList(ShoppingCart.builder().userId(currentUser).build());
        if (list == null || list.size() == 0) {
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        // 向订单表插入订单数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID); // 支付状态 未支付
        orders.setStatus(Orders.PENDING_PAYMENT); // 订单状态：待支付
        orders.setNumber(String.valueOf(System.currentTimeMillis())); // 订单编号
        orders.setPhone(addressBook.getPhone()); // 联系电话
        orders.setConsignee(addressBook.getConsignee()); // 收货人
        orders.setUserId(currentUser);

        int i = orderMapper.insert(orders);

        List<OrderDetail> orderDetailList = new ArrayList<>();

        // 向订单明细插入n条数据
        for (ShoppingCart cart : list) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);
            orderDetail.setOrderId(orders.getId());
            orderDetailList.add(orderDetail);
        }
        int cnt = orderDetailMapper.insertBatch(orderDetailList);
        // 清空购物车数据
        shoppingCartMapper.deleteByUserId(currentUser);
        // 封装VO放回结果
        return OrderSubmitVO.builder()
                .id(orders.getId())
                .orderTime(orders.getOrderTime())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .build();
    }
}
