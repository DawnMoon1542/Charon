package com.dawnmoon.springboot_app_template.common.log;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

/**
 * TraceId 过滤器
 * 为每个HTTP请求生成唯一的traceId，用于链路追踪
 * 
 * 功能：
 * 1. 为每个请求生成唯一的traceId
 * 2. 将traceId存入MDC（Mapped Diagnostic Context），便于日志输出
 * 3. 记录请求耗时统计
 * 4. 在请求结束时清理MDC，避免内存泄漏
 * 
 * @author DawnMoon
 * @since 2025-10-22
 */
@Slf4j
@Component
@Order(1)  // 确保该过滤器优先执行
public class TraceIdFilter implements Filter {

    private static final String TRACE_ID = "traceId";
    private static final String TRACE_ID_HEADER = "X-Trace-Id";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // 1. 从请求头获取 traceId，如果不存在则生成新的
        String traceId = httpRequest.getHeader(TRACE_ID_HEADER);
        if (traceId == null || traceId.isEmpty()) {
            traceId = generateTraceId();
        }

        // 2. 设置到 MDC，后续所有日志都会包含这个 traceId
        MDC.put(TRACE_ID, traceId);

        // 3. 记录收到请求的日志
        String queryString = httpRequest.getQueryString();
        log.info("收到请求: {} {} {}",
                httpRequest.getMethod(),
                httpRequest.getRequestURI(),
                queryString != null ? "?" + queryString : "");

        // 4. 记录请求开始时间
        long startTime = System.currentTimeMillis();

        try {
            // 5. 执行请求链
            chain.doFilter(request, response);
        } finally {
            // 6. 计算请求耗时
            long duration = System.currentTimeMillis() - startTime;

            // 7. 记录请求完成日志（包含方法、URI、耗时）
            log.info("请求完成: {} {} {} - 耗时: {}ms",
                    httpRequest.getMethod(),
                    httpRequest.getRequestURI(),
                    queryString != null ? "?" + queryString : "",
                    duration);

            // 8. 清除 MDC，避免内存泄漏（特别是在使用线程池的情况下）
            MDC.remove(TRACE_ID);
        }
    }

    /**
     * 生成唯一的 TraceId
     * 使用UUID去掉横线作为traceId，保证全局唯一
     * 
     * @return traceId字符串
     */
    private String generateTraceId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

