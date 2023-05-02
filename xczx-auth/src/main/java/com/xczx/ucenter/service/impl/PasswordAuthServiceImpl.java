package com.xczx.ucenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.ucenter.mapper.XcUserMapper;
import com.xczx.ucenter.model.dto.AuthParamsDto;
import com.xczx.ucenter.model.dto.XcUserExt;
import com.xczx.ucenter.model.po.XcUser;
import com.xczx.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
@Service("passwordAuthService")
public class PasswordAuthServiceImpl implements AuthService {
    @Resource
    private XcUserMapper xcUserMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        log.debug("----用户名密码模式方式认证----");
        // 根据用户名去数据库查询数据
        String username = authParamsDto.getUsername();
        XcUser selectedXcUserResult = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        // 如果为空直接返回null即可，SpringSecurity到时候会自己抛出异常
        if (selectedXcUserResult == null) {
            return null;
        }

        //取出数据库存储的正确密码
        String passwordDb = selectedXcUserResult.getPassword();
        String passwordForm = authParamsDto.getPassword();
        boolean matches = passwordEncoder.matches(passwordForm, passwordDb);
        if (!matches) {
            throw new RuntimeException("账号或密码错误");
        }

        XcUserExt xcUserExt = new XcUserExt();
        BeanUtils.copyProperties(selectedXcUserResult, xcUserExt);
        return xcUserExt;
    }
}
