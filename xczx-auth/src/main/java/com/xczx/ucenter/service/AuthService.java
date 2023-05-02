package com.xczx.ucenter.service;

import com.xczx.ucenter.model.dto.AuthParamsDto;
import com.xczx.ucenter.model.dto.XcUserExt;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */
public interface AuthService {
    XcUserExt execute(AuthParamsDto authParamsDto);
}
