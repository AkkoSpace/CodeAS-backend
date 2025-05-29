package space.akko.foundation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 权限验证注解
 * 
 * @author akko
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequirePermission {

    /**
     * 需要的权限
     */
    String[] value() default {};

    /**
     * 权限验证模式
     */
    Mode mode() default Mode.ALL;

    /**
     * 权限验证模式
     */
    enum Mode {
        /**
         * 需要所有权限
         */
        ALL,
        
        /**
         * 需要任意一个权限
         */
        ANY
    }
}
