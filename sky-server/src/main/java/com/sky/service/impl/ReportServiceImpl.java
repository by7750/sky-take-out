package com.sky.service.impl;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yao
 * @version 1.0
 * @date 2024/10/17 - 14:42
 * @className ReportServiceImpl
 * @since 1.0
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    /**
     * 统计指定时间区间营业额数据
     *
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        Map<String, Object> condition = new HashMap<>();
        condition.put("begin", begin);
        end = end.plusDays(1);
        condition.put("end", end);
        condition.put("status", Orders.COMPLETED);
        Map<LocalDate, Map<String, Object>> turnover = orderMapper.selectTurnover(condition);

        // 存放从begin到end每天的日期列表
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.equals(end)) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        List<BigDecimal> amountList = new ArrayList<>();

        if (turnover != null) {
            for (LocalDate date : dateList) {
                // 遍历日期，从map中取出对应数据，若为null则存入0
                Map<String, Object> map = turnover.get(date);
                BigDecimal amount = map == null ?
                        BigDecimal.valueOf(0) :
                        (BigDecimal) map.get("turnover");
                amountList.add(amount);
            }
        }

        return TurnoverReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .turnoverList(StringUtils.join(amountList, ","))
                .build();
    }


    /**
     * 用户统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return
     */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        // 存放每天日期
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.equals(end.plusDays(1))) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }

        // 存放每天的新增用户数量
        List<Integer> newUserList = new ArrayList<>();
        // 存放每天的总用户数量
        List<Integer> countUserList = new ArrayList<>();
        for (LocalDate date : dateList) {
            Map<String, LocalDate> params = new HashMap<>();
            // 查总数
            params.put("end", date.plusDays(1));
            int total = userMapper.selectCountByMap(params);
            // 查新增
            params.put("start", date);
            int newNum = userMapper.selectCountByMap(params);
            countUserList.add(total);
            newUserList.add(newNum);
        }

        return UserReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .newUserList(StringUtils.join(newUserList, ","))
                .totalUserList(StringUtils.join(countUserList, ","))
                .build();
    }


    /**
     * 订单统计
     *
     * @param begin 开始时间
     * @param end   结束时间
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        // 日期列表
        List<LocalDate> dateList = new ArrayList<>();
        while (!begin.equals(end.plusDays(1))) {
            dateList.add(begin);
            begin = begin.plusDays(1);
        }
        // 用于存放订单数量的列表
        List<Integer> totalOrderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        // 查出每一天的订单情况
        for (LocalDate localDate : dateList) {
            Map<String, Object> map = new HashMap<>();
            map.put("begin", localDate);
            map.put("end", localDate.plusDays(1));
            int total = orderMapper.selectCountByCondition(map); // 查总订单数
            totalOrderCountList.add(total);
            map.put("status", Orders.COMPLETED);
            int valid = orderMapper.selectCountByCondition(map); // 有效订单数
            validOrderCountList.add(valid);
        }

        // 对订单做统计
        int count = totalOrderCountList.stream().reduce(0, (a, b) -> a + b);
        int valid = validOrderCountList.stream().reduce(0, Integer::sum);
        double rate = valid * 1.0 / count;
        // 封装VO返回
        return OrderReportVO
                .builder()
                .dateList(StringUtils.join(dateList, ","))
                .orderCountList(StringUtils.join(totalOrderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(count)
                .validOrderCount(valid)
                .orderCompletionRate(rate)
                .build();
    }
}
