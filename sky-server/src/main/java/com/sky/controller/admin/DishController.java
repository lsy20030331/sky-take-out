package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DisService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜品管理
 */
@RestController
@RequestMapping("/admin/dish")
@Api(tags = "菜品相关接口")
@Slf4j
public class DishController {

    @Autowired
    private DisService disService;

    /**
     * 新增菜品操作
     * @param dishDTO
     * @return
     */
    @PostMapping
    @ApiOperation("新增菜品相关接口")
    public Result save(@RequestBody DishDTO dishDTO){
        log.info("新增菜品: {}", dishDTO);
        disService.saveWhithFlavor(dishDTO);
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
        PageResult pageResult = disService.pageQuery(dishPageQueryDTO);
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
        log.info("删除相关菜品操作 {}", ids);
        disService.deleteBetch(ids);
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
        DishVO dishVO = disService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    @PutMapping
    @ApiOperation("修改菜品操作")
    public Result update(@RequestBody DishDTO dishDTO){
        log.info("修改菜品操作: {}", dishDTO);
        disService.updateWithFlavor(dishDTO);
        return Result.success();
    }
}
