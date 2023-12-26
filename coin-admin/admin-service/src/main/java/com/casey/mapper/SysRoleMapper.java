package com.casey.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.casey.domain.SysRole;

public interface SysRoleMapper extends BaseMapper<SysRole> {
    /**
     * 获取用户 code 的实现
     * @param userId
     * @return
     */
    String getUserRoleCode(Long userId);
}
