package com.casey.service.impl;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.casey.domain.SysRole;
import com.casey.mapper.SysRoleMapper;
import com.casey.service.SysRoleService;
@Service
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    @Autowired
    private SysRoleMapper sysRoleMapper;

    /**
     * 判断一个用户是否为超级管理员
     * @param userId
     * @return
     */
    @Override
    public boolean isSuperAdmin(Long userId) {
        // 当用户角色的 code 为 ROLE_ADMIN 时，该用户为超级管理员
        // 用户id -> 用户角色 -> 该角色是否为 ROLE_ADMIN
        String roleCode = sysRoleMapper.getUserRoleCode(userId);
        if(StringUtils.isEmpty(roleCode) && roleCode.equals("ROLE_ADMIN")){
            return true;
        }
        return false;
    }
}
