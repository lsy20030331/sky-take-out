package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.service.WorkSpaceService;
import com.sky.vo.BusinessDataVO;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.OrderOverViewVO;
import com.sky.vo.SetmealOverViewVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@Api(tags = "工作台相关接口")
@RequestMapping("/admin/workspace")
public class WorkSpaceController {

    @Autowired
    private WorkSpaceService workSpaceService;

    /**
     * 调查今日运营数据
     * @return
     */
    @GetMapping("/businessData")
    @ApiOperation("今日运营数据")
    public Result<BusinessDataVO> getRunData(){
        log.info("调查今日运营数据");
        BusinessDataVO businessDataVO = workSpaceService.getRunData();
        return Result.success(businessDataVO);
    }

    /**
     * 调查订单管理数据
     * @return
     */
    @GetMapping("/overviewOrders")
    @ApiOperation("调查订单管理数据")
    public Result<OrderOverViewVO> getOverViewOrders(){
        log.info("订单管理数据");
        OrderOverViewVO orderOverViewVO = workSpaceService.getOverViewOrders();
        return Result.success(orderOverViewVO);
    }

    /**
     * 调查菜品总览
     */
    @GetMapping("overviewDishes")
    @ApiOperation("调查菜品总览")
    public Result<DishOverViewVO> getOverViewDishes(){
        log.info("调查菜品总览");
        DishOverViewVO dishOverViewVO = workSpaceService.getOverViewDishes();
        return Result.success(dishOverViewVO);
    }

    /**
     * 调查套餐总览
     */
    @GetMapping("overviewSetmeals")
    @ApiOperation("调查菜品总览")
    public Result<SetmealOverViewVO> getOverViewSetmeals(){
        log.info("调查菜品总览");
        SetmealOverViewVO setmealOverViewVO = workSpaceService.getOverViewSetmeals();
        return Result.success(setmealOverViewVO);
    }
}
