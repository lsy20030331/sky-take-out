package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderPaymentVO;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

public interface OrderService {

    /**
     * 用户下单方法
     * @param ordersSubmitDTO
     * @return
     */
    OrderSubmitVO  submitOrder(OrdersSubmitDTO ordersSubmitDTO);

    /**
     * 订单支付
     * @param ordersPaymentDTO
     * @return
     */
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;

    /**
     * 支付成功，修改订单状态
     * @param outTradeNo
     */
    void paySuccess(String outTradeNo);

    /**
     * 用户历史订单调查
     * @param page, pageSize, status
     * @return
     */
    PageResult page(int page, int pageSize, Integer status);

    /**
     * 调查订单的信息
     * @param id
     * @return
     */
    OrderVO query(Long id);

    /**
     * 用户取消订单操作
     * @param id
     */
    void cancel(Integer id);

    /**
     * 用户再来一单
     * @param id
     */
    void agagin(Long id);

    /**
     * 管理员搜索订单
     * @return
     */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 各个状态的订单数量统计
     *
     * @return
     */
    OrderStatisticsVO statistics();

    /**
     * 商家接单
     * @param ordersConfirmDTO
     */
    void orderconfirm(OrdersConfirmDTO ordersConfirmDTO);

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     */
    void orderRejection(OrdersRejectionDTO ordersRejectionDTO);

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     */
    void orderCancel(OrdersCancelDTO ordersCancelDTO);

    /**
     * 商家派送订单
     * @param id
     */
    void orderDeliver(Long id);

    /**
     * 订单派送完成
     * @param id
     */
    void orderFinish(Long id);

    /**
     * 用户催单
     * @param id
     */
    void reminder(Long id);
}
