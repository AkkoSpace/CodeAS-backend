package space.akko.foundation.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * 测试配置类
 * 
 * @author akko
 * @since 1.0.0
 */
@TestConfiguration
@Profile("test")
public class TestConfiguration {

    private RedisServer redisServer;

    @PostConstruct
    public void startRedis() {
        try {
            redisServer = new RedisServer(6379);
            redisServer.start();
        } catch (Exception e) {
            // Redis可能已经在运行，忽略错误
        }
    }

    @PreDestroy
    public void stopRedis() {
        if (redisServer != null && redisServer.isActive()) {
            redisServer.stop();
        }
    }
}
