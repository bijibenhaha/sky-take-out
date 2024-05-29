package com.sky.mapper;

import com.sky.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper {

    /**
     * 查询用户信息
     * @param openid
     * @return
     */
    @Select("select  * from user where openid = #{openid}")
    User queryByOpenid(String openid);

    /**
     * 插入新用户信息, 注意，需要获取id
     * @param newUser
     */

    void insert(User newUser);
}
