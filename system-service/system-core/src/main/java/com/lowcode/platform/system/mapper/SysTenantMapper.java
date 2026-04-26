package com.lowcode.platform.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.system.entity.SysTenant;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 租户Mapper
 */
@Mapper
public interface SysTenantMapper extends BaseMapper<SysTenant> {

    /**
     * 根据租户编码查询
     */
    SysTenant selectByTenantCode(@Param("tenantCode") String tenantCode);

    /**
     * 检查租户编码是否存在
     */
    int checkTenantCodeExists(@Param("tenantCode") String tenantCode, @Param("excludeId") Long excludeId);
}