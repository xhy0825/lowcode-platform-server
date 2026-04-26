package com.lowcode.platform.form.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.form.entity.FormDefinition;
import com.lowcode.platform.form.entity.FormData;
import com.lowcode.platform.form.mapper.FormDefinitionMapper;
import com.lowcode.platform.form.mapper.FormDataMapper;
import com.lowcode.platform.form.service.FormDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 表单数据服务实现
 * 支持两种存储方式：
 * 1. 关联数据模型：数据存储在动态创建的表中
 * 2. 无关联模型：数据存储在 form_data_template 表中（JSON格式）
 */
@Service
@RequiredArgsConstructor
public class FormDataServiceImpl implements FormDataService {

    private final FormDefinitionMapper formMapper;
    private final FormDataMapper formDataMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long submitFormData(Long formId, Map<String, Object> data) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }
        if (form.getStatus() != 1) {
            throw new BusinessException("表单未发布");
        }

        // 关联了数据模型，存储到动态表
        if (form.getModelId() != null) {
            return insertToDynamicTable(form.getModelId(), data);
        }

        // 未关联模型，存储到模板表（JSON格式）
        FormData formData = new FormData();
        formData.setTenantId(form.getTenantId());
        formData.setFormDefinitionId(formId);
        formData.setData(JSON.toJSONString(data));
        formDataMapper.insert(formData);
        return formData.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFormData(Long formId, Long dataId, Map<String, Object> data) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        if (form.getModelId() != null) {
            return updateToDynamicTable(form.getModelId(), dataId, data);
        }

        FormData formData = formDataMapper.selectById(dataId);
        if (formData == null) {
            throw new BusinessException("数据不存在");
        }
        formData.setData(JSON.toJSONString(data));
        return formDataMapper.updateById(formData) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFormData(Long formId, Long dataId) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        if (form.getModelId() != null) {
            return deleteFromDynamicTable(form.getModelId(), dataId);
        }

        return formDataMapper.deleteById(dataId) > 0;
    }

    @Override
    public FormData getFormData(Long formId, Long dataId) {
        return formDataMapper.selectById(dataId);
    }

    @Override
    public Page<Map<String, Object>> queryFormDataPage(Long formId, Map<String, Object> query, int pageNum, int pageSize) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);

        if (form.getModelId() != null) {
            // 从动态表查询
            return queryFromDynamicTable(form.getModelId(), query, pageNum, pageSize);
        }

        // 从模板表查询
        Page<FormData> dataPage = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<FormData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(FormData::getFormDefinitionId, formId);
        wrapper.orderByDesc(FormData::getCreatedTime);
        formDataMapper.selectPage(dataPage, wrapper);

        // 转换结果
        List<Map<String, Object>> list = dataPage.getRecords().stream()
                .map(fd -> {
                    Map<String, Object> map = JSON.parseObject(fd.getData(), Map.class);
                    map.put("id", fd.getId());
                    map.put("createdTime", fd.getCreatedTime());
                    return map;
                })
                .collect(Collectors.toList());

        page.setRecords(list);
        page.setTotal(dataPage.getTotal());
        return page;
    }

    @Override
    public List<Map<String, Object>> queryFormDataList(Long formId, Map<String, Object> query) {
        FormDefinition form = formMapper.selectById(formId);
        if (form == null) {
            throw new BusinessException("表单不存在");
        }

        if (form.getModelId() != null) {
            return queryListFromDynamicTable(form.getModelId(), query);
        }

        List<FormData> dataList = formDataMapper.selectByFormDefinitionId(formId, 0, 1000);
        return dataList.stream()
                .map(fd -> {
                    Map<String, Object> map = JSON.parseObject(fd.getData(), Map.class);
                    map.put("id", fd.getId());
                    return map;
                })
                .collect(Collectors.toList());
    }

    // ===================== 动态表操作 =====================

    /**
     * 插入数据到动态表
     */
    private Long insertToDynamicTable(Long modelId, Map<String, Object> data) {
        // 获取表名
        String tableName = getTableNameByModelId(modelId);
        if (tableName == null) {
            throw new BusinessException("数据模型不存在或未发布");
        }

        // 构建插入SQL
        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append(" (");
        StringBuilder values = new StringBuilder(" VALUES (");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String column = entry.getKey().toLowerCase().replaceAll("[^a-z0-9]", "_");
            sql.append(column).append(",");
            values.append("?,");
            params.add(entry.getValue());
        }
        sql.append("created_by,created_time");
        values.append("'system',NOW()");

        sql.append(")").append(values).append(")");

        jdbcTemplate.update(sql.toString(), params.toArray());

        // 获取插入的ID
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id;
    }

    /**
     * 更新动态表数据
     */
    private boolean updateToDynamicTable(Long modelId, Long dataId, Map<String, Object> data) {
        String tableName = getTableNameByModelId(modelId);
        if (tableName == null) {
            throw new BusinessException("数据模型不存在");
        }

        StringBuilder sql = new StringBuilder("UPDATE ").append(tableName).append(" SET ");
        List<Object> params = new ArrayList<>();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            String column = entry.getKey().toLowerCase().replaceAll("[^a-z0-9]", "_");
            sql.append(column).append(" = ?,");
            params.add(entry.getValue());
        }
        sql.append("updated_by = 'system', updated_time = NOW() WHERE id = ?");
        params.add(dataId);

        return jdbcTemplate.update(sql.toString(), params.toArray()) > 0;
    }

    /**
     * 删除动态表数据
     */
    private boolean deleteFromDynamicTable(Long modelId, Long dataId) {
        String tableName = getTableNameByModelId(modelId);
        if (tableName == null) {
            throw new BusinessException("数据模型不存在");
        }

        String sql = "DELETE FROM " + tableName + " WHERE id = ?";
        return jdbcTemplate.update(sql, dataId) > 0;
    }

    /**
     * 从动态表分页查询
     */
    private Page<Map<String, Object>> queryFromDynamicTable(Long modelId, Map<String, Object> query, int pageNum, int pageSize) {
        String tableName = getTableNameByModelId(modelId);
        if (tableName == null) {
            throw new BusinessException("数据模型不存在");
        }

        // 构建查询SQL
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        StringBuilder countSql = new StringBuilder("SELECT COUNT(*) FROM ").append(tableName);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            sql.append(" WHERE ");
            countSql.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (!first) {
                    sql.append(" AND ");
                    countSql.append(" AND ");
                }
                String column = entry.getKey().toLowerCase().replaceAll("[^a-z0-9]", "_");
                sql.append(column).append(" = ?");
                countSql.append(column).append(" = ?");
                params.add(entry.getValue());
                first = false;
            }
        }

        // 查询总数
        Long total = jdbcTemplate.queryForObject(countSql.toString(), params.toArray(), Long.class);

        // 分页查询
        int offset = (pageNum - 1) * pageSize;
        sql.append(" LIMIT ").append(offset).append(", ").append(pageSize);

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql.toString(), params.toArray());

        Page<Map<String, Object>> page = new Page<>(pageNum, pageSize);
        page.setRecords(list);
        page.setTotal(total);
        return page;
    }

    /**
     * 从动态表查询列表
     */
    private List<Map<String, Object>> queryListFromDynamicTable(Long modelId, Map<String, Object> query) {
        String tableName = getTableNameByModelId(modelId);
        if (tableName == null) {
            throw new BusinessException("数据模型不存在");
        }

        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(tableName);
        List<Object> params = new ArrayList<>();

        if (query != null && !query.isEmpty()) {
            sql.append(" WHERE ");
            boolean first = true;
            for (Map.Entry<String, Object> entry : query.entrySet()) {
                if (!first) sql.append(" AND ");
                String column = entry.getKey().toLowerCase().replaceAll("[^a-z0-9]", "_");
                sql.append(column).append(" = ?");
                params.add(entry.getValue());
                first = false;
            }
        }

        return jdbcTemplate.queryForList(sql.toString(), params.toArray());
    }

    /**
     * 根据模型ID获取表名
     */
    private String getTableNameByModelId(Long modelId) {
        // TODO: 通过Feign调用data-service获取模型信息
        // 临时方案：直接查询data_model表
        String sql = "SELECT table_name FROM data_model WHERE id = ? AND status = 1 AND del_flag = 0";
        try {
            return jdbcTemplate.queryForObject(sql, String.class, modelId);
        } catch (Exception e) {
            return null;
        }
    }
}