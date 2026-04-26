package com.lowcode.platform.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lowcode.platform.system.entity.SysDictData;

import java.util.List;

/**
 * 字典数据服务接口
 */
public interface SysDictDataService extends IService<SysDictData> {

    /** 根据字典类型查询 */
    List<SysDictData> selectByDictType(String dictType);

    /** 刷新字典缓存 */
    void refreshDictCache(String dictType);
}