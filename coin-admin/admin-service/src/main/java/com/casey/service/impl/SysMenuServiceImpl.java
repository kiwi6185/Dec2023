package com.casey.service.impl;

import com.casey.service.SysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.casey.mapper.SysMenuMapper;
import com.casey.domain.SysMenu;
import com.casey.service.SysMenuService;
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Autowired
    private SysRoleService sysRoleService;

    @Autowired
    private SysMenuMapper sysMenuMapper;

    /**
     * 通过用户的 id 查询用户的菜单数据
     * @param userId
     * @return
     */
    @Override
    public List<SysMenu> getMenusByUserId(Long userId) {
        // 1. 如果该用户是超级管理员 -> 拥有所有菜单
        if(sysRoleService.isSuperAdmin(userId)){
            return list();  // 查询所有
        }
        // 2. 如果用户不是超级管理员 -> 查询角色 -> 查询菜单
        return sysMenuMapper.selectMenusByUserId(userId);
    }
}
