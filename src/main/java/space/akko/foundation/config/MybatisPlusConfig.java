package space.akko.foundation.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import space.akko.foundation.utils.SecurityUtils;

import java.time.LocalDateTime;

/**
 * MyBatis Plus配置
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Configuration
@EnableTransactionManagement
public class MybatisPlusConfig {

    /**
     * MyBatis Plus拦截器
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        
        // 分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor(DbType.POSTGRE_SQL);
        paginationInterceptor.setMaxLimit(1000L); // 设置最大分页数量
        paginationInterceptor.setOverflow(false); // 溢出总页数后是否进行处理
        interceptor.addInnerInterceptor(paginationInterceptor);
        
        // 乐观锁插件
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        
        return interceptor;
    }

    /**
     * 元数据处理器 - 自动填充
     */
    @Bean
    public MetaObjectHandler metaObjectHandler() {
        return new MetaObjectHandler() {
            @Override
            public void insertFill(MetaObject metaObject) {
                log.debug("开始插入填充...");
                
                LocalDateTime now = LocalDateTime.now();
                Long currentUserId = getCurrentUserId();
                
                // 填充创建时间
                this.strictInsertFill(metaObject, "createdAt", LocalDateTime.class, now);
                // 填充更新时间
                this.strictInsertFill(metaObject, "updatedAt", LocalDateTime.class, now);
                // 填充创建者
                this.strictInsertFill(metaObject, "createdBy", Long.class, currentUserId);
                // 填充更新者
                this.strictInsertFill(metaObject, "updatedBy", Long.class, currentUserId);
                // 填充删除标记
                this.strictInsertFill(metaObject, "isDeleted", Boolean.class, false);
                // 填充版本号
                this.strictInsertFill(metaObject, "version", Integer.class, 0);
            }

            @Override
            public void updateFill(MetaObject metaObject) {
                log.debug("开始更新填充...");
                
                LocalDateTime now = LocalDateTime.now();
                Long currentUserId = getCurrentUserId();
                
                // 填充更新时间
                this.strictUpdateFill(metaObject, "updatedAt", LocalDateTime.class, now);
                // 填充更新者
                this.strictUpdateFill(metaObject, "updatedBy", Long.class, currentUserId);
            }
            
            /**
             * 获取当前用户ID
             */
            private Long getCurrentUserId() {
                try {
                    return SecurityUtils.getCurrentUserId();
                } catch (Exception e) {
                    log.debug("获取当前用户ID失败，使用系统用户ID: {}", e.getMessage());
                    return 0L; // 系统用户ID
                }
            }
        };
    }
}
