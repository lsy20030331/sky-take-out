package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品操作
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品相关接口")
    public Result save(@RequestBody DishDTO dishDTO){
        // 清理缓存数据
        String key = "dish_" + dishDTO.getCategoryId();
        cleanCache(key);
        log.info("新增菜品: {}", dishDTO);
        dishService.saveWhithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 菜品的分页调查操作
     * @param dishPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("菜品分页查询相关接口")
    public Result<PageResult> page(DishPageQueryDTO dishPageQueryDTO){
        log.info("菜品分页查询{}", dishPageQueryDTO);
        PageResult pageResult = dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 菜品的批量删除
     * @param ids
     * @return
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    public Result delete(@RequestParam List<Long> ids){  // 使用SpringMVC将传入的ids字符串转化为Long类型的数组元素

        // 将所有的菜品缓存数据清理 以dish_开头
        cleanCache("dish_*");

        log.info("删除相关菜品操作 {}", ids);
        dishService.deleteBetch(ids);
        return Result.success();
    }

    /**
     * 根据id查询菜品
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id来查询菜品")
    public Result<DishVO> getById(@PathVariable Long id){
        log.info("根据id查询菜品: {}", id);
        DishVO dishVO = dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 修改菜品
     * @param dishDTO
     * @return
     */
    @PutMapping
    @ApiOperation("修改菜品操作")
    public Result update(@RequestBody DishDTO dishDTO){

        // 将所有的菜品缓存数据清理 以dish_开头
        cleanCache("dish_*");

        log.info("修改菜品操作: {}", dishDTO);
        dishService.updateWithFlavor(dishDTO);
        return Result.success();
    }

    /**
     * 根据分类调查菜品
     * @param categoryId
     * @return
     */
    @GetMapping("/list")
    @ApiOperation("根据分类调查菜品")
    public Result<List<Dish>> getBycategory(Long categoryId){

        log.info("根据分类调查菜品");
        List<Dish> dishes = dishService.getBycategory(categoryId);
        return Result.success(dishes);
    }

    /**
     * 菜品起售和停售
     * @param status
     * @param id
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("菜品的起售和停售")
    public Result setstatus(@PathVariable Integer status, Long id){
        cleanCache("dish_*");
        log.info("菜品的起售和停售");
        dishService.setstatus(status, id);
        return Result.success();
    }
    private void cleanCache(String pattern){
        Set keys = redisTemplate.keys("dish_*");
        redisTemplate.delete(keys);
    }
}
