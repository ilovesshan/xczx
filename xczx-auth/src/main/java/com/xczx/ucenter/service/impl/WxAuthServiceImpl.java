package com.xczx.ucenter.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.xczx.ucenter.mapper.XcUserMapper;
import com.xczx.ucenter.mapper.XcUserRoleMapper;
import com.xczx.ucenter.model.dto.AuthParamsDto;
import com.xczx.ucenter.model.dto.XcUserExt;
import com.xczx.ucenter.model.po.XcUser;
import com.xczx.ucenter.model.po.XcUserRole;
import com.xczx.ucenter.service.AuthService;
import com.xczx.ucenter.service.WxAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

import static org.springframework.beans.BeanUtils.copyProperties;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/2
 * @description:
 */

@Slf4j
@Service("wxAuthService")
public class WxAuthServiceImpl implements AuthService, WxAuthService {

    @Value("${weixin.appid}")
    private String appid;

    @Value("${weixin.secret}")
    private String secret;

    @Resource
    private XcUserMapper xcUserMapper;

    @Resource
    private XcUserRoleMapper xcUserRoleMapper;

    @Resource
    private RestTemplate restTemplate;


    @Resource
    private WxAuthServiceImpl wxAuthServiceProxy;


    @Override
    public XcUserExt execute(AuthParamsDto authParamsDto) {
        log.debug("----微信扫码方式认证----");
        String username = authParamsDto.getUsername();
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getUsername, username));
        if (xcUser == null) {
            throw new RuntimeException("账号不存在");
        }
        XcUserExt xcUserExt = new XcUserExt();
        copyProperties(xcUser, xcUserExt);
        return xcUserExt;
    }

    @Override
    public XcUser wxAuth(String code) {
        // 通过code获取access_token
        Map<String, String> rsponseData = getAccessTokenByCode(code);
        String accessToken = rsponseData.get("access_token");
        if (StringUtils.isEmpty(accessToken)) {
            return null;
        }

        // 获取用户个人信息（UnionID机制）
        Map<String, String> userInfo = getUserInfoBytAccessToken(accessToken, appid);
        String unionid = userInfo.get("unionid");
        if (StringUtils.isEmpty(unionid)) {
            return null;
        }

        // 将用户信息存储到数据库
        return wxAuthServiceProxy.saveUserInfoToDb(userInfo);
    }


    @Transactional
    public XcUser saveUserInfoToDb(Map<String, String> userInfo) {
        String unionid = userInfo.get("unionid").toString();
        //根据unionid查询数据库
        XcUser xcUser = xcUserMapper.selectOne(new LambdaQueryWrapper<XcUser>().eq(XcUser::getWxUnionid, unionid));
        if (xcUser != null) {
            return xcUser;
        }
        String userId = UUID.randomUUID().toString();
        xcUser = new XcUser();
        xcUser.setId(userId);
        xcUser.setWxUnionid(unionid);
        //记录从微信得到的昵称
        xcUser.setNickname(userInfo.get("nickname"));
        xcUser.setUserpic(userInfo.get("headimgurl"));
        xcUser.setName(userInfo.get("nickname"));
        xcUser.setUsername(unionid);
        xcUser.setPassword(unionid);
        xcUser.setUtype("101001");//学生类型
        xcUser.setStatus("1");//用户状态
        xcUser.setCreateTime(LocalDateTime.now());
        xcUserMapper.insert(xcUser);
        XcUserRole xcUserRole = new XcUserRole();
        xcUserRole.setId(UUID.randomUUID().toString());
        xcUserRole.setUserId(userId);
        xcUserRole.setRoleId("17");//学生角色
        xcUserRoleMapper.insert(xcUserRole);
        return xcUser;
    }

    /**
     * 参考地址：https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Authorized_Interface_Calling_UnionID.html
     * 微信开放接口：https://api.weixin.qq.com/sns/userinfo?access_token=ACCESS_TOKEN&openid=OPENID
     * 接口返回值：
     * {
     * "openid":"OPENID",
     * "nickname":"NICKNAME",
     * "sex":1,
     * "province":"PROVINCE",
     * "city":"CITY",
     * "country":"COUNTRY",
     * "headimgurl": "https://thirdwx.qlogo.cn/mmopen/g3MonUZtNHkdmzicIlibx6iaFqAc56vxLSUfpb6n5WKSYVY0ChQKkiaJSgQ1dZuTOgvLLrhJbERQQ4eMsv84eavHiaiceqxibJxCfHe/0",
     * "privilege":[
     * "PRIVILEGE1",
     * "PRIVILEGE2"
     * ],
     * "unionid": " o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * <p>
     * }
     */
    private Map<String, String> getUserInfoBytAccessToken(String access_token, String openid) {
        String wxUrl_template = "https://api.weixin.qq.com/sns/userinfo?access_token=%s&openid=%s";
        String wxUrl = String.format(wxUrl_template, access_token, openid);
        log.info("调用微信接口获取个人信息, url:{}", wxUrl);
        ResponseEntity<String> exchange = restTemplate.exchange(wxUrl, HttpMethod.POST, null, String.class);
        //防止乱码进行转码
        String result = new String(exchange.getBody().getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        log.info("调用微信接口获取个人信息: 返回值:{}", result);
        return JSON.parseObject(result, Map.class);
    }


    /**
     * 参考地址：https://developers.weixin.qq.com/doc/oplatform/Website_App/WeChat_Login/Wechat_Login.html
     * 微信开放接口：https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
     * 接口返回值：
     * {
     * "access_token":"ACCESS_TOKEN",
     * "expires_in":7200,
     * "refresh_token":"REFRESH_TOKEN",
     * "openid":"OPENID",
     * "scope":"SCOPE",
     * "unionid": "o6_bmasdasdsad6_2sgVt7hMZOPfL"
     * }
     */
    private Map<String, String> getAccessTokenByCode(String code) {
        String urlTemplate = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=%s&secret=%s&code=%s&grant_type=authorization_code";
        String url = String.format(urlTemplate, appid, secret, code);
        log.info("调用微信接口申请access_token, url:{}", url);
        ResponseEntity<String> exchange = restTemplate.exchange(url, HttpMethod.POST, null, String.class);
        String body = exchange.getBody();
        log.info("调用微信接口申请access_token, response:{}", body);
        return JSON.parseObject(body, Map.class);
    }
}
