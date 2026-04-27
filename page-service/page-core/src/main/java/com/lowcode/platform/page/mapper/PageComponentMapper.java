package com.lowcode.platform.page.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lowcode.platform.page.entity.PageComponent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 页面组件Mapper
 */
@Mapper
public interface PageComponentMapper extends BaseMapper<PageComponent> {

    /**
     * 根据页面ID查询组件列表
     */
    List<PageComponent> selectByPageId(@Param("pageId") Long pageId);

    /**
     * 根据页面ID删除组件
     */
    int deleteByPageId(@Param("pageId") Long pageId);
}