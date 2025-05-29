package space.akko.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.mock;

/**
 * 测试配置类
 * 提供测试环境所需的Mock Bean
 * 
 * @author akko
 * @since 1.0.0
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * 提供Mock的RedisConnectionFactory
     */
    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        return mock(RedisConnectionFactory.class);
    }

    /**
     * 提供Mock的RedisTemplate
     */
    @Bean
    @Primary
    public RedisTemplate<String, Object> redisTemplate() {
        return mock(RedisTemplate.class);
    }
}
