package com.sky.service;

import com.sky.dto.DishDTO;
import org.springframework.stereotype.Service;

public interface DisService {

    /**
     * 新增菜品和对应的口味
     * @param dishDTO
     */
    void saveWhithFlavor(DishDTO dishDTO);
}
