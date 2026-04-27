package com.lowcode.platform.report.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.report.entity.ChartDefinition;
import com.lowcode.platform.report.mapper.ChartDefinitionMapper;
import com.lowcode.platform.report.service.ChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 图表服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChartServiceImpl implements ChartService {

    private final ChartDefinitionMapper chartMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<ChartDefinition> listPage(int pageNum, int pageSize, String chartType) {
        Page<ChartDefinition> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ChartDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChartDefinition::getDelFlag, 0);
        if (StringUtils.hasText(chartType)) {
            wrapper.eq(ChartDefinition::getChartType, chartType);
        }
        wrapper.orderByDesc(ChartDefinition::getCreatedTime);
        return chartMapper.selectPage(page, wrapper);
    }

    @Override
    public ChartDefinition getById(Long id) {
        return chartMapper.selectById(id);
    }

    @Override
    @Transactional
    public Long create(ChartDefinition chart) {
        // 检查编码是否重复
        LambdaQueryWrapper<ChartDefinition> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ChartDefinition::getChartCode, chart.getChartCode())
                .eq(ChartDefinition::getDelFlag, 0);
        if (chartMapper.selectCount(wrapper) > 0) {
            throw new BusinessException("图表编码已存在");
        }

        chart.setStatus(0);
        chart.setVersion(1);
        chart.setDelFlag(0);
        chart.setCreatedTime(LocalDateTime.now());
        chartMapper.insert(chart);
        return chart.getId();
    }

    @Override
    @Transactional
    public boolean update(ChartDefinition chart) {
        ChartDefinition exist = chartMapper.selectById(chart.getId());
        if (exist == null) {
            throw new BusinessException("图表不存在");
        }
        chart.setUpdatedTime(LocalDateTime.now());
        return chartMapper.updateById(chart) > 0;
    }

    @Override
    @Transactional
    public boolean publish(Long chartId) {
        ChartDefinition chart = chartMapper.selectById(chartId);
        if (chart == null) {
            throw new BusinessException("图表不存在");
        }
        chart.setStatus(1);
        chart.setUpdatedTime(LocalDateTime.now());
        return chartMapper.updateById(chart) > 0;
    }

    @Override
    @Transactional
    public boolean delete(Long chartId) {
        ChartDefinition chart = chartMapper.selectById(chartId);
        if (chart == null) {
            throw new BusinessException("图表不存在");
        }
        chart.setDelFlag(1);
        return chartMapper.updateById(chart) > 0;
    }

    @Override
    public Map<String, Object> getChartData(Long chartId, Map<String, Object> params) {
        ChartDefinition chart = chartMapper.selectById(chartId);
        if (chart == null) {
            throw new BusinessException("图表不存在");
        }
        return executeQuery(chart, params);
    }

    @Override
    public Map<String, Object> previewData(ChartDefinition chart, Map<String, Object> params) {
        return executeQuery(chart, params);
    }

    /**
     * 执行数据查询
     */
    private Map<String, Object> executeQuery(ChartDefinition chart, Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        result.put("chartType", chart.getChartType());

        try {
            // 解析查询配置
            Map<String, Object> queryConfig = JSON.parseObject(chart.getQueryConfig(), Map.class);
            String tableName = (String) queryConfig.get("tableName");

            if (!StringUtils.hasText(tableName)) {
                // 如果没有配置数据源，返回模拟数据
                result.put("data", generateMockData(chart));
                return result;
            }

            // 解析维度和指标配置
            List<String> dimensions = JSON.parseArray(chart.getDimensionConfig(), String.class);
            List<String> metrics = JSON.parseArray(chart.getMetricConfig(), String.class);

            // 构建查询SQL
            StringBuilder sql = new StringBuilder("SELECT ");

            // 添加维度字段
            if (dimensions != null && !dimensions.isEmpty()) {
                sql.append(String.join(", ", dimensions));
            }

            // 添加指标字段（聚合函数）
            if (metrics != null && !metrics.isEmpty()) {
                if (dimensions != null && !dimensions.isEmpty()) {
                    sql.append(", ");
                }
                for (int i = 0; i < metrics.size(); i++) {
                    String metric = metrics.get(i);
                    // 解析聚合函数配置
                    Map<String, Object> metricConfig = JSON.parseObject(metric, Map.class);
                    String field = (String) metricConfig.get("field");
                    String aggFunc = (String) metricConfig.getOrDefault("aggFunc", "SUM");
                    sql.append(aggFunc).append("(").append(field).append(") AS ").append(field).append("_").append(aggFunc.toLowerCase());
                    if (i < metrics.size() - 1) {
                        sql.append(", ");
                    }
                }
            }

            sql.append(" FROM ").append(tableName);

            // 添加过滤条件
            if (params != null && !params.isEmpty()) {
                sql.append(" WHERE ");
                List<String> conditions = new ArrayList<>();
                for (Map.Entry<String, Object> entry : params.entrySet()) {
                    conditions.add(entry.getKey() + " = '" + entry.getValue() + "'");
                }
                sql.append(String.join(" AND ", conditions));
            }

            // 添加分组
            if (dimensions != null && !dimensions.isEmpty()) {
                sql.append(" GROUP BY ").append(String.join(", ", dimensions));
            }

            // 执行查询
            List<Map<String, Object>> data = jdbcTemplate.queryForList(sql.toString());
            result.put("data", data);

        } catch (Exception e) {
            log.error("图表数据查询失败: {}", chart.getId(), e);
            // 返回模拟数据
            result.put("data", generateMockData(chart));
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 生成模拟数据（用于无数据源或查询失败时）
     */
    private List<Map<String, Object>> generateMockData(ChartDefinition chart) {
        List<Map<String, Object>> mockData = new ArrayList<>();
        String chartType = chart.getChartType();

        switch (chartType) {
            case "line":
            case "bar":
                // 时间序列数据
                String[] months = {"1月", "2月", "3月", "4月", "5月", "6月"};
                for (String month : months) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("dimension", month);
                    item.put("value", 100 + Math.random() * 200);
                    mockData.add(item);
                }
                break;

            case "pie":
                // 分类占比数据
                String[] categories = {"类别A", "类别B", "类别C", "类别D"};
                for (String category : categories) {
                    Map<String, Object> item = new HashMap<>();
                    item.put("name", category);
                    item.put("value", 50 + Math.random() * 150);
                    mockData.add(item);
                }
                break;

            case "gauge":
                // 仪表盘数据
                Map<String, Object> gauge = new HashMap<>();
                gauge.put("value", 75 + Math.random() * 20);
                gauge.put("max", 100);
                mockData.add(gauge);
                break;

            case "scatter":
                // 散点数据
                for (int i = 0; i < 20; i++) {
                    Map<String, Object> point = new HashMap<>();
                    point.put("x", Math.random() * 100);
                    point.put("y", Math.random() * 100);
                    mockData.add(point);
                }
                break;

            default:
                // 默认表格数据
                for (int i = 0; i < 5; i++) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", i + 1);
                    row.put("name", "数据" + (i + 1));
                    row.put("value", 100 + i * 50);
                    mockData.add(row);
                }
        }

        return mockData;
    }
}