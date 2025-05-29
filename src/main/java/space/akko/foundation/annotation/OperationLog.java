package space.akko.foundation.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 操作日志注解
 * 
 * @author akko
 * @since 1.0.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OperationLog {

    /**
     * 操作类型
     */
    String operationType() default "";

    /**
     * 操作名称
     */
    String operationName() default "";

    /**
     * 资源类型
     */
    String resourceType() default "";

    /**
     * 是否记录请求参数
     */
    boolean includeRequestParams() default true;

    /**
     * 是否记录请求体
     */
    boolean includeRequestBody() default false;

    /**
     * 是否记录响应体
     */
    boolean includeResponseBody() default false;

    /**
     * 是否异步记录
     */
    boolean async() default true;
}
