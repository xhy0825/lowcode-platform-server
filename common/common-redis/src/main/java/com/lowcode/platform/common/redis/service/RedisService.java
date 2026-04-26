package com.lowcode.platform.common.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Redis 服务
 */
@Service
public class RedisService {

    private final RedisTemplate<String, Object> redisTemplate;

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // ==================== String ====================

    /** 设置值 */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /** 设置值并过期时间 */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /** 获取值 */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /** 删除 */
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /** 批量删除 */
    public Long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /** 是否存在 */
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /** 设置过期时间 */
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /** 获取过期时间 */
    public Long getExpire(String key) {
        return redisTemplate.getExpire(key);
    }

    // ==================== Hash ====================

    /** Hash设置 */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /** Hash获取 */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /** Hash删除 */
    public Long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /** Hash获取所有 */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /** Hash是否存在 */
    public Boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ==================== List ====================

    /** List左推 */
    public Long lPush(String key, Object value) {
        return redisTemplate.opsForList().leftPush(key, value);
    }

    /** List右推 */
    public Long rPush(String key, Object value) {
        return redisTemplate.opsForList().rightPush(key, value);
    }

    /** List左弹 */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /** List右弹 */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /** List范围 */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    // ==================== Set ====================

    /** Set添加 */
    public Long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /** Set移除 */
    public Long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /** Set获取所有 */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /** Set是否存在 */
    public Boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /** Set大小 */
    public Long sSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }
}