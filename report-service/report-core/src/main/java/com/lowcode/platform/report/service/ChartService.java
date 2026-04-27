package com.lowcode.platform.report.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.report.entity.ChartDefinition;

import java.util.List;
import java.util.Map;

/**
 * 图表服务接口
 */
public interface ChartService {

    /**
     * 分页查询图表
     */
    Page<ChartDefinition> listPage(int pageNum, int pageSize, String chartType);

    /**
     * 获取图表详情
     */
    ChartDefinition getById(Long id);

    /**
     * 创建图表
     */
    Long create(ChartDefinition chart);

    /**
     * 更新图表
     */
    boolean update(ChartDefinition chart);

    /**
     * 发布图表
     */
    boolean publish(Long chartId);

    /**
     * 删除图表
     */
    boolean delete(Long chartId);

    /**
     * 获取图表数据
     */
    Map<String, Object> getChartData(Long chartId, Map<String, Object> params);

    /**
     * 预览图表数据（不保存）
     */
    Map<String, Object> previewData(ChartDefinition chart, Map<String, Object> params);
}