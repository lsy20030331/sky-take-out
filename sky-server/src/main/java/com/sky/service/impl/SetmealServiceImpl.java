package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {

    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setMealDishMapper;


    /**
     * 生成套餐,同时保存菜品和套餐的联系
     * @param setmealDTO
     */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);

        // 获取套餐id(因为前端并未传入id)
        Long setmealId = setmeal.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();  // 获取套餐与菜品的关系

            // 向dishes中插入套餐的id
            dishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmealId);
            });

        setMealDishMapper.insertBatch(dishes);  // 向套餐菜品关系表插入数据
    }

    /**
     * 套餐的分页查询
     * @param setmealPageQueryDTO
     * @return
     */
    @Override
    public PageResult page(SetmealPageQueryDTO setmealPageQueryDTO) {

        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        // 根据接口文档返回的数据类型所以要用SetmealVO来返回
        // 因为Setmeal中没有categoryName所以使用SetmealVO作为泛型

        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);

        return new PageResult(page.getTotal(),page.getResult());
    }

    /**
     * 套餐的批量删除
     * @param ids
     */
    @Override
    public void delete(List<Long> ids) {
        // 判断当前套餐是否能删除(套餐是否处于售卖状态)
        for (Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        // 删除套餐菜品关系表中与之对应的菜品
        setMealDishMapper.deleteBySetmealId(ids);

        // 删除套餐
        setmealMapper.deleteById(ids);
    }

    /**
     * 根据id查询套餐信息
     * @param id
     * @return
     */
    @Override
    public SetmealVO getByIdWithDish(Long id) {
        // 根据id调查套餐
        Setmeal setmeal = setmealMapper.getById(id);

        // 根据套餐id调查套餐中的菜品
        List<SetmealDish> setmealDishes = setMealDishMapper.getDishBySetmealId(id);

        // 将数据封装到VO对象中
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal, setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);

        return setmealVO;
    }

    /**
     * 修改套餐操作
     * @param setmealDTO
     */
    @Override
    public void update(SetmealDTO setmealDTO) {
        // 因为传入的DTO中可能有菜品的数据 不能拿DTO直接更新菜品数据如果直接更新那么会在数据库中出现重复的数据
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);

        // 修改菜品表的基本信息
        setmealMapper.update(setmeal);

        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();

        // 删除菜品数据
        setMealDishMapper.deleteBySetmealId2(setmealDTO.getId());
        // 插入修改的套餐数据(为菜品设置对应套餐的id)
        if(setmealDishes != null && setmealDishes.size() > 0){
            setmealDishes.forEach(Dishes -> {
                Dishes.setSetmealId(setmealDTO.getId());
            });
        }
        // 新增菜品数据
        setMealDishMapper.insertBatch(setmealDishes);

    }

    /**
     * 套餐的起售停售状态
     * @param status
     * @param id
     */
    @Override
    public void setstatus(Integer status, Long id) {
        setmealMapper.setstatus(status, id);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
