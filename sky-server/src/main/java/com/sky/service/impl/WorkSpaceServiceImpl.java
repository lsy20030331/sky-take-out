package com.sky.service.impl;

import com.sky.entity.Dish;
import com.sky.entity.Orders;
import com.sky.mapper.DishMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.mapper.UserMapper;

import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class WorkSpaceServiceImpl implements WorkSpaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    /**
     * 调查今日订单数据
     * @return
     */
    @Override
    public BusinessDataVO getRunData() {
        LocalDateTime beginTime = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        LocalDateTime endtime = LocalDateTime.of(LocalDate.now(), LocalTime.MAX);
        Map map = new HashMap<>();

        map.put("begin", beginTime);
        map.put("end", endtime);
        map.put("status", Orders.COMPLETED);

        // 计算当日的营业额
        Double amount = orderMapper.sumByMap(map);
        if (amount == null){
            amount = 0.0;
        }
        // 计算有效订单的数量
        Integer validOrderCount = orderMapper.countByMap(map);
        // 计算订单完成率
        map.replace("status", null);
        Integer totalOrderCount = orderMapper.countByMap(map);
        Double orderCompletionRate = 0.0;
        if(totalOrderCount != 0){
            orderCompletionRate = (validOrderCount.doubleValue() / totalOrderCount);
        }
        // 计算平均客单价
        Double avg = 0.0;
        if(amount != 0){
            avg = (amount / validOrderCount);
        }
        // 计算新增用户数量
        Map map1 = new HashMap<>();
        map.put("begin", beginTime);
        map.put("end", endtime);
        Integer newUsers = userMapper.countByMap(map);
        return BusinessDataVO.builder()
                .turnover(amount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(avg)
                .newUsers(newUsers)
                .build();
    }

    /**
     * 调查订单管理数据
     * @return
     */
    @Override
    public OrderOverViewVO getOverViewOrders() {
        Integer waitingOrders = orderMapper.countStatus(Orders.TO_BE_CONFIRMED);
        Integer deliveredOrders = orderMapper.countStatus(Orders.DELIVERY_IN_PROGRESS);
        Integer completedOrders = orderMapper.countStatus(Orders.COMPLETED);
        Integer cancelledOrders = orderMapper.countStatus(Orders.CANCELLED);
        Map map = new HashMap<>();
        Integer allOrders = orderMapper.countByMap(map);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders)
                .deliveredOrders(deliveredOrders)
                .cancelledOrders(cancelledOrders)
                .completedOrders(completedOrders)
                .allOrders(allOrders)
                .build();
    }

    /**
     * 调查菜品总览
     * @return
     */
    @Override
    public DishOverViewVO getOverViewDishes() {
        Integer sold = dishMapper.countByStatus(1);
        Integer discontinued = dishMapper.countByStatus(0);

        return DishOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }

    /**
     * 调查套餐总览
     * @return
     */
    @Override
    public SetmealOverViewVO getOverViewSetmeals() {
        Integer sold = setmealMapper.countByStatus(1);
        Integer discontinued = setmealMapper.countByStatus(0);

        return SetmealOverViewVO.builder()
                .sold(sold)
                .discontinued(discontinued)
                .build();
    }
}
