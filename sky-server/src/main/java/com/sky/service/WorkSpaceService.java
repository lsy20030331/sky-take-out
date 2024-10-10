package com.sky.service;

import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;

public interface WorkSpaceService {

    /**
     * 调查今日运营数据
     * @return
     */
    BusinessDataVO getRunData();

    /**
     * 调查订单管理数据
     * @return
     */
    OrderOverViewVO getOverViewOrders();

    /**
     * 调查菜品总览
     * @return
     */
    DishOverViewVO getOverViewDishes();

    /**
     * 调查套餐总览
     * @return
     */
    SetmealOverViewVO getOverViewSetmeals();
}
