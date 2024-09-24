package com.sky.service.impl;

import com.github.pagehelper.Constant;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;

import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetMealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DisService;
import com.sky.vo.DishVO;
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

    @Autowired
    private SetMealDishMapper setMealDishMapper;
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
    /**
     * 菜品的分页查询
     * @param dishPageQueryDTO
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());  // 调用PageHelper来进行分页调查
        // 由于接口文档的要求所以得用DishVO来返回
        // 因为Dish中没有categoryName对象所以创建了DishVO里面存在此对象用于返回给前端所以泛型为DishVO
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);

        long total = page.getTotal();
        List<DishVO> records = page.getResult();

        return new PageResult(total, records);
    }

    /**
     * 菜品的删除
     * @param ids
     */
    @Override
    @Transactional
    public void deleteBetch(List<Long> ids) {
        // 判断当前菜品是否能删除---判断当前菜品是否处于售卖的状态???
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }

        // 判断当前菜品是否能删除---判断当前菜品是否和套餐相关联???
        List<Long> setMealIds = setMealDishMapper.getSetMealIdsByDishIds(ids);  // 获取被关联套餐的id eg:因为一个菜品可能关联多个套餐所以用List存储
        if(setMealIds != null && setMealIds.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }

        // 删除菜品表中的相关数据
            dishMapper.deleteById(ids);
        // 删除与当前被删除菜品的相关口味数据
            dishFlavorMapper.deleteByDishId(ids);
    }

    @Override
    public DishVO getByIdWithFlavor(Long id) {
        // 根据id调查菜品数据
        Dish dish = dishMapper.getById(id);

        // 根据菜品id调查菜品对应的口味数据
        List<DishFlavor> dishFlavors = dishFlavorMapper.getFlavorByDishId(id);

        // 将获取到的属性封装到VO对象中
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish, dishVO);
        dishVO.setFlavors(dishFlavors);

        return dishVO;
    }

    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);

        // 修改菜品表的基本信息
        // 因为DTO中封装了口味的信息虽然传入DTO没有问题但是根据项目规范还是传入无口味信息的Dish类对象比较合适
        dishMapper.update(dish);
        // 删除原有的口味信息
        dishFlavorMapper.deleteFlavor(dishDTO.getId());
        //添加新的口味信息
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && flavors.size() > 0){  // 检查传入的口味是否为空如果不为空则调用Mapper插入数据
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishDTO.getId());
            });
            //向口味表插入n条数据
            dishFlavorMapper.insertBatch(flavors);
        }
    }

}
