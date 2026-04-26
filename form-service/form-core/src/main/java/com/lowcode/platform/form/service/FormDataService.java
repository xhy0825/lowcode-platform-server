package com.lowcode.platform.form.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.form.entity.FormData;

import java.util.Map;

/**
 * 表单数据服务接口
 */
public interface FormDataService {

    /** 提交表单数据 */
    Long submitFormData(Long formId, Map<String, Object> data);

    /** 更新表单数据 */
    boolean updateFormData(Long formId, Long dataId, Map<String, Object> data);

    /** 删除表单数据 */
    boolean deleteFormData(Long formId, Long dataId);

    /** 查询表单数据 */
    FormData getFormData(Long formId, Long dataId);

    /** 分页查询表单数据 */
    Page<Map<String, Object>> queryFormDataPage(Long formId, Map<String, Object> query, int pageNum, int pageSize);

    /** 查询表单数据列表 */
    java.util.List<Map<String, Object>> queryFormDataList(Long formId, Map<String, Object> query);
}