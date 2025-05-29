package space.akko.foundation.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

import java.util.concurrent.Callable;

/**
 * 多级缓存实现
 * L1: 本地缓存（Caffeine）- 快速访问
 * L2: 分布式缓存（Redis）- 数据共享
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class MultiLevelCache implements Cache {

    private final String name;
    private final Cache l1Cache;
    private final Cache l2Cache;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    @Override
    public ValueWrapper get(Object key) {
        // 先从L1缓存获取
        ValueWrapper l1Value = l1Cache.get(key);
        if (l1Value != null) {
            log.debug("L1缓存命中: cache={}, key={}", name, key);
            return l1Value;
        }

        // L1缓存未命中，从L2缓存获取
        ValueWrapper l2Value = l2Cache.get(key);
        if (l2Value != null) {
            log.debug("L2缓存命中: cache={}, key={}", name, key);
            // 将L2缓存的数据回写到L1缓存
            l1Cache.put(key, l2Value.get());
            return l2Value;
        }

        log.debug("缓存未命中: cache={}, key={}", name, key);
        return null;
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        // 先从L1缓存获取
        T l1Value = l1Cache.get(key, type);
        if (l1Value != null) {
            log.debug("L1缓存命中: cache={}, key={}, type={}", name, key, type.getSimpleName());
            return l1Value;
        }

        // L1缓存未命中，从L2缓存获取
        T l2Value = l2Cache.get(key, type);
        if (l2Value != null) {
            log.debug("L2缓存命中: cache={}, key={}, type={}", name, key, type.getSimpleName());
            // 将L2缓存的数据回写到L1缓存
            l1Cache.put(key, l2Value);
            return l2Value;
        }

        log.debug("缓存未命中: cache={}, key={}, type={}", name, key, type.getSimpleName());
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        // 先从L1缓存获取
        T l1Value = l1Cache.get(key, (Class<T>) null);
        if (l1Value != null) {
            log.debug("L1缓存命中: cache={}, key={}", name, key);
            return l1Value;
        }

        // L1缓存未命中，从L2缓存获取
        T l2Value = l2Cache.get(key, (Class<T>) null);
        if (l2Value != null) {
            log.debug("L2缓存命中: cache={}, key={}", name, key);
            // 将L2缓存的数据回写到L1缓存
            l1Cache.put(key, l2Value);
            return l2Value;
        }

        // 两级缓存都未命中，执行valueLoader
        try {
            T value = valueLoader.call();
            if (value != null) {
                put(key, value);
            }
            return value;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load value for key: " + key, e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        log.debug("缓存写入: cache={}, key={}", name, key);
        // 同时写入L1和L2缓存
        l1Cache.put(key, value);
        l2Cache.put(key, value);
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        // 先检查L1缓存
        ValueWrapper existingValue = l1Cache.get(key);
        if (existingValue != null) {
            return existingValue;
        }

        // 检查L2缓存
        existingValue = l2Cache.get(key);
        if (existingValue != null) {
            // 将L2缓存的数据回写到L1缓存
            l1Cache.put(key, existingValue.get());
            return existingValue;
        }

        // 两级缓存都没有，写入新值
        put(key, value);
        return null;
    }

    @Override
    public void evict(Object key) {
        log.debug("缓存清除: cache={}, key={}", name, key);
        // 同时清除L1和L2缓存
        l1Cache.evict(key);
        l2Cache.evict(key);
    }

    @Override
    public void clear() {
        log.debug("缓存全部清除: cache={}", name);
        // 同时清除L1和L2缓存
        l1Cache.clear();
        l2Cache.clear();
    }
}
