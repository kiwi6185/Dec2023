package com.casey.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.api.IErrorCode;
import com.baomidou.mybatisplus.extension.enums.ApiErrorCode;
import com.baomidou.mybatisplus.extension.exceptions.ApiException;
import com.casey.domain.SysMenu;
import com.casey.feign.JwtToken;
import com.casey.feign.OAuth2FeignClient;
import com.casey.model.LoginResult;
import com.casey.service.SysLoginService;
import com.casey.service.SysMenuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SysLoginServiceImpl implements SysLoginService {

    @Autowired
    private OAuth2FeignClient oAuth2FeignClient;

    @Autowired
    private SysMenuService sysMenuService;

    @Value("${basic.token:Basic Y29pbi1hcGk6Y29pbi1zZWNyZXQ=}")
    private String basicToken;

    /**
     * 登录的实现
     * @param username 用户名
     * @param password 用户密码
     * @return 登录的结果
     */
    @Override
    public LoginResult login(String username, String password) {
        log.info("用户{}开始登录", username);
        // 1. 获取 token 需要远程调用 authorization-server 该服务
        ResponseEntity<JwtToken> tokenResponseEntity = oAuth2FeignClient.getToken("password", username, password, "admin_type", basicToken);
        if(tokenResponseEntity.getStatusCode() != HttpStatus.OK){
            throw new ApiException(ApiErrorCode.FAILED);
        }
        JwtToken jwtToken = tokenResponseEntity.getBody();
        log.info("远程调用授权服务器成功，获取的token为{}", JSON.toJSONString(jwtToken, true));
        String token = jwtToken.getAccessToken();

        // 2. 查询我们的菜单数据
        Jwt jwt = JwtHelper.decode(token);
        String jwtJsonStr = jwt.getClaims();
        JSONObject jwtJson = JSON.parseObject(jwtJsonStr);
        Long userId = Long.valueOf(jwtJson.getString("user_name"));
        List<SysMenu> menu = sysMenuService.getMenusByUserId(userId);

        // 3. 权限数据怎么查询 -- 不需要查询，因为我们的 jwt 里面已经包含了
        JSONArray authoritiesJsonArray = jwtJson.getJSONArray("authorities");
        List<SimpleGrantedAuthority> authorities = authoritiesJsonArray.stream()    // 组装我们的权限数据
                                .map(authorityJson -> new SimpleGrantedAuthority(authorityJson.toString()))
                                .collect(Collectors.toList());
        return new LoginResult(token, menu, authorities);
    }
}
