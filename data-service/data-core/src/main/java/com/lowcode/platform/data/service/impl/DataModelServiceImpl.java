package com.lowcode.platform.data.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.common.core.exception.BusinessException;
import com.lowcode.platform.data.entity.DataModel;
import com.lowcode.platform.data.entity.DataModelField;
import com.lowcode.platform.data.mapper.DataModelMapper;
import com.lowcode.platform.data.mapper.DataModelFieldMapper;
import com.lowcode.platform.data.service.DataModelService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据模型服务实现
 */
@Service
@RequiredArgsConstructor
public class DataModelServiceImpl implements DataModelService {

    private final DataModelMapper modelMapper;
    private final DataModelFieldMapper fieldMapper;
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Page<DataModel> selectPage(Page<DataModel> page, DataModel query) {
        LambdaQueryWrapper<DataModel> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DataModel::getDelFlag, 0);
        if (StringUtils.hasText(query.getModelName())) {
            wrapper.like(DataModel::getModelName, query.getModelName());
        }
        if (StringUtils.hasText(query.getModelCode())) {
            wrapper.like(DataModel::getModelCode, query.getModelCode());
        }
        wrapper.orderByDesc(DataModel::getCreatedTime);
        return modelMapper.selectPage(page, wrapper);
    }

    @Override
    public DataModel getById(Long id) {
        DataModel model = modelMapper.selectById(id);
        if (model != null) {
            model.setFields(modelMapper.selectFieldsByModelId(id));
        }
        return model;
    }

    @Override
    public DataModel selectByModelCode(String modelCode) {
        return modelMapper.selectByModelCode(modelCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createModel(DataModel model) {
        // 检查编码是否重复
        DataModel exist = modelMapper.selectByModelCode(model.getModelCode());
        if (exist != null) {
            throw new BusinessException("模型编码已存在");
        }
        model.setStatus(0);
        model.setVersion(1);
        model.setDelFlag(0);
        return modelMapper.insert(model) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateFields(Long modelId, List<DataModelField> fields) {
        DataModel model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        // 删除原有字段
        fieldMapper.deleteByModelId(modelId);
        // 保存新字段
        if (fields != null && !fields.isEmpty()) {
            for (DataModelField field : fields) {
                field.setModelId(modelId);
                field.setDelFlag(0);
            }
            fieldMapper.batchInsert(fields);
        }
        return true;
    }

    @Override
    public String generateDdl(Long modelId) {
        DataModel model = getById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        List<DataModelField> fields = model.getFields();
        if (fields == null || fields.isEmpty()) {
            throw new BusinessException("请先配置字段");
        }
        return buildCreateTableSql(model.getTableName(), fields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean executeDdl(Long modelId) {
        DataModel model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        // 生成DDL
        String ddl = generateDdl(modelId);
        // 执行建表
        try {
            jdbcTemplate.execute(ddl);
            // 更新状态为已发布
            model.setStatus(1);
            modelMapper.updateById(model);
            return true;
        } catch (Exception e) {
            throw new BusinessException("建表失败: " + e.getMessage());
        }
    }

    @Override
    public String previewTable(Long modelId) {
        DataModel model = getById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        return generateDdl(modelId);
    }

    @Override
    public boolean publishModel(Long modelId) {
        return executeDdl(modelId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteModel(Long modelId) {
        DataModel model = modelMapper.selectById(modelId);
        if (model == null) {
            throw new BusinessException("模型不存在");
        }
        if (model.getStatus() == 1) {
            throw new BusinessException("已发布的模型不能删除");
        }
        model.setDelFlag(1);
        fieldMapper.deleteByModelId(modelId);
        return modelMapper.updateById(model) > 0;
    }

    /**
     * 构建建表SQL
     */
    private String buildCreateTableSql(String tableName, List<DataModelField> fields) {
        StringBuilder sql = new StringBuilder();
        sql.append("CREATE TABLE IF NOT EXISTS ").append(tableName).append(" (\n");

        List<String> primaryKeys = new ArrayList<>();
        List<String> indexes = new ArrayList<>();

        for (int i = 0; i < fields.size(); i++) {
            DataModelField field = fields.get(i);
            sql.append("    ").append(field.getColumnName()).append(" ");

            // 字段类型映射
            String columnType = mapFieldType(field);
            sql.append(columnType);

            // 是否必填
            if (field.getIsRequired() == 1) {
                sql.append(" NOT NULL");
            }

            // 默认值
            if (StringUtils.hasText(field.getDefaultValue())) {
                sql.append(" DEFAULT '").append(field.getDefaultValue()).append("'");
            }

            // 注释
            sql.append(" COMMENT '").append(field.getFieldName()).append("'");

            if (i < fields.size() - 1) {
                sql.append(",\n");
            }

            // 收集主键和索引
            if (field.getIsPrimary() == 1) {
                primaryKeys.add(field.getColumnName());
            }
            if (field.getIsIndexed() == 1) {
                indexes.add(field.getColumnName());
            }
        }

        // 主键
        if (!primaryKeys.isEmpty()) {
            sql.append(",\n    PRIMARY KEY (");
            sql.append(String.join(", ", primaryKeys));
            sql.append(")");
        }

        sql.append("\n) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='动态创建的表';\n");

        // 索引
        for (String column : indexes) {
            sql.append("CREATE INDEX idx_").append(tableName).append("_").append(column)
               .append(" ON ").append(tableName).append(" (").append(column).append(");\n");
        }

        return sql.toString();
    }

    /**
     * 字段类型映射
     */
    private String mapFieldType(DataModelField field) {
        String fieldType = field.getFieldType();
        switch (fieldType) {
            case "string":
                int length = field.getLength() != null ? field.getLength() : 255;
                return "VARCHAR(" + length + ")";
            case "text":
                return "TEXT";
            case "textarea":
                return "TEXT";
            case "integer":
                return "INT";
            case "number":
                int precision = field.getPrecision() != null ? field.getPrecision() : 10;
                int scale = field.getScale() != null ? field.getScale() : 2;
                return "DECIMAL(" + precision + "," + scale + ")";
            case "boolean":
                return "TINYINT(1)";
            case "date":
                return "DATE";
            case "datetime":
                return "DATETIME";
            case "time":
                return "TIME";
            case "select":
                return "VARCHAR(50)";
            case "file":
                return "VARCHAR(500)";
            case "image":
                return "VARCHAR(500)";
            case "json":
                return "JSON";
            default:
                return "VARCHAR(255)";
        }
    }
}