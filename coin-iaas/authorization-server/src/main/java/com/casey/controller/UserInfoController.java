package com.casey.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class UserInfoController {
    /**
     * 当前登录的用户对象
     * @param principal
     * @return
     */
    @GetMapping("/user/info")
    public Principal userInfo(Principal principal){// 此处的principal 由OAuth2.0 框架自动注入
        // 原理就是：利用Context概念，将授权用户放在线程里面，利用ThreadLocal来获取当前的用户对象
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        return principal;
    }
}
