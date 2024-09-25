package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询对应的套餐id
     * @param dishIds
     * @return
     */
    List<Long> getSetMealIdsByDishIds(List<Long> dishIds);

    /**
     * 向套餐菜品关系表中插入数据
     * @param dishes
     */
    void insertBatch(List<SetmealDish> dishes);

    /**
     * 批量删除套餐中与之对应的菜品
     * @param setmealids
     */
    void deleteBySetmealId(List<Long> setmealids);

    /**
     * 根据套餐id调查套餐中的菜品
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getDishBySetmealId(Long setmealId);

    /**
     * 根据id删除菜品单条
     * @param setmealId
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId2(Long setmealId);
}
