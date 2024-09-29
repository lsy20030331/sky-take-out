package com.sky.controller.user;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;
import com.sky.result.Result;
import com.sky.service.ShoppingCartService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Api(tags = "C端-购物车相关接口")
@Slf4j
@RequestMapping("/user/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 购物车新增商品的相关操作
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/add")
    @ApiOperation("购物车新增商品")
    public Result add(@RequestBody ShoppingCartDTO shoppingCartDTO){
        log.info("用户添加商品至购物车商品信息: {}", shoppingCartDTO);
        shoppingCartService.add(shoppingCartDTO);
        return Result.success();
    }

    /**
     * 调查购物车商品
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("显示商品数据")
    public Result<List<ShoppingCart>> query(){
        log.info("调查购物车商品");
        List<ShoppingCart> shoppingCart = shoppingCartService.query();
        return Result.success(shoppingCart);
    }

    /**
     * 清空购物车商品
     * @return
     */
    @DeleteMapping("/clean")
    @ApiOperation("清空购物车")
    public Result cleanShoppingCart(){
        shoppingCartService.cleanShoppingCart();
        return Result.success();
    }

    /**
     * 删除购物车单件商品
     * @param shoppingCartDTO
     * @return
     */
    @PostMapping("/sub")
    @ApiOperation("删除购物车单个商品")
    public Result cleanOneItem(@RequestBody ShoppingCartDTO shoppingCartDTO){
        shoppingCartService.cleanOneItem(shoppingCartDTO);
        return Result.success();
    }
}
