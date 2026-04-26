package com.lowcode.platform.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.common.core.result.PageResult;
import com.lowcode.platform.system.entity.SysTenant;

import java.util.List;

/**
 * 租户服务接口
 */
public interface SysTenantService extends IService<SysTenant> {

    /**
     * 分页查询租户
     */
    PageResult<SysTenant> queryPageList(SysTenant query, Integer pageNum, Integer pageSize);

    /**
     * 根据租户编码查询
     */
    SysTenant getByTenantCode(String tenantCode);

    /**
     * 创建租户（含创建Schema和初始用户）
     */
    Long createTenant(SysTenant tenant);

    /**
     * 更新租户
     */
    void updateTenant(SysTenant tenant);

    /**
     * 删除租户（逻辑删除）
     */
    void deleteTenant(Long id);

    /**
     * 启用/禁用租户
     */
    void updateStatus(Long id, Integer status);

    /**
     * 检查租户编码是否存在
     */
    boolean checkTenantCodeExists(String tenantCode, Long excludeId);

    /**
     * 获取所有启用的租户
     */
    List<SysTenant> listEnabledTenants();

    /**
     * 初始化租户数据（创建Schema、初始用户、角色等）
     */
    void initTenantData(Long tenantId, String adminUsername, String adminPassword);
}