package com.lowcode.platform.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lowcode.platform.common.redis.service.RedisService;
import com.lowcode.platform.system.entity.SysDictData;
import com.lowcode.platform.system.mapper.SysDictDataMapper;
import com.lowcode.platform.system.service.SysDictDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 字典数据服务实现
 */
@Service
@RequiredArgsConstructor
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    private final SysDictDataMapper dictDataMapper;
    private final RedisService redisService;

    private static final String DICT_CACHE_KEY = "dict:data:";
    private static final long DICT_CACHE_EXPIRE = 3600;

    @Override
    public List<SysDictData> selectByDictType(String dictType) {
        // 先从缓存获取
        String cacheKey = DICT_CACHE_KEY + dictType;
        List<SysDictData> cached = (List<SysDictData>) redisService.get(cacheKey);
        if (cached != null) {
            return cached;
        }
        // 从数据库查询
        List<SysDictData> list = dictDataMapper.selectByDictType(dictType);
        // 写入缓存
        redisService.set(cacheKey, list, DICT_CACHE_EXPIRE, TimeUnit.SECONDS);
        return list;
    }

    @Override
    public void refreshDictCache(String dictType) {
        String cacheKey = DICT_CACHE_KEY + dictType;
        redisService.delete(cacheKey);
        // 重新加载缓存
        selectByDictType(dictType);
    }
}