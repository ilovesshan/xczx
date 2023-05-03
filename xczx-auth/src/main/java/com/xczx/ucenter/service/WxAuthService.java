package com.xczx.ucenter.service;

import com.xczx.ucenter.model.po.XcUser;

public interface WxAuthService {

    public XcUser wxAuth(String code);

}