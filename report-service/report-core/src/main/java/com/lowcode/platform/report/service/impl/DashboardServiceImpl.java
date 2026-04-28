package com.lowcode.platform.report.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.report.entity.ChartDefinition;
import com.lowcode.platform.report.entity.DashboardDefinition;
import com.lowcode.platform.report.mapper.ChartDefinitionMapper;
import com.lowcode.platform.report.mapper.DashboardDefinitionMapper;
import com.lowcode.platform.report.service.ChartService;
import com.lowcode.platform.report.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 仪表盘服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DashboardDefinitionMapper dashboardMapper;
    private final ChartDefinitionMapper chartMapper;
    private final ChartService chartService;

    @Override
    public Page<DashboardDefinition> listPage(int pageNum, int pageSize) {
        Page<DashboardDefinition> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<DashboardDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardDefinition::getDelFlag, 0);
        wrapper.orderByDesc(DashboardDefinition::getCreatedTime);
        return dashboardMapper.selectPage(page, wrapper);
    }

    @Override
    public DashboardDefinition getById(Long id) {
        return dashboardMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(DashboardDefinition dashboard) {
        // 检查编码是否重复
        LambdaQueryWrapper<DashboardDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DashboardDefinition::getDashboardCode, dashboard.getDashboardCode())
                .eq(DashboardDefinition::getDelFlag, 0);
        if (dashboardMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("仪表盘编码已存在");
        }

        dashboard.setStatus(0);
        dashboard.setDelFlag(0);
        dashboard.setCreatedTime(LocalDateTime.now());
        dashboardMapper.insert(dashboard);
        return dashboard.getId();
    }

    @Override
    @Transactional
    public boolean update(DashboardDefinition dashboard) {
        DashboardDefinition exist = dashboardMapper.selectById(dashboard.getId());
        if (exist == null) {
            throw new BusinessException("仪表盘不存在");
        }
        dashboard.setUpdatedTime(LocalDateTime.now());
        return dashboardMapper.updateById(dashboard) > 0;
    }

    @Override
    @Transactional
    public boolean publish(Long dashboardId) {
        DashboardDefinition dashboard = dashboardMapper.selectById(dashboardId);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        dashboard.setStatus(1);
        dashboard.setUpdatedTime(LocalDateTime.now());
        return dashboardMapper.updateById(dashboard) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long dashboardId) {
        DashboardDefinition dashboard = dashboardMapper.selectById(dashboardId);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        dashboard.setDelFlag(1);
        return dashboardMapper.updateById(dashboard) > 0;
    }

    @Override
    public Map<String, Object> getDashboardData(Long dashboardId, Map<String, Object> params) {
        DashboardDefinition dashboard = dashboardMapper.selectById(dashboardId);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("dashboard", dashboard);

        // 解析布局配置，获取图表列表
        if (StringUtils.hasText(dashboard.getLayoutConfig())) {
            List<Map<String, Object>> layoutItems = JSON.parseArray(dashboard.getLayoutConfig(), Map.class);
            List<Map<String, Object>> chartDataList = new ArrayList<>();

            for (Map<String, Object> item : layoutItems) {
                Long chartId = Long.parseLong(item.get("chartId").toString());
                ChartDefinition chart = chartMapper.selectById(chartId);
                if (chart != null) {
                    Map<String, Object> chartData = new HashMap<>();
                    chartData.put("chartId", chartId);
                    chartData.put("chartName", chart.getChartName());
                    chartData.put("chartType", chart.getChartType());
                    chartData.put("position", item.get("position"));
                    chartData.put("size", item.get("size"));
                    chartData.put("data", chartService.getChartData(chartId, params));
                    chartDataList.add(chartData);
                }
            }

            result.put("charts", chartDataList);
        }

        return result;
    }

    @Override
    @Transactional
    public boolean updateLayout(Long dashboardId, String layoutConfig) {
        DashboardDefinition dashboard = dashboardMapper.selectById(dashboardId);
        if (dashboard == null) {
            throw new BusinessException("仪表盘不存在");
        }
        dashboard.setLayoutConfig(layoutConfig);
        dashboard.setUpdatedTime(LocalDateTime.now());
        return dashboardMapper.updateById(dashboard) > 0;
    }
}