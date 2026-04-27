package com.lowcode.platform.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.report.entity.DashboardDefinition;

import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 分页查询仪表盘
     */
    Page<DashboardDefinition> listPage(int pageNum, int pageSize);

    /**
     * 获取仪表盘详情
     */
    DashboardDefinition getById(Long id);

    /**
     * 创建仪表盘
     */
    Long create(DashboardDefinition dashboard);

    /**
     * 更新仪表盘
     */
    boolean update(DashboardDefinition dashboard);

    /**
     * 发布仪表盘
     */
    boolean publish(Long dashboardId);

    /**
     * 删除仪表盘
     */
    boolean delete(Long dashboardId);

    /**
     * 获取仪表盘完整数据（包含所有图表数据）
     */
    Map<String, Object> getDashboardData(Long dashboardId, Map<String, Object> params);

    /**
     * 更新图表布局
     */
    boolean updateLayout(Long dashboardId, String layoutConfig);
}