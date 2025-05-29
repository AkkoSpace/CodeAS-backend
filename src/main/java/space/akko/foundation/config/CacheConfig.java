package space.akko.foundation.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import space.akko.foundation.cache.MultiLevelCacheManager;
import space.akko.foundation.constant.CacheConstants;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * 缓存配置
 *
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * L1缓存管理器（Caffeine本地缓存）
     */
    @Bean(CacheConstants.L1_CACHE_NAME)
    public CacheManager l1CacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats());
        return cacheManager;
    }

    /**
     * L2缓存管理器（Redis分布式缓存）
     */
    @Bean(CacheConstants.L2_CACHE_NAME)
    public CacheManager l2CacheManager(RedisConnectionFactory redisConnectionFactory) {
        try {
            RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                    .entryTtl(Duration.ofMinutes(30))
                    .serializeKeysWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(new StringRedisSerializer()))
                    .serializeValuesWith(RedisSerializationContext.SerializationPair
                            .fromSerializer(jackson2JsonRedisSerializer()))
                    .disableCachingNullValues();

            return RedisCacheManager.builder(redisConnectionFactory)
                    .cacheDefaults(config)
                    .build();
        } catch (Exception e) {
            log.warn("Redis缓存管理器创建失败，使用本地缓存作为备选: {}", e.getMessage());
            // 如果Redis不可用，返回本地缓存作为备选
            CaffeineCacheManager fallbackManager = new CaffeineCacheManager();
            fallbackManager.setCaffeine(Caffeine.newBuilder()
                    .maximumSize(500)
                    .expireAfterWrite(30, TimeUnit.MINUTES)
                    .recordStats());
            return fallbackManager;
        }
    }

    /**
     * 多级缓存管理器
     */
    @Bean(CacheConstants.MULTI_LEVEL_CACHE_NAME)
    @Primary
    @ConditionalOnProperty(name = "platform.cache.multi-level.enabled", havingValue = "true", matchIfMissing = true)
    public CacheManager multiLevelCacheManager(@Qualifier(CacheConstants.L1_CACHE_NAME) CacheManager l1CacheManager,
                                              @Qualifier(CacheConstants.L2_CACHE_NAME) CacheManager l2CacheManager) {
        log.info("启用多级缓存管理器");
        return new MultiLevelCacheManager(l1CacheManager, l2CacheManager);
    }

    /**
     * Redis模板
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // 使用Jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        template.setValueSerializer(jackson2JsonRedisSerializer());
        template.setHashValueSerializer(jackson2JsonRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Jackson2JsonRedisSerializer
     */
    private Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
                ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        objectMapper.registerModule(new JavaTimeModule());

        // 使用新的构造函数，避免使用deprecated的setObjectMapper方法
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }
}
