package com.casey.service;

import com.casey.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysMenuService extends IService<SysMenu>{

    /**
     * 通过用户的 id 查询用户的菜单数据
     * @param userId
     * @return
     */
    List<SysMenu> getMenusByUserId(Long userId);
}
