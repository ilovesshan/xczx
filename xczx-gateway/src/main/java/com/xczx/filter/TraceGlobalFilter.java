package com.xczx.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: ilovesshan
 * @date: 2023/5/5
 * @description:
 */

@Slf4j
@Component
public class TraceGlobalFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 生成日志ID和请求时间
        String traceID = UUID.randomUUID().toString().replaceAll("-", "");
        String traceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());

        // 将Trace信息存储到Slf4j中，配合日志文件模板打印
        MDC.put("traceID", traceID);
        MDC.put("traceTime", traceTime);

        ServerHttpRequest httpRequest = exchange.getRequest().mutate()
                .header("traceID", traceID)
                .header("traceTime", traceTime)
                .build();

        // 将Trace信息传递给下一个微服务
        return chain.filter(exchange.mutate().request(httpRequest).build());
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
