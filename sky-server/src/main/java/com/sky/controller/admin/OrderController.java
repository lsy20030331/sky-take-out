package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Api(tags = "订单管理接口")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
     * 搜索订单
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/conditionSearch")
    @ApiOperation("搜索订单")
    public Result<PageResult> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO){
        log.info("订单搜索");
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 调查订单详细信息
     * @param id
     * @return
     */
    @GetMapping("/details/{id}")
    @ApiOperation("调查订单的详细信息")
    public Result<OrderVO> querydetails(@PathVariable Long id){
        log.info("管理员调查订单的详细信息");
        OrderVO orderVO = orderService.query(id);
        return Result.success(orderVO);
    }

    /**
     * 各个订单的数量统计
     * @return
     */
    @GetMapping("/statistics")
    @ApiOperation("各个订单的数量统计")
    public Result<OrderStatisticsVO> statistics(){
        log.info("各个订单的数量统计");
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
     * 商家接单
     * @param ordersConfirmDTO
     * @return
     */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result orderconfirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO){
        log.info("商家接单");
        orderService.orderconfirm(ordersConfirmDTO);
        return Result.success();
    }

    /**
     * 商家拒单
     * @param ordersRejectionDTO
     * @return
     */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result orderRejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("商家拒单");
        orderService.orderRejection(ordersRejectionDTO);
        return Result.success();
    }

    /**
     * 商家取消订单
     * @param ordersCancelDTO
     * @return
     */
    @PutMapping("/cancel")
    @ApiOperation("商家取消订单")
    public Result orderCancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("商家取消订单");
        orderService.orderCancel(ordersCancelDTO);
        return Result.success();
    }

    /**
     * 商家派送订单
     * @param id
     * @return
     */
    @PutMapping("/delivery/{id}")
    @ApiOperation("商家派送订单")
    public Result orderDeliver(@PathVariable Long id){
        log.info("商家派送订单");
        orderService.orderDeliver(id);
        return Result.success();
    }

    /**
     * 订单派送完成
     * @param id
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result orderFinish(@PathVariable Long id){
        log.info("订单派送完成");
        orderService.orderFinish(id);
        return Result.success();
    }
}
