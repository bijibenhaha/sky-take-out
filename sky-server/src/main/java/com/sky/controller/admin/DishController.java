package com.sky.controller.admin;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@Api(tags = "菜品相关接口")
@Slf4j
@RequestMapping("/admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    @ApiOperation("新增菜品")
    public Result insert(@RequestBody DishDTO dishDTO){
        log.info("新增菜品: {}",dishDTO);
        dishService.insertWithFlavor(dishDTO);

        // 清理缓存
        String key = "dish_" +  dishDTO.getCategoryId();
        clearRedis(key);

        return Result.success();
    }

    /**
     * 分页查询
     */
    @GetMapping("/page")
    @ApiOperation("分页查询菜品")
    public Result<PageResult> pageQuery(DishPageQueryDTO dishPageQueryDTO){
        log.info("分页查询菜品: {}",dishPageQueryDTO);
        PageResult pageResult =  dishService.pageQuery(dishPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 删除菜品
     */
    @DeleteMapping
    @ApiOperation("删除菜品")
    // 形参：让spring 把字符串转成List (1,2,3 -> )
    public Result delete(@RequestParam List<Long> ids) {
        log.info("删除的菜品id：{}", ids);
        dishService.delete(ids);

        // 清理缓存 比较复杂 有可能涉及多个分类 -> 全部删除
        clearRedis("dish_*");

        return Result.success();
    }

    /**
     * 根据dishid查询 dish以及flavor
     *
     */
    @GetMapping("/{id}")
    @ApiOperation("根据dishid查询 dish以及flavor")
    public Result<DishVO> getByIdWithFlavor(@PathVariable Long id){
        log.info("查询菜品id：{}",id);
        DishVO dishVO =  dishService.getByIdWithFlavor(id);
        return Result.success(dishVO);
    }

    /**
     * 通过id修改菜品
     *
     */
    @PutMapping
    @ApiOperation("通过id修改菜品")
    public Result updateById(@RequestBody DishDTO dishDTO){
        log.info("通过id修改菜品：{}",dishDTO);
        dishService.updateById(dishDTO);

        // 清理缓存 比较复杂 有可能涉及多个分类 -> 全部删除
        clearRedis("dish_*");
        return Result.success();
    }

    // 清理缓存数据
    private void clearRedis(String template){
        Set keys = redisTemplate.keys(template);
        redisTemplate.delete(keys);
    }



}
