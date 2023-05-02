package com.xczx.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.xczx.ucenter.model.dto.AuthParamsDto;
import com.xczx.ucenter.model.dto.XcUserExt;
import com.xczx.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */
@Slf4j
@Service
public class UserDetailServiceImpl implements UserDetailsService {

    @Resource
    private ApplicationContext springApplication;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        AuthParamsDto authParamsDto = null;
        try {
            authParamsDto = JSON.parseObject(s, AuthParamsDto.class);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("认证请求数据格式不对");
        }

        // 根据认证类型来选择对应的认证服务(注意Bean命名规范)
        String authType = authParamsDto.getAuthType();
        String beanName = authType + "AuthService";
        AuthService authService = springApplication.getBean(beanName, AuthService.class);
        // 调用对应服务处理业务逻辑
        XcUserExt xcUserExt = authService.execute(authParamsDto);

        String[] authorities = {"test"};
        String password = xcUserExt.getPassword();
        xcUserExt.setPassword(null);
        String userInfoJson = JSON.toJSONString(xcUserExt);
        return User.withUsername(userInfoJson).password(password).authorities(authorities).build();
    }
}
