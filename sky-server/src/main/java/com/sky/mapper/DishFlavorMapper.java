package com.sky.mapper;

import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishFlavorMapper {

    /**
     * 批量插入口味
     * @param flavors
     */

    void insertBatch(List<DishFlavor> flavors);

    /**
     * 根据id来删除口味
     * @param dishIds
     */
    void deleteByDishId(List<Long> dishIds);

    /**
     * 根据dishId调查菜品口味
     * @param dishId
     * @return
     */
    @Select("select * from dish_flavor where dish_id = #{dishId}")
    List<DishFlavor> getFlavorByDishId(Long dishId);

    /**
     * 根据dishId删除菜品口味
     * @param dishId
     */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteFlavor(Long dishId);

    /**
     * 更新口味数据
     * @param flavors
     */
}
