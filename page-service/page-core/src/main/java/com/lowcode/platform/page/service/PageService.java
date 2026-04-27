package com.lowcode.platform.page.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lowcode.platform.page.entity.PageComponent;
import com.lowcode.platform.page.entity.PageDefinition;

import java.util.List;
import java.util.Map;

/**
 * 页面服务接口
 */
public interface PageService {

    /**
     * 分页查询页面
     */
    Page<PageDefinition> listPage(int pageNum, int pageSize, String pageType);

    /**
     * 获取页面详情（含组件）
     */
    Map<String, Object> getDetail(Long id);

    /**
     * 创建页面
     */
    Long create(PageDefinition page);

    /**
     * 更新页面
     */
    boolean update(PageDefinition page);

    /**
     * 发布页面
     */
    boolean publish(Long pageId);

    /**
     * 删除页面
     */
    boolean delete(Long pageId);

    /**
     * 获取页面组件列表
     */
    List<PageComponent> getComponents(Long pageId);

    /**
     * 更新页面组件
     */
    boolean updateComponents(Long pageId, List<PageComponent> components);

    /**
     * 更新页面布局
     */
    boolean updateLayout(Long pageId, String layoutConfig);

    /**
     * 根据编码查询页面
     */
    PageDefinition getByCode(String pageCode);
}