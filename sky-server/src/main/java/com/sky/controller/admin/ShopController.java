package com.sky.controller.admin;

import com.sky.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.*;

@RestController("adminshopcontroller")
@RequestMapping("/admin/shop")
@Api(tags = "店铺相关接口")
@Slf4j
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public final static String KEY = "SHOP_STATUS";
    /**
     * 设置店铺营业状态
     */
    @PutMapping("/{status}")
    @ApiOperation("设置店铺营业状态")
    public Result setStatus(@PathVariable Integer status){
        log.info("店铺的营业状态为：{}",status == 1 ? "营业中" :"打烊了");
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set(KEY,status);
        return Result.success();
    }

    /**
     * 获取店铺营业状态
     */
    @GetMapping("/status")
    @ApiOperation("获取店铺营业状态")
    public Result<Integer> getStatus(){

        ValueOperations valueOperations = redisTemplate.opsForValue();
        Integer status = (Integer)valueOperations.get(KEY);

        log.info("店铺的营业状态为:{}",status == 1 ? "营业中" :"打烊了");

        return Result.success(status);
    }
}
