package space.akko.foundation.cache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 多级缓存管理器
 * 实现L1（本地缓存）+ L2（Redis缓存）的多级缓存策略
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class MultiLevelCacheManager implements CacheManager {

    private final CacheManager l1CacheManager;
    private final CacheManager l2CacheManager;
    private final ConcurrentMap<String, Cache> cacheMap = new ConcurrentHashMap<>();

    @Override
    public Cache getCache(String name) {
        return cacheMap.computeIfAbsent(name, this::createMultiLevelCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }

    /**
     * 创建多级缓存
     */
    private Cache createMultiLevelCache(String name) {
        Cache l1Cache = l1CacheManager.getCache(name);
        Cache l2Cache = l2CacheManager.getCache(name);
        
        if (l1Cache == null || l2Cache == null) {
            log.warn("创建多级缓存失败，L1缓存: {}, L2缓存: {}", l1Cache != null, l2Cache != null);
            return l2Cache != null ? l2Cache : l1Cache;
        }
        
        log.debug("创建多级缓存: {}", name);
        return new MultiLevelCache(name, l1Cache, l2Cache);
    }
}
