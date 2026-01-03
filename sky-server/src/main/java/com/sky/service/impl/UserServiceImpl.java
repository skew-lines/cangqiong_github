package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.constant.MessageConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.exception.LoginFailedException;
import com.sky.mapper.UserMapper;
import com.sky.properties.WeChatProperties;
import com.sky.service.UserService;
import com.sky.utils.HttpClientUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    public static final String WX_LOGIN = "https://api.weixin.qq.com/sns/jscode2session";

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private UserMapper userMapper;

    /**
     * 微信用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    public User WechatLogin(UserLoginDTO userLoginDTO) {
        //首先通过HttpClient向微信发送请求获得用户的openid
        String openid = getOpenId(userLoginDTO);

        //openid为空，则抛出异常登录失败
        if(openid == null) {
            throw new LoginFailedException(MessageConstant.LOGIN_FAILED);
        }
        //判断当前openid是否在数据库表中，没有则添加用户数据，注册用户
        User user = userMapper.getByOpenId(openid);
        if(user == null) {
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
        }

        //返回数据,jwt令牌在controller层生成

        return user;
    }

    /**
     * 调用微信接口服务，获取当前用户的openid
     * @param userLoginDTO
     * @return
     */
    private String getOpenId(UserLoginDTO userLoginDTO) {
        //发送请求获取openid
        //使用封装的工具
        Map<String, String> query = new HashMap<>();
        query.put("appid",weChatProperties.getAppid());
        query.put("secret",weChatProperties.getSecret());
        query.put("grant_type","authorization_code");
        query.put("js_code", userLoginDTO.getCode());
        String response = HttpClientUtil.doGet(WX_LOGIN, query);

        JSONObject jsonObject = JSON.parseObject(response);

        String openid = jsonObject.getString("openid");
        return openid;
    }
}
