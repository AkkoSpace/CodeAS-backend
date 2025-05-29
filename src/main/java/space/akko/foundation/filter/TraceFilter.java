package space.akko.foundation.filter;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import space.akko.foundation.utils.TraceUtils;

import java.io.IOException;

/**
 * 请求追踪过滤器
 * 为每个请求生成唯一的追踪ID
 * 
 * @author akko
 * @since 1.0.0
 */
@Slf4j
@Component
@Order(1)
public class TraceFilter implements Filter {

    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        
        try {
            // 从请求头获取追踪ID，如果没有则生成新的
            String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
            if (StrUtil.isBlank(traceId)) {
                traceId = TraceUtils.generateTraceId();
            }
            
            // 设置追踪ID到MDC
            TraceUtils.setTraceId(traceId);
            
            // 将追踪ID添加到响应头
            httpResponse.setHeader(TRACE_ID_HEADER, traceId);
            
            log.debug("请求开始 - TraceId: {}, URI: {}, Method: {}", 
                     traceId, httpRequest.getRequestURI(), httpRequest.getMethod());
            
            // 继续执行过滤器链
            chain.doFilter(request, response);
            
        } finally {
            // 清理MDC
            TraceUtils.clearAll();
            log.debug("请求结束 - TraceId: {}", TraceUtils.getTraceId());
        }
    }
}
