package space.akko.foundation.utils;

import cn.hutool.core.util.IdUtil;
import org.slf4j.MDC;

/**
 * 链路追踪工具类
 * 
 * @author akko
 * @since 1.0.0
 */
public final class TraceUtils {

    private TraceUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 追踪ID键名
     */
    public static final String TRACE_ID_KEY = "traceId";

    /**
     * 生成追踪ID
     */
    public static String generateTraceId() {
        return IdUtil.fastSimpleUUID();
    }

    /**
     * 设置追踪ID
     */
    public static void setTraceId(String traceId) {
        MDC.put(TRACE_ID_KEY, traceId);
    }

    /**
     * 获取追踪ID
     */
    public static String getTraceId() {
        return MDC.get(TRACE_ID_KEY);
    }

    /**
     * 清除追踪ID
     */
    public static void clearTraceId() {
        MDC.remove(TRACE_ID_KEY);
    }

    /**
     * 清除所有MDC
     */
    public static void clearAll() {
        MDC.clear();
    }
}
