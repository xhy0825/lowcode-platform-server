package com.lowcode.platform.data.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.data.entity.DataModel;
import com.lowcode.platform.data.entity.DataModelField;

import java.util.List;

/**
 * 数据模型服务接口
 */
public interface DataModelService {

    /** 分页查询 */
    Page<DataModel> selectPage(Page<DataModel> page, DataModel query);

    /** 根据ID查询 */
    DataModel getById(Long id);

    /** 根据编码查询 */
    DataModel selectByModelCode(String modelCode);

    /** 创建模型 */
    boolean createModel(DataModel model);

    /** 更新字段配置 */
    boolean updateFields(Long modelId, List<DataModelField> fields);

    /** 生成DDL */
    String generateDdl(Long modelId);

    /** 执行建表 */
    boolean executeDdl(Long modelId);

    /** 预览表结构 */
    String previewTable(Long modelId);

    /** 发布模型 */
    boolean publishModel(Long modelId);

    /** 删除模型 */
    boolean deleteModel(Long modelId);
}