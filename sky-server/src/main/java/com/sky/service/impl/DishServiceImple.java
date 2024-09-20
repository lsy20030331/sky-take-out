package com.sky.service.impl;

import com.sky.dto.DishDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.service.DisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImple implements DisService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    @Override
    @Transactional  // 事务正常起作用。无异常时正常提交，有异常时数据回滚(因为此时操作的是两个表所以要么同时成功要么同时失败)
    public void saveWhithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        // 向菜品表插入1条数据
        dishMapper.insert(dish);

        Long dishId = dish.getId();  // 根据接口文档,前端传入的数据并没有id,所以要主动获取dish中的id详细见DishMapper.xml
        // 插入口味数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){  // 检查传入的口味是否为空如果不为空则调用Mapper插入数据
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });

            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }
}
