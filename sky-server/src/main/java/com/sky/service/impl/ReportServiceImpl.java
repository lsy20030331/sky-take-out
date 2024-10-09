package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;
    /**
     * 营业额统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 创建list数组用来存放日期区间的日期集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);
        while (!begin.equals(end)){
            // 日期计算计算指定日期的后一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        List<Double> amount = new ArrayList<>();

        for (LocalDate date : dateList) {
            // 查询date日期对应的营业额(状态为已完成的订单金额合计)

            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);

            // select sum(amount) from orders where order_time > ? and order_time < ? and status = 5
            Map map = new HashMap<>();
            map.put("begin", begintime);
            map.put("end", endtime);
            map.put("status", Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            amount.add(turnover);
        }

        // 将list集合转化为字符串
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .turnoverList(StringUtils.join(amount, ","))
                .build();
    }

    /**
     * 用户统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public UserReportVO getuserStatistics(LocalDate begin, LocalDate end) {
        // 创建list数组用来存放日期区间的日期集合
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            // 日期计算计算指定日期的后一天
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 统计新用户的总数量
        List<Integer> newUserList = new ArrayList<>();
        // 统计用户的总数量
        List<Integer> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList){
            // 一天中最后的时间
            LocalDateTime begintime = LocalDateTime.of(date,LocalTime.MIN);
            // 一天中起始的时间
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);

            Map map = new HashMap<>();

            // 先调查用户的总数量
            map.put("end", endtime);
            Integer totalUser = userMapper.countByMap(map);

            // 再调查新增的用户数量
            map.put("begin", begintime);
            Integer newUser = userMapper.countByMap(map);

            totalUserList.add(totalUser);
            newUserList.add(newUser);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .totalUserList(StringUtils.join(totalUserList,","))
                .newUserList(StringUtils.join(newUserList,","))
                .build();
    }

    /**
     * 订单统计
     * @param begin
     * @param end
     * @return
     */
    @Override
    public OrderReportVO getOrderStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);
            dateList.add(begin);
        }

        // 每日订单数
        List<Integer> orderCountList = new ArrayList<>();
        // 每日订单有效数
        List<Integer> validOrderCountList = new ArrayList<>();

        for (LocalDate date : dateList){
            LocalDateTime begintime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endtime = LocalDateTime.of(date, LocalTime.MAX);

            // 先调查每日总订单数
            Integer countList = getOrderCount(begintime, endtime, null);
            orderCountList.add(countList);

            // 再调查每日有效订单数
            Integer validCountList = getOrderCount(begintime, endtime, Orders.COMPLETED);
            validOrderCountList.add(validCountList);
        }

        // 计算区间的订单总数量
        Integer totalOrderCount = orderCountList.stream().reduce(Integer::sum).get();
        // 计算区间的订单有效数量
        Integer validOrderCount = validOrderCountList.stream().reduce(Integer::sum).get();

        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount);
        }
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,","))
                .orderCountList(StringUtils.join(orderCountList, ","))
                .validOrderCountList(StringUtils.join(validOrderCountList, ","))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }

    @Override
    public SalesTop10ReportVO getsalesTopStatistics(LocalDate begin, LocalDate end) {

        LocalDateTime begintime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(end, LocalTime.MAX);

        List<GoodsSalesDTO> salesTop10 = orderMapper.getSalesTop(begintime, endtime);

        List<String> names = salesTop10.stream().map(GoodsSalesDTO::getName).collect(Collectors.toList());
        String nameList = StringUtils.join(names, ",");

        List<Integer> numbers = salesTop10.stream().map(GoodsSalesDTO::getNumber).collect(Collectors.toList());
        String numberList = StringUtils.join(numbers, ",");

        return SalesTop10ReportVO.builder()
                .nameList(nameList)
                .numberList(numberList)
                .build();
    }

    private Integer getOrderCount(LocalDateTime begin, LocalDateTime end, Integer status){
        Map map = new HashMap<>();
        map.put("begin", begin);
        map.put("end", end);
        map.put("status", status);

        return orderMapper.countByMap(map);
    }
}
