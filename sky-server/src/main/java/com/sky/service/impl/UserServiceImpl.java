package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.BaseException;
import com.sky.exception.LoginFailedException;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import com.sky.mapper.UserMapper;

/**
 * @author yao
 * @version 1.0
 * @date 2024/7/28 - 19:53
 * @className UserServiceImpl
 * @since 1.0
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    // 微信服务接口地址
    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信登录
     *
     * @param loginDTO
     * @return
     */
    @Override
    public User wxLogin(UserLoginDTO loginDTO) {
        // 调用微信服务接口，获取openId
        String openid = getOpenid(loginDTO.getCode());

        // 判断openId是否为null，如果空，登录失败，抛出业务异常
        if(openid == null){
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }

        // 不为空，判断是否新用户，是，注册并登录，不是直接登录
        User user = userMapper.getByOpenid(openid);
        if(null == user){
            user = User.builder().openid(openid).createTime(LocalDateTime.now()).build();
            int i = userMapper.insert(user);
            if (i != 1){
                throw new BaseException(MessageConstant.UNKNOWN_ERROR);
            }
        }

        // 返回用户对象
        return user;
    }

    private String getOpenid(String code){
        Map<String, String> param = new HashMap<>();
        param.put("appid", weChatProperties.getAppid());
        param.put("secret", weChatProperties.getSecret());
        param.put("js_code", code);
        param.put("grant_type", "authorization_code");
        String jsonstr = HttpClientUtil.doGet(WX_LOGIN, param);

        JSONObject jsonObject = JSON.parseObject(jsonstr);
        return jsonObject.getString("openid");
    }
}
