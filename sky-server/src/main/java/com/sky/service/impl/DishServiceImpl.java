package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.annotation.AutoFill;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.entity.Employee;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
     * 新增菜品
     * @param dishDTO
     */
    @Override
    @Transactional
    public void insertWithFlavor(DishDTO dishDTO) {
        // 向菜品表插入一条数据
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.insert(dish);

        // 向口味表插入多条数据
        // 获得dishId
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            // 把dishId加进去
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);
        }

    }

    /**
     * 分页查询
     * @param dishPageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page =  dishMapper.pageQuery(dishPageQueryDTO);

        // 封装 PageResult
        int pageNum = page.getPageNum();
        List<DishVO> result = page.getResult();
        PageResult pageResult = new PageResult(pageNum, result);

        return pageResult;


    }

    /**
     * 删除菜品
     * @param ids
     */
    @Override
    @Transactional
    public void delete(List<Long> ids) {
        // 如果起售，不能删除
        for (Long id : ids){
            Dish dish =  dishMapper.getById(id);
            if (dish.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        // 如果被套餐包含，不能删除
        List<Long> list = setmealDishMapper.getByIds(ids);
        if (list != null && list.size() > 0){
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        // 删除菜品 以及 （如果有的话）口味
        dishMapper.delete(ids);
        dishFlavorMapper.delete(ids); // 这里的id是dish_id

    }

    /**
     * 根据dishid查询 dish以及flavor
     * @param id
     * @return
     */
    @Override
    public DishVO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors = dishFlavorMapper.getById(id) ;
        // 数据拷贝到DishVO
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
     * 通过id修改菜品
     * @param dishDTO
     */
    @Override
    public void updateById(DishDTO dishDTO) {
        // 1 修改 dish
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        dishMapper.updateById(dish);

        // 2 修改 dish_flavor
        // 方案：先删除，再增加
        dishFlavorMapper.deleteByDishId(dish.getId());
        // 向口味表插入多条数据
        // 获得dishId
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if (flavors != null && flavors.size() > 0){
            // 把dishId加进去
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insert(flavors);
        }


    }
}
