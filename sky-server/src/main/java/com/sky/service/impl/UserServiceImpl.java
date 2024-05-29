package com.sky.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.FailedLoginException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    // 微信服务接口url
    private static final String url = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    @Override
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 1 通过 微信接口服务 获取 对应的openid
        String openid = getOpenid(userLoginDTO.getCode());
        log.info("openid是：{}",openid);

        // 2 判断openid是否为空，返回登陆失败信息
        if (openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 3 通过openid得到用户信息
        User user =  userMapper.queryByOpenid(openid);

        // 4 如果没得到信息，就在数据库插入用户信息
        if (user == null){
            User newUser = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(newUser);
            // 再次查询
            User user1 =  userMapper.queryByOpenid(openid);
            return user1;
        }

        return user;
    }

    /**
     * 写成模块，方便管理
     */
    private String getOpenid(String code){
        Map<String, String> map1 = new HashMap<>();
        map1.put("appid",weChatProperties.getAppid());
        map1.put("secret",weChatProperties.getSecret());
        map1.put("js_code",code);
        map1.put("grant_type","authorization_code");

        String json = HttpClientUtil.doGet(url, map1); // 返回的是json格式的字符串
        JSONObject jsonObject = JSONObject.parseObject(json);// 解析json
        String openid = jsonObject.getString("openid");
        return openid;
    }


}
