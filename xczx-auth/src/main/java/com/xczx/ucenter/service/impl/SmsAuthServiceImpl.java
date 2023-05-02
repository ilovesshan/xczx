package com.xczx.ucenter.service.impl;

import com.xczx.ucenter.model.dto.AuthParamsDto;
import com.xczx.ucenter.model.dto.XcUserExt;
import com.xczx.ucenter.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */

@Slf4j
@Service("smsAuthService")
public class SmsAuthServiceImpl implements AuthService {


    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        log.debug("----短信模式方式认证----");



        return null;
    }
}
