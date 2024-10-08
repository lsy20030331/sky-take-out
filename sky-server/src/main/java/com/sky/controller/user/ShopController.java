package com.sky.controller.user;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("UserShopController")
@RequestMapping("/user/shop")
@Slf4j
@Api(tags = "C端-店铺相关接口")

public class ShopController{
    public static final String KEY = "SHOP_STATUS";

    @Autowired
    private RedisTemplate redisTemplate;

    @GetMapping("/status")
    @ApiOperation("查询店铺的营业状态")
    public Result<Integer> queryStatus(){
        Integer integer = (Integer) redisTemplate.opsForValue().get(KEY);
        log.info("查询店铺的营业状态： {}", integer == 1 ? "营业中" : "打烊中");
        return Result.success(integer);
    }
}
