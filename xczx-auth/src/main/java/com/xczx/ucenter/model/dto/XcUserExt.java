package com.xczx.ucenter.model.dto;

import com.xczx.ucenter.model.po.XcUser;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;


@Data
public class XcUserExt extends XcUser {
    //用户权限
    List<String> permissions = new ArrayList<>();
}
