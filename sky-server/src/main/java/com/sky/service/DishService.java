package com.sky.service;

import com.sky.dto.DishDTO;
import lombok.extern.slf4j.Slf4j;


public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void insertWithFlavor(DishDTO dishDTO);
}
