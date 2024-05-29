package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


public interface DishService {
    /**
     * 新增菜品
     * @param dishDTO
     */
    void insertWithFlavor(DishDTO dishDTO);

    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    void delete(List<Long> ids);

    DishVO getByIdWithFlavor(Long id);

    void updateById(DishDTO dishDTO);

    List<DishVO> listWithFlavor(Dish dish);
}
