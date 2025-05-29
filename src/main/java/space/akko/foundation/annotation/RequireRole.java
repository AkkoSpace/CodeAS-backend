package space.akko.foundation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色验证注解
 * 
 * @author akko
 * @since 1.0.0
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequireRole {

    /**
     * 需要的角色
     */
    String[] value() default {};

    /**
     * 角色验证模式
     */
    Mode mode() default Mode.ALL;

    /**
     * 角色验证模式
     */
    enum Mode {
        /**
         * 需要所有角色
         */
        ALL,
        
        /**
         * 需要任意一个角色
         */
        ANY
    }
}
