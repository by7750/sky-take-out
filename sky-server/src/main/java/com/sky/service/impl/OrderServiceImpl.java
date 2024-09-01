package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.BaseException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;

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

    /**
     * 订单支付
     *
     * @param ordersPaymentDTO
     * @return
     */
    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
        // 当前登录用户id
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);

        //调用微信支付接口，生成预支付交易单
        JSONObject jsonObject = weChatPayUtil.pay(
                ordersPaymentDTO.getOrderNumber(), //商户订单号
                new BigDecimal(0.01), //支付金额，单位 元
                "苍穹外卖订单", //商品描述
                user.getOpenid() //微信用户的openid
        );

        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
            throw new OrderBusinessException("该订单已支付");
        }

        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
        vo.setPackageStr(jsonObject.getString("package"));

        return vo;
    }

    /**
     * 支付成功，修改订单状态
     *
     * @param outTradeNo
     */
    public void paySuccess(String outTradeNo) {

        // 根据订单号查询订单
        Orders ordersDB = orderMapper.getByNumber(outTradeNo);

        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
        Orders orders = Orders.builder()
                .id(ordersDB.getId())
                .status(Orders.TO_BE_CONFIRMED)
                .payStatus(Orders.PAID)
                .checkoutTime(LocalDateTime.now())
                .build();

        orderMapper.update(orders);
    }

    /**
     * 用户端订单分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param status
     * @return
     */
    @Override
    public PageResult pageQuery4User(int pageNum, int pageSize, Integer status) {
        PageHelper.startPage(pageNum, pageSize);

        OrdersPageQueryDTO ordersPageQueryDTO = new OrdersPageQueryDTO();
        ordersPageQueryDTO.setUserId(BaseContext.getCurrentId());
        ordersPageQueryDTO.setStatus(status);

        List<Orders> list = orderMapper.pageQuery(ordersPageQueryDTO);
        PageInfo<Orders> pageInfo = new PageInfo<>(list);

        List<OrderVO> orderVOS = new ArrayList();
        if (pageInfo.getList() != null && pageInfo.getTotal() > 0) {
            for (Orders orders : pageInfo.getList()) {
                // 查询订单明细
                Long orderId = orders.getId();
                List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(orderId);

                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);

                orderVOS.add(orderVO);
            }
        }


        return new PageResult(pageInfo.getTotal(), orderVOS);
    }

    /**
     * 查询订单详情
     *
     * @param id
     * @return
     */
    @Override
    public OrderVO details(Long id) {
        Orders orders = orderMapper.selectById(id);
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(id);
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(orders, orderVO);
        orderVO.setOrderDetailList(orderDetails);
        return orderVO;
    }

    /**
     * 用户取消订单
     *
     * @param id
     */
    @Override
    public void userCancelById(Long id) throws Exception {
        Orders orders = orderMapper.selectById(id);
        // 校验是否存在
        if (orders == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 校验是否已经接单
        if (orders.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        // 已支付要退款
        if (orders.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
//            weChatPayUtil.refund(
//                    orders.getNumber(), //商户订单号
//                    orders.getNumber(), //商户退款单号
//                    new BigDecimal(0.01),//退款金额，单位 元
//                    new BigDecimal(0.01));//原订单金额

            //支付状态修改为 退款
//            orders.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }


    /**
     * 再来一单
     *
     * @param id
     */
    @Override
    public void repetition(Long id) {
        Long userId = BaseContext.getCurrentId();

        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(id);
        // 将商品详情对象转化为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetails.stream().map(e -> {
            ShoppingCart shoppingCart = ShoppingCart.builder()
                    .userId(userId)
                    .createTime(LocalDateTime.now())
                    .build();
            BeanUtils.copyProperties(e, shoppingCart);
            return shoppingCart;
        }).collect(Collectors.toList());

        int i = shoppingCartMapper.insertBatch(shoppingCartList);
        if (i != shoppingCartList.size()) {
            throw new BaseException(MessageConstant.UNKNOWN_ERROR);
        }
    }

    /**
     * 管理端分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @Override
    public PageResult conditionSearch(OrdersPageQueryDTO ordersPageQueryDTO) {
        PageHelper.startPage(ordersPageQueryDTO.getPage(), ordersPageQueryDTO.getPageSize());
        Page<Orders> orders = orderMapper.pageQuery(ordersPageQueryDTO);
        List<OrderVO> orderVOS = getOrderVOList(orders);
        return new PageResult(orders.getTotal(), orderVOS);
    }

    /**
     * 统计数据
     * @return
     */
    @Override
    public OrderStatisticsVO statistics() {
        // 根据状态，分别查询出待接单、待派送、派送中的订单数量
        Integer toBeConfirmed = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer confirmed = orderMapper.countStatus(Orders.CONFIRMED);
        Integer deliveryInProgress = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);

        // 将查询出的数据封装到orderStatisticsVO中响应
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(toBeConfirmed);
        orderStatisticsVO.setConfirmed(confirmed);
        orderStatisticsVO.setDeliveryInProgress(deliveryInProgress);
        return orderStatisticsVO;
    }

    /**
     * @param orders
     * @return
     */
    private List<OrderVO> getOrderVOList(Page<Orders> orders) {
        List<OrderVO> ret = new ArrayList<>();
        if(!CollectionUtils.isEmpty(orders)){
            // 需要返回订单菜品信息，自定义OrderVO响应结果
            ret = orders.getResult().stream().map(e -> {
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(e, orderVO);
                // 将订单菜品信息封装到orderVO中
                orderVO.setOrderDishes(getOrderDishesStr(e));
                return orderVO;
            }).collect(Collectors.toList());

        }
        return ret;
    }

    /**
     * 根据订单id获取菜品信息字符串
     *
     * @param orders
     * @return
     */
    private String getOrderDishesStr(Orders orders) {
        // 查询订单菜品详情信息（订单中的菜品和数量）
        List<OrderDetail> orderDetails = orderDetailMapper.selectByOrderId(orders.getId());
        // 将每一条订单菜品信息拼接为字符串（格式：宫保鸡丁*3；）
        List<String> list = orderDetails.stream().map(
                e -> e.getName() + "*" + e.getNumber() + ";"
        ).collect(Collectors.toList());
        // 将该订单对应的所有菜品信息拼接在一起
        return String.join("", list);
    }

}
