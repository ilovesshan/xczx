package com.xczx.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.ucenter.mapper.XcUserMapper;
import com.xczx.ucenter.model.po.XcUser;
import lombok.extern.slf4j.Slf4j;
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
    private XcUserMapper xcUserMapper;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {

        // 根据用户名去数据库查询数据
        XcUser queryXcUser = new XcUser();
        queryXcUser.setUsername(s);
        XcUser selectedXcUserResult = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>(queryXcUser));

        // 如果为空直接返回null即可，SpringSecurity到时候会自己抛出异常
        if (selectedXcUserResult == null) {
            return null;
        }

        // 获取查询到的用户密码
        String password = selectedXcUserResult.getPassword();
        String username = selectedXcUserResult.getUsername();

        // 用户权限,如果不加报Cannot pass a null GrantedAuthority collection 先暂时随便写
        String[] authorities = {"test"};

        // 扩展用户信息类(将查询到的数据转成JSON字符串)
        String userInfoJson = JSON.toJSONString(selectedXcUserResult);

        // 将查询到的用户的密码封装到UserDetails对象中，由SpringSecurity自己比对密码(暂时这样写，后期由我们自己来编写比对逻辑)
        return User.withUsername(userInfoJson).password(password).authorities(authorities).build();
    }
}
