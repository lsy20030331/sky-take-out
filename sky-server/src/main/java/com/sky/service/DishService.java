package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

import java.util.List;

public interface DishService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    void saveWhithFlavor(DishDTO dishDTO);



    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 删除菜品
     * @param ids
     */
    void deleteBetch(List<Long> ids);

    /**
     * 根据id调查菜品
     * @param id
     */
    DishVO getByIdWithFlavor(Long id);

    /**
     * 修改菜品的操作
     * @param dishDTO
     */
    void updateWithFlavor(DishDTO dishDTO);

    /**
     * 根据分类调查菜品
     * @param categoryId
     * @return
     */
    List<Dish> getBycategory(Long categoryId);
}
